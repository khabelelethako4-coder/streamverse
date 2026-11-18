package com.streamverse.model;

import java.util.List;

/** A labelled group of movies (one genre = one scrolling row on the home screen). */
public record Category(String name, String tagline, List<Movie> movies) {
    public Category {
        movies = List.copyOf(movies);
    }
}
