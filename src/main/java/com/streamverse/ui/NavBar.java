package com.streamverse.ui;

import com.streamverse.audio.SoundManager;
import com.streamverse.ui.controls.CustomButton;
import com.streamverse.ui.controls.CustomButton.Style;
import com.streamverse.ui.controls.Icon;
import com.streamverse.ui.controls.NavLink;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** Fixed top bar: logo, genre links (scroll-spy highlighted), expanding search box, sound toggle. */
public class NavBar extends StackPane {
    private static final double SEARCH_CLOSED = 40, SEARCH_OPEN = 230;

    private final List<NavLink> links = new ArrayList<>();
    private final Region solid = new Region();
    private final HBox searchBox = new HBox();
    private final TextField field = new TextField();
    private boolean scrolled;
    private final NavLink genresLink;

    public NavBar(Consumer<String> onNavigate, Runnable onGenres, Consumer<String> onSearch) {
        setPickOnBounds(false);
        setMaxHeight(USE_PREF_SIZE);

        Region gradient = new Region();
        gradient.setBackground(new Background(new BackgroundFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(0, 0, 0, 0.85)), new Stop(1, Color.rgb(0, 0, 0, 0))), CornerRadii.EMPTY, Insets.EMPTY)));
        gradient.setMouseTransparent(true);
        solid.setBackground(new Background(new BackgroundFill(Theme.alpha(Backdrop.TINT, 0.95), CornerRadii.EMPTY, Insets.EMPTY)));
        solid.setEffect(new DropShadow(14, Color.rgb(0, 0, 0, 0.7)));
        solid.setOpacity(0);
        solid.setMouseTransparent(true);

        // ---- logo
        Label logo = new Label("STREAMVERSE");
        logo.setFont(Theme.font(FontWeight.EXTRA_BOLD, 27));
        logo.setTextFill(Theme.ACCENT);
        logo.setEffect(new DropShadow(10, Color.rgb(229, 9, 20, 0.45)));
        logo.setCursor(Cursor.HAND);
        logo.setMinWidth(Region.USE_PREF_SIZE);
        logo.setOnMouseClicked(e -> { SoundManager.get().play(SoundManager.Sfx.NAV); onNavigate.accept("Home"); });

        // ---- links
        HBox linkBox = new HBox(16);
        linkBox.setAlignment(Pos.CENTER);
        linkBox.setMinWidth(Region.USE_PREF_SIZE);        // never ellipsize the links when the search box opens
        for (String s : List.of("Home", "Coming Soon", "My List", "Downloads")) {
            NavLink l = new NavLink(s, () -> onNavigate.accept(s));
            l.setMinWidth(Region.USE_PREF_SIZE);
            links.add(l);
            linkBox.getChildren().add(l);
        }
        genresLink = new NavLink("Genres", Icon.DROP_DOWN, onGenres);
        genresLink.setMinWidth(Region.USE_PREF_SIZE);
        links.add(genresLink);
        linkBox.getChildren().add(genresLink);
        setActive("Home");

        // ---- search (collapses to an icon, expands on click/focus)
        Icon searchIcon = new Icon(Icon.SEARCH, 22, Color.WHITE);
        Region iconHolder = new Region();
        StackPane iconPane = new StackPane(searchIcon);
        iconPane.setPrefSize(38, 36);
        iconPane.setMinSize(38, 36);
        iconPane.setCursor(Cursor.HAND);
        iconPane.setOnMouseClicked(e -> { SoundManager.get().play(SoundManager.Sfx.CLICK); field.requestFocus(); });
        field.getStyleClass().add("search-field");
        field.setPromptText("Search movies, genres\u2026");
        field.setOnKeyPressed(e -> { if (e.getCode() == javafx.scene.input.KeyCode.ESCAPE) { field.clear(); searchBox.requestFocus(); } });
        HBox.setHgrow(field, Priority.ALWAYS);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.getChildren().addAll(iconPane, field);
        searchBox.setPrefWidth(SEARCH_CLOSED);
        searchBox.setMinWidth(SEARCH_CLOSED);
        searchBox.setMaxWidth(SEARCH_CLOSED);
        searchBox.setPrefHeight(36);
        searchBox.setMaxHeight(36);
        field.setOpacity(0);
        styleSearch(false);
        field.focusedProperty().addListener((o, a, focused) -> expandSearch(focused || !field.getText().isEmpty()));
        field.textProperty().addListener((o, a, text) -> { onSearch.accept(text.trim()); expandSearch(true); });

        // ---- sound toggle
        CustomButton sound = new CustomButton(null, Icon.VOLUME, Style.ICON);
        sound.setOnAction(() -> {
            boolean on = !SoundManager.get().enabledProperty().get();
            SoundManager.get().enabledProperty().set(on);
            sound.setIconPath(on ? Icon.VOLUME : Icon.MUTE);
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox bar = new HBox(28, logo, linkBox, spacer, searchBox, sound);
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(14, Theme.SIDE, 18, Theme.SIDE));

        getChildren().addAll(gradient, solid, bar);
    }

    public NavLink getGenresLink() { return genresLink; }

    /** Empties the search box (which also leaves search mode). */
    public void clearSearch() { if (!field.getText().isEmpty()) field.clear(); }

    public void setActive(String name) {
        for (NavLink l : links) l.setActive(l.getText().equals(name));
    }

    /** Fade in a solid background once the page has been scrolled. */
    public void setScrolled(boolean value) {
        if (scrolled == value) return;
        scrolled = value;
        FadeTransition ft = new FadeTransition(Duration.millis(220), solid);
        ft.setToValue(value ? 1 : 0);
        ft.play();
    }

    private void expandSearch(boolean open) {
        double target = open ? SEARCH_OPEN : SEARCH_CLOSED;
        if (searchBox.getPrefWidth() == target) return;
        styleSearch(open);
        new Timeline(new KeyFrame(Duration.millis(220),
                new KeyValue(searchBox.prefWidthProperty(), target, Interpolator.EASE_BOTH),
                new KeyValue(searchBox.minWidthProperty(), target, Interpolator.EASE_BOTH),
                new KeyValue(searchBox.maxWidthProperty(), target, Interpolator.EASE_BOTH),
                new KeyValue(field.opacityProperty(), open ? 1 : 0))).play();
    }

    private void styleSearch(boolean open) {
        searchBox.setStyle(open
                ? "-fx-background-color: rgba(0,0,0,0.75); -fx-border-color: rgba(255,255,255,0.85); -fx-border-width: 1;"
                : "-fx-background-color: transparent; -fx-border-color: transparent; -fx-border-width: 1;");
    }
}
