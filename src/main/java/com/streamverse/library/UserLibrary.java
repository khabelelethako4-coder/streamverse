package com.streamverse.library;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Everything personal the viewer builds up, saved in {user home}/.streamverse/library.txt:
 *   F:id  = My List           R:id = Continue Watching       D:id = downloaded
 *   N:slug = "remind me" for a coming-soon movie           P:id:seconds:duration = how far you watched
 * All lists are ObservableLists, so every page just listens and redraws. I/O errors are swallowed on purpose.
 */
public final class UserLibrary {
    private static final UserLibrary INSTANCE = new UserLibrary();
    private static final int MAX_RECENT = 12;
    private static final Pattern LIST_LINE = Pattern.compile("([FRDN]):([A-Za-z0-9_-]{1,48})");
    private static final Pattern PROGRESS_LINE = Pattern.compile("P:([A-Za-z0-9_-]{1,48}):(\\d+(?:\\.\\d+)?):(\\d+(?:\\.\\d+)?)");

    private final ObservableList<String> favorites = FXCollections.observableArrayList();
    private final ObservableList<String> recents = FXCollections.observableArrayList();
    private final ObservableList<String> downloads = FXCollections.observableArrayList();
    private final ObservableList<String> reminders = FXCollections.observableArrayList();
    private final Map<String, double[]> progress = new HashMap<>();       // key -> {seconds, duration}
    private final IntegerProperty progressVersion = new SimpleIntegerProperty();
    private final Path file = Paths.get(System.getProperty("user.home"), ".streamverse", "library.txt");

    private UserLibrary() { load(); }

    public static UserLibrary get() { return INSTANCE; }

    public ObservableList<String> favorites() { return favorites; }
    public ObservableList<String> recents() { return recents; }
    public ObservableList<String> downloads() { return downloads; }
    public ObservableList<String> reminders() { return reminders; }
    /** Changes every time any watch progress is saved or cleared. */
    public IntegerProperty progressVersion() { return progressVersion; }

    // ---------------------------------------------------------------- My List
    public boolean isFavorite(String key) { return favorites.contains(key); }

    /** @return true if the movie is now in the list, false if it was just removed */
    public boolean toggleFavorite(String key) {
        boolean now;
        if (favorites.remove(key)) now = false; else { favorites.add(0, key); now = true; }
        save();
        return now;
    }

    // ---------------------------------------------------------------- recents
    public void addRecent(String key) {
        recents.remove(key);
        recents.add(0, key);
        while (recents.size() > MAX_RECENT) recents.remove(recents.size() - 1);
        save();
    }

    // ---------------------------------------------------------------- downloads
    public boolean isDownloaded(String key) { return downloads.contains(key); }

    public void addDownload(String key) {
        if (!downloads.contains(key)) { downloads.add(0, key); save(); }
    }

    public void removeDownload(String key) {
        if (downloads.remove(key)) save();
    }

    // ---------------------------------------------------------------- reminders
    public boolean hasReminder(String slug) { return reminders.contains(slug); }

    /** @return true if a reminder is now set */
    public boolean toggleReminder(String slug) {
        boolean now;
        if (reminders.remove(slug)) now = false; else { reminders.add(slug); now = true; }
        save();
        return now;
    }

    // ---------------------------------------------------------------- watch progress
    /** Remember where the viewer stopped. Finished movies (95 %+) and tiny positions are cleared instead. */
    public void saveProgress(String key, double seconds, double duration) {
        if (duration < 20) return;
        if (seconds / duration > 0.95 || seconds < 5) progress.remove(key);
        else progress.put(key, new double[]{seconds, duration});
        progressVersion.set(progressVersion.get() + 1);
        save();
    }

    /** 0..1, or 0 if nothing saved. */
    public double progressFraction(String key) {
        double[] p = progress.get(key);
        return p == null ? 0 : Math.min(1, p[0] / p[1]);
    }

    /** Seconds to resume from (0 = start). */
    public double resumeSeconds(String key) {
        double[] p = progress.get(key);
        return p == null ? 0 : Math.max(0, p[0] - 3);          // step back 3 s so you don't miss a line
    }

    // ---------------------------------------------------------------- file
    private void load() {
        try {
            if (!Files.exists(file)) return;
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                line = line.trim();
                Matcher m = LIST_LINE.matcher(line);
                if (m.matches()) {
                    ObservableList<String> target = switch (m.group(1)) {
                        case "F" -> favorites; case "R" -> recents; case "D" -> downloads; default -> reminders;
                    };
                    if (!target.contains(m.group(2))) target.add(m.group(2));
                    continue;
                }
                m = PROGRESS_LINE.matcher(line);
                if (m.matches()) {
                    progress.put(m.group(1), new double[]{Double.parseDouble(m.group(2)), Double.parseDouble(m.group(3))});
                }
            }
        } catch (IOException | RuntimeException e) {
            System.err.println("[library] could not read " + file + ": " + e);
        }
    }

    private void save() {
        try {
            Files.createDirectories(file.getParent());
            List<String> lines = new ArrayList<>();
            favorites.forEach(id -> lines.add("F:" + id));
            recents.forEach(id -> lines.add("R:" + id));
            downloads.forEach(id -> lines.add("D:" + id));
            reminders.forEach(id -> lines.add("N:" + id));
            progress.forEach((k, v) -> lines.add(String.format(Locale.ROOT, "P:%s:%.1f:%.1f", k, v[0], v[1])));
            Files.write(file, lines, StandardCharsets.UTF_8);
        } catch (IOException | RuntimeException e) {
            System.err.println("[library] could not save " + file + ": " + e);
        }
    }
}
