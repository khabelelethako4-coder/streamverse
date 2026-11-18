package com.streamverse.model;

import com.streamverse.net.TrailerResolver;

/**
 * Immutable description of one movie. Only the YouTube video id is stored:
 * thumbnails and the trailer itself are streamed from YouTube at run time, so
 * the project folder contains no video or image assets.
 */
public record Movie(String title, int year, String rating, String genre,
                    String description, String youtubeId) {

    /** Where pictures come from (a -Dstreamverse.imageHost=... override exists only for offline testing). */
    public static final String IMAGE_HOST = System.getProperty("streamverse.imageHost", "https://img.youtube.com");

    /** The id this movie was registered with. Never changes, so saved lists (My List, downloads, progress) stay valid. */
    public String key() { return youtubeId; }

    /** The id actually used for pictures and playback: the registered id, or its replacement if the app had to repair it. */
    public String videoId() { return TrailerResolver.get().effective(youtubeId); }

    /** 480x360 thumbnail (16:9 picture with black bars above and below). */
    public String thumbnailUrl() {
        return IMAGE_HOST + "/vi/" + videoId() + "/hqdefault.jpg";
    }

    /** 1280x720 thumbnail, used by the hero banner (not available for every video). */
    public String backdropUrl() {
        return IMAGE_HOST + "/vi/" + videoId() + "/maxresdefault.jpg";
    }

    /** Normal YouTube page, used by the "Watch on YouTube" fallback button. */
    public String watchUrl() {
        return "https://www.youtube.com/watch?v=" + videoId();
    }

    /** "2010  •  PG-13" style line shown under each thumbnail. */
    public String metaLine() {
        return year + "  \u2022  " + rating;
    }
}
