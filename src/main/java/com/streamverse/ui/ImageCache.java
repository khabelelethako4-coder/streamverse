package com.streamverse.ui;

import com.streamverse.downloads.DownloadService;
import com.streamverse.model.Movie;
import com.streamverse.net.TrailerResolver;
import javafx.scene.image.Image;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads pictures in the background once and re-uses them.
 *  - A downloaded movie is read from the saved file (so it also shows offline).
 *  - Otherwise the picture comes from its YouTube URL (Movie.thumbnailUrl / backdropUrl). If that URL fails to load
 *    because the movie's code is dead, TrailerResolver finds a working trailer and the cards reload by themselves.
 */
public final class ImageCache {
    private static final Map<String, Image> CACHE = new ConcurrentHashMap<>();

    private ImageCache() { }

    public static Image thumbnail(Movie m) {
        Path local = DownloadService.get().localThumb(m);
        return local != null ? load(local.toUri().toString(), null) : load(m.thumbnailUrl(), m);
    }

    public static Image backdrop(Movie m) {
        Path local = DownloadService.get().localBanner(m);
        return local != null ? load(local.toUri().toString(), null) : load(m.backdropUrl(), null);
    }

    /** @param repairFor the movie to repair if this (remote) picture cannot be loaded, or null */
    private static Image load(String url, Movie repairFor) {
        return CACHE.computeIfAbsent(url, u -> {
            // backgroundLoading = true -> the UI never freezes while the network is slow
            Image img = new Image(u, 0, 0, true, true, true);
            if (repairFor != null) {
                img.errorProperty().addListener((o, was, error) -> { if (error) TrailerResolver.get().repair(repairFor); });
                if (img.isError()) TrailerResolver.get().repair(repairFor);
            }
            return img;
        });
    }
}
