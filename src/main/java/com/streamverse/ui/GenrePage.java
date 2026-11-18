package com.streamverse.ui;

import com.streamverse.model.Category;
import com.streamverse.model.Movie;
import com.streamverse.ui.controls.Chip;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** One genre as a full page: title, a row of genre chips to jump between genres, and every movie of the genre in a grid. */
public class GenrePage extends ScrollPage {
    private final List<Category> categories;
    private final List<Chip> chips = new ArrayList<>();
    private final Label title = heading("");
    private final Label tagline = subtitle("");
    private final MovieGrid grid;

    public GenrePage(List<Category> categories, Consumer<Movie> onPlay) {
        this.categories = categories;
        grid = new MovieGrid(onPlay);
        FlowPane chipRow = new FlowPane(10, 10);
        for (Category c : categories) {
            Chip chip = new Chip(c.name(), () -> showGenre(c.name()));
            chips.add(chip);
            chipRow.getChildren().add(chip);
        }
        content.getChildren().addAll(title, tagline, chipRow, grid);
        showGenre(categories.get(0).name());
    }

    public void showGenre(String name) {
        for (int i = 0; i < categories.size(); i++) {
            Category c = categories.get(i);
            boolean on = c.name().equals(name);
            chips.get(i).setSelected(on);
            if (on) {
                title.setText(c.name());
                tagline.setText(c.tagline() + "  \u2022  " + c.movies().size() + " titles");
                grid.setMovies(c.movies());
            }
        }
        scrollToTop();
    }

    @Override public void onShow() { /* keep the chosen genre */ }
}
