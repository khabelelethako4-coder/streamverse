package com.streamverse.ui;

import com.streamverse.data.MovieRepository;
import com.streamverse.library.UserLibrary;
import com.streamverse.model.Movie;
import com.streamverse.model.Upcoming;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.HostServices;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * The main window content: the stage backdrop, ONE visible page at a time (Home, Coming Soon, My List, Downloads, Genre),
 * the navigation bar on top and the Genres drop-down. Pages are created once and switched with a short fade.
 */
public class MainView extends StackPane {

    private final NavBar nav;
    private final GenreMenu genreMenu;
    private final HomePage home;
    private final GenrePage genrePage;
    private final MyListPage myListPage;
    private final DownloadsPage downloadsPage;
    private final ComingSoonPage comingSoonPage;
    private final List<ScrollPage> pages = new ArrayList<>();
    private ScrollPage current;

    public MainView(MovieRepository repo, Consumer<Movie> onPlay, HostServices hostServices) {
        Map<String, Movie> byKey = new HashMap<>();
        repo.getAllMovies().forEach(m -> byKey.put(m.key(), m));

        home = new HomePage(repo, byKey, onPlay);
        genrePage = new GenrePage(repo.getCategories(), onPlay);
        myListPage = new MyListPage(byKey, onPlay, () -> navigate("Home"));
        downloadsPage = new DownloadsPage(byKey, onPlay, () -> navigate("Home"));
        comingSoonPage = new ComingSoonPage(repo.getUpcoming(), onPlay, hostServices);
        pages.addAll(List.of(home, comingSoonPage, myListPage, downloadsPage, genrePage));

        genreMenu = new GenreMenu(repo.getCategories(), this::navigate);
        StackPane.setAlignment(genreMenu, Pos.TOP_LEFT);
        nav = new NavBar(this::navigate, this::toggleGenreMenu, this::onSearch);
        StackPane.setAlignment(nav, Pos.TOP_CENTER);

        getChildren().add(new Backdrop(0.52, 0.86));
        for (ScrollPage p : pages) {
            p.setVisible(false);
            p.scrollYProperty().addListener((o, a, b) -> { if (p == current) nav.setScrolled(b.doubleValue() > 30); });
            getChildren().add(p);
        }
        getChildren().addAll(nav, genreMenu);

        // click anywhere outside the open Genres menu closes it
        addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            if (genreMenu.isVisible() && !isInside(e.getTarget(), genreMenu) && !isInside(e.getTarget(), nav.getGenresLink())) {
                genreMenu.close();
            }
        });

        setFocusTraversable(true);
        show(home, "Home", false);
        remindAboutOpenings(repo.getUpcoming());
    }

    // ------------------------------------------------------------------ navigation

    /** Names come from the nav bar ("Home", "Coming Soon", "My List", "Downloads") or the Genres menu (a genre name). */
    private void navigate(String name) {
        genreMenu.close();
        nav.clearSearch();
        switch (name) {
            case "Home" -> show(home, "Home", true);
            case "Coming Soon" -> show(comingSoonPage, "Coming Soon", true);
            case "My List" -> show(myListPage, "My List", true);
            case "Downloads" -> show(downloadsPage, "Downloads", true);
            default -> { genrePage.showGenre(name); show(genrePage, "Genres", true); }
        }
    }

    private void show(ScrollPage page, String navName, boolean animate) {
        boolean same = page == current;
        current = page;
        nav.setActive(navName);
        nav.setScrolled(page.scrollYProperty().get() > 30);
        for (ScrollPage p : pages) p.setVisible(p == page);
        if (same) { if (page != home) page.onShow(); else page.scrollToTop(); return; }
        page.onShow();
        javafx.application.Platform.runLater(this::requestFocus);   // keep the search box from grabbing focus when a page hides the old focus owner
        if (animate) {
            page.setOpacity(0);
            page.setTranslateY(14);
            FadeTransition fade = new FadeTransition(Duration.millis(240), page);
            fade.setToValue(1);
            TranslateTransition slide = new TranslateTransition(Duration.millis(240), page);
            slide.setToY(0);
            fade.play();
            slide.play();
        } else {
            page.setOpacity(1);
            page.setTranslateY(0);
        }
    }

    /** Typing in the search box always shows the Home page's results grid. */
    private void onSearch(String text) {
        if (text != null && !text.isBlank() && current != home) show(home, "Home", true);
        home.onSearchText(text);
    }

    private void toggleGenreMenu() {
        if (!genreMenu.isVisible()) {
            double linkX = nav.getGenresLink().localToScene(0, 0).getX() - getScene().getX() - 14;
            StackPane.setMargin(genreMenu, new Insets(62, 0, 0, Math.max(10, linkX)));
        }
        genreMenu.toggle();
    }

    private static boolean isInside(Object target, Node container) {
        for (Node n = target instanceof Node ? (Node) target : null; n != null; n = n.getParent()) {
            if (n == container) return true;
        }
        return false;
    }

    // ------------------------------------------------------------------ reminders

    /** A few seconds after start-up: tell the viewer about films they set a bell on that open within a week. */
    private void remindAboutOpenings(List<Upcoming> upcoming) {
        List<String> soon = new ArrayList<>();
        for (Upcoming u : upcoming) {
            if (UserLibrary.get().hasReminder(u.slug()) && u.daysToGo() >= 0 && u.daysToGo() <= 7) {
                soon.add("\u201C" + u.title() + "\u201D \u2013 " + u.countdown().toLowerCase());
            }
        }
        if (soon.isEmpty()) return;
        PauseTransition wait = new PauseTransition(Duration.millis(4200));
        wait.setOnFinished(e -> Toast.show("\uD83D\uDD14 Reminder: " + String.join("  \u2022  ", soon)));
        wait.play();
    }

    public void dispose() {
        home.dispose();
        myListPage.dispose();
        downloadsPage.dispose();
    }
}
