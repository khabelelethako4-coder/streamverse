package com.streamverse.data;

import com.streamverse.model.Category;
import com.streamverse.model.Movie;
import com.streamverse.model.Upcoming;

import java.util.List;

/**
 * Source of the catalogue. The UI only depends on this interface, so the
 * in-memory sample data could later be replaced by a JSON file or a web API
 * without touching any UI code (dependency inversion).
 */
public interface MovieRepository {
    /** The genre rows, in display order. */
    List<Category> getCategories();

    /** Movies shown, in rotation, in the big hero banner (also used for the "Trending Now" row). */
    List<Movie> getFeatured();

    /** Every movie in the catalogue (used to resolve saved ids back into movies). */
    List<Movie> getAllMovies();

    /** Movies that are not out yet, sorted by release date (the Coming Soon page). */
    List<Upcoming> getUpcoming();

    /** The movie after {@code current} in its genre (wraps around) - powers the player's "Next trailer" button. */
    Movie nextInGenre(Movie current);
}
