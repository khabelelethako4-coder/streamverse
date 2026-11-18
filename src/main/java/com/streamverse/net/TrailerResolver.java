package com.streamverse.net;

import com.streamverse.model.Movie;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Self-healing trailers.  Every movie is registered with one YouTube code; if that code is wrong, removed, or the owner
 * disabled embedding, this class finds a WORKING official trailer by itself:
 *
 *   1. health(id)   asks YouTube's public oEmbed endpoint: 200 = exists and may be embedded, 401/404 = broken.
 *   2. search(...)  reads the first page of YouTube search results for "<title> <year> official trailer".
 *   3. pickBest()   checks the candidates with health() and keeps the one whose title says "trailer" and contains the movie name.
 *
 * The replacement is remembered in {user home}/.streamverse/trailer_fixes.txt, so the repair happens once.
 * Movie.videoId() asks {@link #effective(String)}, so pictures and the player pick the repaired code automatically.
 * It also finds trailers for coming-soon movies, which have no code at all ({@link #findTrailer}).
 * No API key is needed; if the computer is offline nothing is changed.
 */
public final class TrailerResolver {

    public enum State { OK, BROKEN, OFFLINE }

    private record Health(State state, String title) { }

    /** Result of {@link #repairAll}. */
    public record Report(int checked, int broken, int fixed, boolean offline) { }

    @FunctionalInterface
    public interface Progress { void update(int done, int total, String message); }

    /** YouTube address (a -Dstreamverse.ytHost=... override exists only for offline testing). */
    private static final String YT_HOST = System.getProperty("streamverse.ytHost", "https://www.youtube.com");
    private static final TrailerResolver INSTANCE = new TrailerResolver();
    private static final String UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0 Safari/537.36";
    private static final Pattern VIDEO_ID = Pattern.compile("\"videoId\":\"([A-Za-z0-9_-]{11})\"");
    private static final Pattern TITLE = Pattern.compile("\"title\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"");
    private static final Pattern FIX_LINE = Pattern.compile("([A-Za-z0-9_-]{1,48})=([A-Za-z0-9_-]{11})");
    private static final Pattern BAD_WORDS = Pattern.compile(
            "reaction|review|explained|breakdown|ending|scene|clip|fan[ -]?made|parody|recap|analysis|behind the scenes|full movie|soundtrack|theory");
    private static final Set<String> STOP = Set.of("the", "a", "an", "of", "and", "in", "to", "part", "vol");

    private final Map<String, String> fixes = new ConcurrentHashMap<>();            // registered key -> working id
    private final Map<String, CompletableFuture<Optional<String>>> inFlight = new ConcurrentHashMap<>();
    private final Map<String, Long> failedAt = new ConcurrentHashMap<>();
    private final ExecutorService worker = daemonPool(1, "trailer-worker");        // searches run one at a time (polite)
    private final ExecutorService checkers = daemonPool(6, "trailer-check");       // health checks run in parallel
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8))
            .followRedirects(HttpClient.Redirect.NORMAL).build();
    private final IntegerProperty fixVersion = new SimpleIntegerProperty();
    private final Path file = Paths.get(System.getProperty("user.home"), ".streamverse", "trailer_fixes.txt");

    private TrailerResolver() { loadFixes(); }

    public static TrailerResolver get() { return INSTANCE; }

    /** The code to really use for a registered key (the repaired one if there is one). */
    public String effective(String key) { return fixes.getOrDefault(key, key); }

    /** Increases each time a repair is stored - cards listen to this to reload their picture. */
    public IntegerProperty fixVersionProperty() { return fixVersion; }

    // ================================================================== repairing registered movies

    /** Checks the movie's current code; if (and only if) it is broken, finds and stores a replacement. */
    public CompletableFuture<Optional<String>> repair(Movie movie) {
        String key = movie.key();
        CompletableFuture<Optional<String>> running = inFlight.get(key);
        if (running != null) return running;
        CompletableFuture<Optional<String>> job = CompletableFuture.supplyAsync(() -> doRepair(movie), worker);
        inFlight.put(key, job);
        job.whenComplete((r, e) -> inFlight.remove(key));
        return job;
    }

    private Optional<String> doRepair(Movie movie) {
        String key = movie.key();
        if (System.currentTimeMillis() - failedAt.getOrDefault(key, 0L) < 10 * 60_000L) return Optional.empty();
        if (health(effective(key)).state() != State.BROKEN) return Optional.empty();     // fine, or we are offline
        Optional<String> found = findBest(movie.title(), movie.year());
        if (found.isPresent()) {
            fixes.put(key, found.get());
            saveFixes();
            bump();
        } else {
            failedAt.put(key, System.currentTimeMillis());
        }
        return found;
    }

    /** Checks every movie (6 at a time) and repairs the broken ones. Runs in the background; progress arrives off the UI thread. */
    public CompletableFuture<Report> repairAll(List<Movie> movies, Progress progress) {
        return CompletableFuture.supplyAsync(() -> {
            int total = movies.size();
            AtomicInteger done = new AtomicInteger(), offline = new AtomicInteger();
            List<Movie> bad = java.util.Collections.synchronizedList(new ArrayList<>());
            List<Future<?>> jobs = new ArrayList<>();
            for (Movie m : movies) {
                jobs.add(checkers.submit(() -> {
                    Health h = health(effective(m.key()));
                    if (h.state() == State.BROKEN) bad.add(m);
                    else if (h.state() == State.OFFLINE) offline.incrementAndGet();
                    progress.update(done.incrementAndGet(), total, "Checking trailers");
                }));
            }
            for (Future<?> f : jobs) {
                try { f.get(); } catch (Exception ignored) { }
            }
            if (offline.get() > total / 2) return new Report(total, 0, 0, true);

            int fixed = 0, n = 0;
            for (Movie m : bad) {
                progress.update(n++, bad.size(), "Repairing " + m.title());
                Optional<String> found = findBest(m.title(), m.year());
                if (found.isPresent()) { fixes.put(m.key(), found.get()); fixed++; }
            }
            saveFixes();
            if (fixed > 0) bump();
            return new Report(total, bad.size(), fixed, false);
        }, worker);
    }

    // ================================================================== trailers for coming-soon movies

    /** Finds (and remembers) an embeddable trailer/teaser for a movie that has no code yet. Empty if offline/not found. */
    public CompletableFuture<Optional<String>> findTrailer(String slug, String title, int year) {
        String cacheKey = "u_" + slug;
        String cached = fixes.get(cacheKey);
        if (cached != null) return CompletableFuture.completedFuture(Optional.of(cached));
        CompletableFuture<Optional<String>> running = inFlight.get(cacheKey);
        if (running != null) return running;
        CompletableFuture<Optional<String>> job = CompletableFuture.supplyAsync(() -> {
            Optional<String> found = findBest(title, year);
            found.ifPresent(id -> { fixes.put(cacheKey, id); saveFixes(); });
            return found;
        }, worker);
        inFlight.put(cacheKey, job);
        job.whenComplete((r, e) -> inFlight.remove(cacheKey));
        return job;
    }

    // ================================================================== search + validation

    private Optional<String> findBest(String title, int year) {
        String clean = title.replaceAll("\\s*\\(.*?\\)", "").trim();
        for (String query : new String[]{clean + " " + year + " official trailer", clean + " official trailer"}) {
            Optional<String> best = pickBest(clean, search(query));
            if (best.isPresent()) return best;
        }
        return Optional.empty();
    }

    private Optional<String> pickBest(String movieTitle, List<String> candidates) {
        List<String> words = new ArrayList<>();
        for (String w : normalize(movieTitle).split(" ")) if (!w.isEmpty() && !STOP.contains(w)) words.add(w);
        String bestId = null;
        int bestScore = 0, checked = 0;
        for (String id : candidates) {
            if (checked++ >= 8) break;
            Health h = health(id);
            if (h.state() == State.OFFLINE) return Optional.empty();
            if (h.state() != State.OK) continue;
            String t = h.title().toLowerCase(Locale.ROOT), n = normalize(h.title());
            int score = 0;
            if (t.contains("trailer") || t.contains("teaser")) score += 3; else continue;
            boolean named = true;
            for (String w : words) if (!n.contains(w)) { named = false; break; }
            if (!named) continue;
            score += 3;
            if (t.contains("official")) score += 1;
            if (BAD_WORDS.matcher(t).find()) score -= 5;
            if (score > bestScore) { bestScore = score; bestId = id; }
            if (score >= 7) break;
        }
        return bestScore >= 5 ? Optional.of(bestId) : Optional.empty();
    }

    /** oEmbed: 200 = real and embeddable; 400/401/403/404 = broken; anything else (or no network) = we cannot tell. */
    private Health health(String id) {
        try {
            String url = YT_HOST + "/oembed?format=json&url="
                    + URLEncoder.encode("https://www.youtube.com/watch?v=" + id, StandardCharsets.UTF_8);
            HttpResponse<String> r = http.send(HttpRequest.newBuilder(URI.create(url)).header("User-Agent", UA)
                    .timeout(Duration.ofSeconds(8)).GET().build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            int c = r.statusCode();
            if (c == 200) {
                Matcher m = TITLE.matcher(r.body());
                return new Health(State.OK, m.find() ? unescape(m.group(1)) : "");
            }
            if (c == 400 || c == 401 || c == 403 || c == 404) return new Health(State.BROKEN, "");
            return new Health(State.OFFLINE, "");
        } catch (IOException e) {
            return new Health(State.OFFLINE, "");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new Health(State.OFFLINE, "");
        }
    }

    /** First ~12 distinct video ids of a YouTube search results page. */
    private List<String> search(String query) {
        try {
            String url = YT_HOST + "/results?hl=en&search_query=" + URLEncoder.encode(query, StandardCharsets.UTF_8);
            HttpResponse<String> r = http.send(HttpRequest.newBuilder(URI.create(url)).header("User-Agent", UA)
                    .header("Accept-Language", "en-US,en;q=0.9").header("Cookie", "CONSENT=YES+1; SOCS=CAI")
                    .timeout(Duration.ofSeconds(12)).GET().build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            Set<String> ids = new LinkedHashSet<>();
            Matcher m = VIDEO_ID.matcher(r.body());
            while (m.find() && ids.size() < 12) ids.add(m.group(1));
            return new ArrayList<>(ids);
        } catch (IOException e) {
            return List.of();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return List.of();
        }
    }

    // ================================================================== helpers

    private static String normalize(String s) {
        return s.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9 ]", " ").replaceAll("\\s+", " ").trim();
    }

    private static String unescape(String s) {
        return s.replace("\\u0026", "&").replace("\\\"", "\"").replace("\\/", "/").replace("\\u0027", "'").replace("\\\\", "\\");
    }

    private void bump() {
        try {
            Platform.runLater(() -> fixVersion.set(fixVersion.get() + 1));
        } catch (IllegalStateException e) {
            fixVersion.set(fixVersion.get() + 1);
        }
    }

    private void loadFixes() {
        try {
            if (!Files.exists(file)) return;
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                Matcher m = FIX_LINE.matcher(line.trim());
                if (m.matches()) fixes.put(m.group(1), m.group(2));
            }
        } catch (IOException | RuntimeException e) {
            System.err.println("[trailers] could not read " + file + ": " + e);
        }
    }

    private synchronized void saveFixes() {
        try {
            Files.createDirectories(file.getParent());
            List<String> lines = new ArrayList<>();
            fixes.forEach((k, v) -> lines.add(k + "=" + v));
            Files.write(file, lines, StandardCharsets.UTF_8);
        } catch (IOException | RuntimeException e) {
            System.err.println("[trailers] could not save " + file + ": " + e);
        }
    }

    private static ExecutorService daemonPool(int threads, String name) {
        return Executors.newFixedThreadPool(threads, r -> {
            Thread t = new Thread(r, name);
            t.setDaemon(true);
            return t;
        });
    }
}
