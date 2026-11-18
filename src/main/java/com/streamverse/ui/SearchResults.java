package com.streamverse.ui;

import com.streamverse.model.Movie;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.FontWeight;

import java.util.List;
import java.util.function.Consumer;

/** What the Home page shows while searching: ONE de-duplicated grid with every matching movie, best match first. */
public class SearchResults extends VBox {
    private final Label heading = new Label();
    private final Label hint = new Label();
    private final MovieGrid grid;

    public SearchResults(Consumer<Movie> onPlay) {
        super(10);
        grid = new MovieGrid(onPlay);
        setPadding(new Insets(100, Theme.SIDE, 40, Theme.SIDE));
        setVisible(false);
        setManaged(false);
        heading.setFont(Theme.font(FontWeight.BOLD, 24));
        heading.setTextFill(javafx.scene.paint.Color.WHITE);
        hint.setFont(Theme.font(FontWeight.NORMAL, 14));
        hint.setTextFill(Theme.MUTED);
        hint.setWrapText(true);
        VBox.setMargin(grid, new Insets(14, 0, 0, 0));
        getChildren().addAll(heading, hint, grid);
    }

    public void show(String query, List<Movie> matches) {
        grid.setMovies(matches);
        if (matches.isEmpty()) {
            heading.setText("No titles match \u201C" + query + "\u201D");
            hint.setText("Try a movie title, a genre (comedy, horror, musicals...), a year (2019), a rating (PG-13) "
                    + "or a keyword from the story (batman, space, love, war).");
        } else {
            heading.setText(matches.size() + (matches.size() == 1 ? " title" : " titles") + " for \u201C" + query + "\u201D");
            hint.setText("Matches in titles, genres, years, ratings and descriptions. Click a picture to watch the trailer.");
        }
    }
}
