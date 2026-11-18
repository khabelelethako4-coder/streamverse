package com.streamverse.ui;

import com.streamverse.library.UserLibrary;
import com.streamverse.model.Movie;
import com.streamverse.ui.controls.Buttons;
import com.streamverse.ui.controls.ButtonPictures;
import com.streamverse.ui.controls.Chip;
import com.streamverse.ui.controls.Icon;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/** The My List page: everything you saved with the heart, sortable, with a friendly empty state. */
public class MyListPage extends ScrollPage {
    private enum Sort { RECENT, AZ, NEWEST, OLDEST }

    private final UserLibrary library = UserLibrary.get();
    private final Map<String, Movie> byKey;
    private final MovieGrid grid;
    private final Label count = subtitle("");
    private final FlowPane sortRow = new FlowPane(10, 10);
    private final VBox empty = new VBox(14);
    private final List<Chip> chips = new ArrayList<>();
    private final ListChangeListener<String> listener = c -> refresh();
    private Sort sort = Sort.RECENT;

    public MyListPage(Map<String, Movie> byKey, Consumer<Movie> onPlay, Runnable onBrowse) {
        this.byKey = byKey;
        grid = new MovieGrid(onPlay);

        String[] names = {"Recently added", "A \u2013 Z", "Newest first", "Oldest first"};
        Sort[] sorts = Sort.values();
        for (int i = 0; i < names.length; i++) {
            Sort s = sorts[i];
            Chip chip = new Chip(names[i], () -> { sort = s; refresh(); });
            chips.add(chip);
            sortRow.getChildren().add(chip);
        }

        Icon bigHeart = new Icon(Icon.HEART_OUTLINE, 72, Color.rgb(255, 255, 255, 0.55));
        Label none = new Label("Your list is empty");
        none.setFont(Theme.font(FontWeight.BOLD, 26));
        none.setTextFill(Color.WHITE);
        Label how = subtitle("Tap the heart on any movie to save it here. Your list is kept on this computer.");
        how.setTextAlignment(TextAlignment.CENTER);
        empty.getChildren().addAll(bigHeart, none, how, Buttons.browseMovies(onBrowse));
        empty.setAlignment(Pos.CENTER);
        empty.setPadding(new javafx.geometry.Insets(70, 0, 70, 0));

        content.getChildren().addAll(heading("My List"), count, sortRow, grid, empty);
        library.favorites().addListener(listener);
        refresh();
    }

    private void refresh() {
        List<Movie> movies = new ArrayList<>();
        for (String key : library.favorites()) {
            Movie m = byKey.get(key);
            if (m != null) movies.add(m);
        }
        switch (sort) {
            case AZ -> movies.sort(Comparator.comparing(Movie::title));
            case NEWEST -> movies.sort(Comparator.comparingInt(Movie::year).reversed());
            case OLDEST -> movies.sort(Comparator.comparingInt(Movie::year));
            default -> { /* library order = most recently added first */ }
        }
        for (int i = 0; i < chips.size(); i++) chips.get(i).setSelected(Sort.values()[i] == sort);
        boolean any = !movies.isEmpty();
        grid.setMovies(movies);
        count.setText(any ? movies.size() + (movies.size() == 1 ? " title" : " titles") + " saved" : "Nothing saved yet");
        sortRow.setVisible(any); sortRow.setManaged(any);
        grid.setVisible(any); grid.setManaged(any);
        empty.setVisible(!any); empty.setManaged(!any);
    }

    @Override public void onShow() { refresh(); scrollToTop(); }

    public void dispose() { library.favorites().removeListener(listener); }
}
