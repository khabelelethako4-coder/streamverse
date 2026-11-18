package com.streamverse.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** A movie that is not out yet. Its trailer is looked up on YouTube when it is needed (see TrailerResolver). */
public record Upcoming(String slug, String title, LocalDate releaseDate, String genre, String description) {

    /** Days from today until release (negative once it is out). */
    public long daysToGo() { return ChronoUnit.DAYS.between(LocalDate.now(), releaseDate); }

    /** "Today!", "Tomorrow", "In 12 days", "In 5 weeks", "In 8 months", or "Out now". */
    public String countdown() {
        long d = daysToGo();
        if (d < 0) return "Out now";
        if (d == 0) return "In cinemas today!";
        if (d == 1) return "Tomorrow";
        if (d < 14) return "In " + d + " days";
        if (d < 70) return "In " + (d / 7) + " weeks";
        return "In " + (d / 30) + " months";
    }

    /** First page of YouTube results for the trailer - the always-works fallback. */
    public String youtubeSearchUrl() {
        return "https://www.youtube.com/results?search_query="
                + java.net.URLEncoder.encode(title + " official trailer", java.nio.charset.StandardCharsets.UTF_8);
    }
}
