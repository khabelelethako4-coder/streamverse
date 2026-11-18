package com.streamverse.ui;

import com.streamverse.data.MovieRepository;
import com.streamverse.library.UserLibrary;
import com.streamverse.model.Category;
import com.streamverse.model.Movie;
import com.streamverse.net.TrailerResolver;
import com.streamverse.ui.controls.Buttons;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

/**
 * The Home page: hero banner, then rows - Continue Watching (with progress bars), Because you watched..., My List,
 * Top 10 Today, Trending Now and one row per genre - plus the search-results grid and a footer with the trailer repair button.
 * Thumbnails load lazily: a row only downloads its pictures when it is near the screen.
 */
public class HomePage extends ScrollPage {

    private final VBox rowsBox = new VBox(10);
    private final HeroBanner hero;
    private final SearchResults results;
    private final CategoryRow recentRow, becauseRow, myListRow, topRow, trendingRow;
    private final List<CategoryRow> genreRows = new ArrayList<>();
    private final List<CategoryRow> allRows = new ArrayList<>();
    private final List<Category> categories;
    private final List<Movie> everything;
    private final Map<String, Movie> byKey;
    private final Map<Movie, String> haystack = new java.util.HashMap<>();
    private final UserLibrary library = UserLibrary.get();
    private final Consumer<Movie> onPlay;
    private final javafx.animation.PauseTransition debounce = new javafx.animation.PauseTransition(Duration.millis(220));
    private final ListChangeListener<String> listListener = c -> refreshPersonalRows();
    private final ChangeListener<Number> progressListener = (o, a, b) -> refreshPersonalRows();
    private boolean filtering;
    private boolean repairing;
    private Timeline scrollAnim;

    public HomePage(MovieRepository repo, Map<String, Movie> byKey, Consumer<Movie> onPlay) {
        super(new Insets(0));
        this.onPlay = onPlay;
        this.byKey = byKey;
        categories = repo.getCategories();
        everything = repo.getAllMovies();
        buildSearchIndex();

        hero = new HeroBanner(repo.getFeatured(), onPlay, this::browseTitles, this::surpriseMe);

        recentRow = new CategoryRow(new Category("Continue Watching", "Pick up where you left off", List.of()), onPlay);
        becauseRow = new CategoryRow(new Category("Because you watched", "More like your last movie", List.of()), onPlay);
        myListRow = new CategoryRow(new Category("My List", "Saved with the heart button", List.of()), onPlay);
        topRow = new CategoryRow(new Category("Top 10 on StreamVerse Today", "Changes every day", List.of()), onPlay).setRanked(true);
        topRow.setMovies(top10());
        trendingRow = new CategoryRow(new Category("Trending Now", "Hand-picked trailers everyone is watching", repo.getFeatured()), onPlay);
        allRows.addAll(List.of(recentRow, becauseRow, myListRow, topRow, trendingRow));
        for (Category c : categories) {
            CategoryRow row = new CategoryRow(c, onPlay);
            genreRows.add(row);
            allRows.add(row);
        }

        rowsBox.setPadding(new Insets(0, 0, 30, 0));
        rowsBox.setTranslateY(-48);                               // rows overlap the hero's fade-out a little
        // soft gradient at the top of the rows: the hero's fade colour melts into the stage picture (no hard seam)
        rowsBox.setBackground(new javafx.scene.layout.Background(new javafx.scene.layout.BackgroundFill(
                new javafx.scene.paint.LinearGradient(0, 0, 0, 110, false, javafx.scene.paint.CycleMethod.NO_CYCLE,
                        new javafx.scene.paint.Stop(0, Theme.HERO_FADE),
                        new javafx.scene.paint.Stop(1, Theme.alpha(Theme.HERO_FADE, 0))),
                javafx.scene.layout.CornerRadii.EMPTY, Insets.EMPTY)));
        rowsBox.getChildren().addAll(allRows);
        results = new SearchResults(onPlay);

        Label footer = new Label("StreamVerse \u2022 student demo project \u2022 trailers and thumbnails are streamed from YouTube "
                + "and belong to their respective owners.");
        footer.setFont(Theme.font(FontWeight.NORMAL, 12));
        footer.setTextFill(Theme.MUTED);
        footer.setWrapText(true);
        Label repairHint = new Label("Some trailer won't play or has no picture? The app fixes dead links by itself \u2013 or check them all now:");
        repairHint.setFont(Theme.font(FontWeight.NORMAL, 12));
        repairHint.setTextFill(Theme.MUTED);
        HBox repairRow = new HBox(14, repairHint, Buttons.repairTrailers(this::repairAllTrailers));
        repairRow.setAlignment(Pos.CENTER_LEFT);
        VBox foot = new VBox(10, repairRow, footer);
        foot.setPadding(new Insets(0, Theme.SIDE, 36, Theme.SIDE));

        content.setSpacing(0);
        content.getChildren().addAll(hero, rowsBox, results, foot);

        scroll.vvalueProperty().addListener((o, a, b) -> onScrolled());
        scroll.viewportBoundsProperty().addListener((o, a, b) -> onScrolled());
        library.favorites().addListener(listListener);
        library.recents().addListener(listListener);
        library.progressVersion().addListener(progressListener);

        refreshPersonalRows();
        Platform.runLater(this::onScrolled);
    }

    // ------------------------------------------------------------------ rows that depend on you

    private List<Movie> top10() {
        LocalDate today = LocalDate.now();                    // a different, stable ranking each day
        return everything.stream()
                .sorted(Comparator.comparingInt(m -> (m.title() + today).hashCode()))
                .limit(10).toList();
    }

    private void refreshPersonalRows() {
        fill(recentRow, library.recents());
        fill(myListRow, library.favorites());

        List<Movie> because = new ArrayList<>();
        Movie last = library.recents().isEmpty() ? null : byKey.get(library.recents().get(0));
        if (last != null) {
            for (Category c : categories) {
                if (!c.movies().contains(last)) continue;
                for (Movie m : c.movies()) {
                    if (!m.equals(last) && !library.recents().contains(m.key()) && because.size() < 14) because.add(m);
                }
                break;
            }
            becauseRow.setTitle("Because you watched " + last.title());
        }
        becauseRow.setMovies(because);
        setShown(becauseRow, !filtering && !because.isEmpty());
        Platform.runLater(this::onScrolled);
    }

    private void fill(CategoryRow row, List<String> keys) {
        List<Movie> movies = new ArrayList<>();
        for (String k : keys) {
            Movie m = byKey.get(k);
            if (m != null) movies.add(m);
        }
        row.setMovies(movies);
        setShown(row, !filtering && !movies.isEmpty());
    }

    private static void setShown(CategoryRow row, boolean shown) {
        row.setVisible(shown);
        row.setManaged(shown);
    }

    private void browseTitles() { scrollToRow(topRow); }

    private void surpriseMe() {
        Movie m = everything.get(ThreadLocalRandom.current().nextInt(everything.size()));
        Toast.show("Surprise! \u201C" + m.title() + "\u201D");
        onPlay.accept(m);
    }

    // ------------------------------------------------------------------ repair all trailers

    private void repairAllTrailers() {
        if (repairing) { Toast.show("Already checking trailers\u2026"); return; }
        repairing = true;
        Toast.show("Checking all " + everything.size() + " trailers\u2026");
        int[] lastShown = {0};
        TrailerResolver.get().repairAll(everything, (done, total, message) -> {
            if (done - lastShown[0] >= 40 || done == total) {
                lastShown[0] = done;
                Platform.runLater(() -> Toast.show(message + "\u2026 " + done + "/" + total));
            }
        }).thenAccept(report -> Platform.runLater(() -> {
            repairing = false;
            if (report.offline()) Toast.show("Can't reach YouTube \u2013 check your internet connection and try again");
            else if (report.broken() == 0) Toast.show("All " + report.checked() + " trailers are working \u2713");
            else Toast.show("Checked " + report.checked() + ": repaired " + report.fixed() + " of " + report.broken() + " broken trailers");
        }));
    }

    // ------------------------------------------------------------------ scrolling + lazy loading

    private double topOf(CategoryRow row) {
        return content.sceneToLocal(row.localToScene(0, 0)).getY();
    }

    public void scrollToRow(CategoryRow row) {
        double max = Math.max(1, content.getHeight() - scroll.getViewportBounds().getHeight());
        double v = Math.max(0, Math.min(1, (topOf(row) - 84) / max));
        if (scrollAnim != null) scrollAnim.stop();
        scrollAnim = new Timeline(new KeyFrame(Duration.millis(650), new KeyValue(scroll.vvalueProperty(), v, Interpolator.EASE_BOTH)));
        scrollAnim.play();
    }

    /** Only rows within ~350 px of the visible area download their thumbnails. */
    private void onScrolled() {
        if (filtering) return;
        double y = currentScrollY();
        double viewH = scroll.getViewportBounds().getHeight();
        for (CategoryRow row : allRows) {
            if (row.isVisible() && topOf(row) < y + viewH + 350) row.ensureLoaded();
        }
    }

    // ------------------------------------------------------------------ search

    public void onSearchText(String text) {
        String q = text == null ? "" : text.trim();
        if (q.isEmpty()) { debounce.stop(); applyFilter(""); return; }
        debounce.setOnFinished(e -> applyFilter(q));
        debounce.playFromStart();
    }

    public boolean isFiltering() { return filtering; }

    /** One lower-case string per movie: title + every genre it appears in + year + rating + description. */
    private void buildSearchIndex() {
        for (Movie m : everything) {
            StringBuilder sb = new StringBuilder(m.title()).append(' ').append(m.year()).append(' ')
                    .append(m.rating()).append(' ').append(m.genre()).append(' ');
            for (Category c : categories) if (c.movies().contains(m)) sb.append(c.name()).append(' ');
            sb.append(m.description());
            haystack.put(m, sb.toString().toLowerCase(Locale.ROOT));
        }
    }

    /** Every word typed must match somewhere; title matches rank above genre matches above description matches. */
    private List<Movie> search(String query) {
        String[] words = query.toLowerCase(Locale.ROOT).split("\\s+");
        record Hit(Movie movie, int score) { }
        List<Hit> hits = new ArrayList<>();
        for (Movie m : everything) {
            String hay = haystack.get(m);
            String title = m.title().toLowerCase(Locale.ROOT);
            int score = 0;
            boolean all = true;
            for (String w : words) {
                if (!hay.contains(w)) { all = false; break; }
                score += title.startsWith(w) ? 0 : title.contains(w) ? 1 : hay.contains(" " + w) ? 2 : 3;
            }
            if (all) hits.add(new Hit(m, score));
        }
        hits.sort(Comparator.comparingInt(Hit::score).thenComparing(h -> h.movie().title()));
        return hits.stream().map(Hit::movie).toList();
    }

    private void applyFilter(String query) {
        filtering = !query.isEmpty();
        hero.setVisible(!filtering);
        hero.setManaged(!filtering);
        rowsBox.setVisible(!filtering);
        rowsBox.setManaged(!filtering);
        results.setVisible(filtering);
        results.setManaged(filtering);
        if (filtering) results.show(query, search(query));
        else { refreshPersonalRows(); Platform.runLater(this::onScrolled); }
        scrollToTop();
    }

    @Override public void onShow() { /* keep the scroll position when coming back to Home */ }

    public void dispose() {
        library.favorites().removeListener(listListener);
        library.recents().removeListener(listListener);
        library.progressVersion().removeListener(progressListener);
    }
}
