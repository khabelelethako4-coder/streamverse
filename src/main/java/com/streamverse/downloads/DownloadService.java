package com.streamverse.downloads;

import com.streamverse.library.UserLibrary;
import com.streamverse.model.Movie;
import com.streamverse.ui.Toast;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Duration;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

/**
 * "Downloads". IMPORTANT: the video itself is never downloaded or stored (the assignment forbids video files in the
 * project and YouTube's terms forbid saving its videos). A download saves the movie's PICTURES to
 * {user home}/.streamverse/downloads/ (the card picture + the large banner when it exists) so the movie stays visible
 * in the Downloads page even without internet. The trailer itself is always streamed from YouTube when you press play.
 */
public final class DownloadService {
    private static final DownloadService INSTANCE = new DownloadService();

    private final Path dir = Paths.get(System.getProperty("user.home"), ".streamverse", "downloads");
    private final Map<String, DoubleProperty> active = new ConcurrentHashMap<>();
    private final IntegerProperty version = new SimpleIntegerProperty();           // changes when any download starts/ends
    private final ExecutorService pool = Executors.newFixedThreadPool(3, r -> {
        Thread t = new Thread(r, "downloader");
        t.setDaemon(true);
        return t;
    });
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(java.time.Duration.ofSeconds(8))
            .followRedirects(HttpClient.Redirect.NORMAL).build();
    private final UserLibrary library = UserLibrary.get();

    private DownloadService() { }

    public static DownloadService get() { return INSTANCE; }

    /** Changes whenever a download starts, finishes, fails or is removed - cards listen to redraw their badge. */
    public IntegerProperty versionProperty() { return version; }

    /** Progress 0..1 while the movie is downloading, otherwise null. */
    public ReadOnlyDoubleProperty progressOf(Movie m) { return active.get(m.key()); }

    /** Keys of the movies being downloaded right now. */
    public java.util.Set<String> activeKeys() { return new java.util.LinkedHashSet<>(active.keySet()); }

    public boolean isDownloaded(Movie m) {
        return library.isDownloaded(m.key()) && Files.exists(thumbFile(m));
    }

    /** The saved card picture, or null if this movie is not downloaded. */
    public Path localThumb(Movie m) { return isDownloaded(m) ? thumbFile(m) : null; }

    /** The saved large picture, or null if there is none. */
    public Path localBanner(Movie m) {
        Path p = dir.resolve(m.key() + "_hd.jpg");
        return isDownloaded(m) && Files.exists(p) ? p : null;
    }

    // ------------------------------------------------------------------ commands

    public void download(Movie m) {
        String key = m.key();
        if (isDownloaded(m) || active.containsKey(key)) return;
        DoubleProperty progress = new SimpleDoubleProperty(0);
        active.put(key, progress);
        version.set(version.get() + 1);
        pool.submit(() -> {
            try {
                Files.createDirectories(dir);
                byte[] card = fetch(m.thumbnailUrl(), f -> setProgress(progress, f * 0.7));
                Files.write(thumbFile(m), card);
                try {
                    byte[] banner = fetch(m.backdropUrl(), f -> setProgress(progress, 0.7 + f * 0.3));
                    if (banner.length > 8_000) Files.write(dir.resolve(key + "_hd.jpg"), banner);   // YouTube sends a tiny grey placeholder when missing
                } catch (Exception ignored) { /* the large picture is optional */ }
                Platform.runLater(() -> finish(m, progress));
            } catch (Exception e) {
                Platform.runLater(() -> {
                    active.remove(key);
                    version.set(version.get() + 1);
                    Toast.show("Download failed \u2013 check your internet connection");
                });
            }
        });
    }

    /** Downloads every movie of the list that is not saved yet. */
    public int downloadAll(List<Movie> movies) {
        int started = 0;
        for (Movie m : movies) {
            if (!isDownloaded(m) && !active.containsKey(m.key())) { download(m); started++; }
        }
        return started;
    }

    public void remove(Movie m) {
        try {
            Files.deleteIfExists(thumbFile(m));
            Files.deleteIfExists(dir.resolve(m.key() + "_hd.jpg"));
        } catch (IOException ignored) { }
        library.removeDownload(m.key());
        version.set(version.get() + 1);
    }

    public void removeAll(List<Movie> movies) { new java.util.ArrayList<>(movies).forEach(this::remove); }

    /** Disk space used by the saved pictures. */
    public long totalBytes() {
        try (Stream<Path> files = Files.list(dir)) {
            return files.filter(Files::isRegularFile).mapToLong(p -> p.toFile().length()).sum();
        } catch (IOException | RuntimeException e) {
            return 0;
        }
    }

    public static String humanSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.0f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }

    // ------------------------------------------------------------------ internals

    private Path thumbFile(Movie m) { return dir.resolve(m.key() + ".jpg"); }

    private void setProgress(DoubleProperty p, double value) { Platform.runLater(() -> p.set(Math.max(p.get(), value))); }

    /** Eases the bar to 100 % so even a tiny, instant download visibly "fills up", then marks the movie as downloaded. */
    private void finish(Movie m, DoubleProperty progress) {
        Timeline fill = new Timeline(new KeyFrame(Duration.millis(900), new KeyValue(progress, 1.0)));
        fill.setOnFinished(e -> {
            library.addDownload(m.key());
            active.remove(m.key());
            version.set(version.get() + 1);
            Toast.show("Downloaded \u201C" + m.title() + "\u201D \u2013 saved for offline browsing");
        });
        fill.play();
    }

    private byte[] fetch(String url, java.util.function.DoubleConsumer onProgress) throws IOException, InterruptedException {
        HttpResponse<InputStream> r = http.send(HttpRequest.newBuilder(URI.create(url)).GET().build(),
                HttpResponse.BodyHandlers.ofInputStream());
        if (r.statusCode() != 200) throw new IOException("HTTP " + r.statusCode());
        long total = r.headers().firstValueAsLong("content-length").orElse(-1);
        try (InputStream in = r.body()) {
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int n;
            while ((n = in.read(buf)) > 0) {
                out.write(buf, 0, n);
                if (total > 0) onProgress.accept(Math.min(1.0, out.size() / (double) total));
            }
            onProgress.accept(1.0);
            return out.toByteArray();
        }
    }
}
