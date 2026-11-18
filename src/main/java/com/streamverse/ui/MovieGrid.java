package com.streamverse.ui;

import com.streamverse.model.Movie;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.FlowPane;

import java.util.List;
import java.util.function.Consumer;

/** A wrapping grid of movie cards, five per line at any window width. Used by Genre, My List, Downloads and Search. */
public class MovieGrid extends FlowPane {
    private static final double GAP = 14;

    private final DoubleProperty cardWidth = new SimpleDoubleProperty(240);
    private final Consumer<Movie> onPlay;

    public MovieGrid(Consumer<Movie> onPlay) {
        super(GAP, 26);
        this.onPlay = onPlay;
        prefWrapLengthProperty().bind(widthProperty());
        cardWidth.bind(Bindings.createDoubleBinding(
                () -> Math.max(150, (getWidth() - 4 * GAP - 2) / 5), widthProperty()));
    }

    /** Replaces the cards. Pictures load straight away (grids are short and the user asked for exactly these). */
    public void setMovies(List<Movie> movies) {
        getChildren().clear();
        for (Movie m : movies) {
            MovieCard card = new MovieCard(m, cardWidth, onPlay, (c, extra) -> 0);
            card.loadImage();
            getChildren().add(card);
        }
    }

    public int size() { return getChildren().size(); }
}
