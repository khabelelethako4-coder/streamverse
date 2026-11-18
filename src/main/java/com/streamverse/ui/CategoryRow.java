package com.streamverse.ui;

import com.streamverse.audio.SoundManager;
import com.streamverse.audio.SoundManager.Sfx;
import com.streamverse.model.Category;
import com.streamverse.model.Movie;
import com.streamverse.ui.controls.Icon;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.List;
import java.util.function.Consumer;

/**
 * One labelled genre row. Exactly {@value #VISIBLE} thumbnails are visible; the rest are reached by
 * paging (arrow buttons, Shift+wheel / horizontal trackpad scroll, or dragging the row).
 *
 * Implementation: a clipped {@link Pane} (the viewport) contains an HBox "strip" of ALL cards; paging
 * animates strip.translateX. Card width is derived from the viewport width, so 5 cards always fit,
 * whatever the window size.
 */
public class CategoryRow extends VBox {

    public static final int VISIBLE = 5;
    private static final double GAP = 14;
    private static final double VPAD = 26;            // room above/below so the zoomed card is not clipped

    private final Category category;
    private final Label nameLabel = new Label();
    private final Consumer<Movie> onPlay;
    private final Pane viewport = new Pane();
    private final HBox strip = new HBox(GAP);
    private final DoubleProperty cardWidth = new SimpleDoubleProperty(240);
    private final HBox dots = new HBox(4);
    private final Arrow left, right;
    private List<Movie> movies = List.of();
    private int page;
    private Timeline slide;
    private long lastWheel;
    private double dragStartX;
    private boolean ranked;                           // true for the "Top 10" row: cards get big rank numbers
    private boolean near;                              // true once the row is close to the visible screen area

    public CategoryRow(Category category, Consumer<Movie> onPlay) {
        super(2);
        this.category = category;
        this.onPlay = onPlay;

        // ---------- header: genre label + page dashes ----------
        Label name = nameLabel;
        name.setText(category.name());
        name.setFont(Theme.font(FontWeight.BOLD, 22));
        name.setTextFill(Color.WHITE);
        Label tagline = new Label(category.tagline());
        tagline.setFont(Theme.font(FontWeight.NORMAL, 13));
        tagline.setTextFill(Theme.MUTED);
        VBox names = new VBox(0, name, tagline);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        dots.setAlignment(Pos.BOTTOM_RIGHT);
        dots.setFillHeight(false);
        HBox header = new HBox(10, names, spacer, dots);
        header.setAlignment(Pos.BOTTOM_LEFT);
        header.setPadding(new Insets(0, Theme.SIDE, 0, Theme.SIDE));

        // ---------- viewport + strip ----------
        viewport.setPrefWidth(100);                    // real width comes from HBox.setHgrow
        viewport.setMinWidth(0);
        viewport.prefHeightProperty().bind(cardWidth.multiply(9.0 / 16.0).add(MovieCard.TEXT_H + 2 * VPAD));
        viewport.minHeightProperty().bind(viewport.prefHeightProperty());
        cardWidth.bind(Bindings.createDoubleBinding(
                () -> Math.max(130, (viewport.getWidth() - (VISIBLE - 1) * GAP) / VISIBLE), viewport.widthProperty()));
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(viewport.widthProperty());
        clip.heightProperty().bind(viewport.heightProperty());
        viewport.setClip(clip);
        strip.setLayoutY(VPAD);
        strip.setAlignment(Pos.TOP_LEFT);
        viewport.getChildren().add(strip);

        left = new Arrow(false);
        right = new Arrow(true);
        HBox body = new HBox(left, viewport, right);
        HBox.setHgrow(viewport, Priority.ALWAYS);
        getChildren().addAll(header, body);

        // arrows only show while the pointer is over the row (like Netflix)
        setOnMouseEntered(e -> updateArrows(true));
        setOnMouseExited(e -> updateArrows(false));
        cardWidth.addListener((o, a, b) -> snapToPage());

        // wheel: Shift+wheel or horizontal trackpad scroll pages the row; plain wheel scrolls the page
        addEventFilter(ScrollEvent.SCROLL, e -> {
            double dx = e.getDeltaX();
            if (dx == 0 && e.isShiftDown()) dx = e.getDeltaY();
            if (Math.abs(dx) < 1) return;
            long now = System.currentTimeMillis();
            if (now - lastWheel > 380) { lastWheel = now; pageBy(dx < 0 ? 1 : -1); }
            e.consume();
        });
        // drag-to-swipe
        viewport.setOnMousePressed(e -> dragStartX = e.getSceneX());
        viewport.setOnMouseReleased(e -> {
            double dx = e.getSceneX() - dragStartX;
            if (Math.abs(dx) > 90) pageBy(dx < 0 ? 1 : -1);
        });

        setMovies(category.movies());
    }

    /** Turns the row into a numbered Top-10 style row (call before setMovies). */
    public CategoryRow setRanked(boolean value) { this.ranked = value; return this; }

    public String getCategoryName() { return nameLabel.getText(); }

    public void setTitle(String text) { nameLabel.setText(text); }

    /** Lazy loading: called by HomeView when the row is near the viewport; only then do its thumbnails download. */
    public void ensureLoaded() {
        near = true;
        loadCards(VISIBLE + 1);
    }

    private void loadCards(int upTo) {
        for (int i = 0; i < strip.getChildren().size() && i < upTo; i++) {
            if (strip.getChildren().get(i) instanceof MovieCard card) card.loadImage();
        }
    }

    public int getMovieCount() { return movies.size(); }

    /** Replace the shown movies (used by the search filter). */
    public void setMovies(List<Movie> list) {
        this.movies = list;
        strip.getChildren().clear();
        for (int i = 0; i < list.size(); i++) {
            strip.getChildren().add(new MovieCard(list.get(i), cardWidth, onPlay, this::shiftFor, ranked ? i + 1 : 0));
        }
        page = 0;
        strip.setTranslateX(0);
        rebuildDots();
        updateArrows(false);
        if (near) loadCards(VISIBLE + 1);
    }

    // ------------------------------------------------------------------ paging

    private int pageCount() { return Math.max(1, (int) Math.ceil(movies.size() / (double) VISIBLE)); }

    private double maxOffset() {
        double total = movies.size() * cardWidth.get() + Math.max(0, movies.size() - 1) * GAP;
        return Math.max(0, total - viewport.getWidth());
    }

    private double offsetFor(int p) {
        if (p >= pageCount() - 1) return maxOffset();              // last page: show the final cards flush right
        return Math.min(p * (viewport.getWidth() + GAP), maxOffset());
    }

    private void pageBy(int dir) {
        int target = Math.max(0, Math.min(pageCount() - 1, page + dir));
        if (target == page) return;
        page = target;
        loadCards(Integer.MAX_VALUE);                    // the other pages are about to scroll into view
        SoundManager.get().play(Sfx.NAV);
        if (slide != null) slide.stop();
        slide = new Timeline(new KeyFrame(Duration.millis(480),
                new KeyValue(strip.translateXProperty(), -offsetFor(page), Interpolator.EASE_BOTH)));
        slide.play();
        rebuildDots();
        updateArrows(isHover());
    }

    /** After a window resize keep showing the same page without animation. */
    private void snapToPage() {
        if (slide != null) slide.stop();
        strip.setTranslateX(-offsetFor(page));
    }

    private void updateArrows(boolean rowHovered) {
        left.setAvailable(rowHovered && page > 0);
        right.setAvailable(rowHovered && page < pageCount() - 1);
    }

    private void rebuildDots() {
        dots.getChildren().clear();
        if (pageCount() < 2) return;
        for (int i = 0; i < pageCount(); i++) {
            Region d = new Region();
            d.setPrefSize(16, 3);
            d.setMinSize(16, 3);
            d.setStyle("-fx-background-color: " + (i == page ? "#e5e5e5" : "#4d4d4d") + ";");
            dots.getChildren().add(d);
        }
    }

    /** Sideways nudge so a zoomed edge card stays inside the clipped viewport. */
    private double shiftFor(MovieCard card, double extra) {
        double minX = card.getLayoutX() + strip.getTranslateX();
        double maxX = minX + cardWidth.get();
        if (minX < extra) return extra - minX;
        if (maxX + extra > viewport.getWidth()) return viewport.getWidth() - (maxX + extra);
        return 0;
    }

    // ------------------------------------------------------------------ custom arrow button

    /** Tall translucent chevron zone at the side of the row (custom control, no Button class). */
    private final class Arrow extends StackPane {
        private final boolean forward;
        private final Icon icon;
        private boolean available;
        private Timeline fade;

        Arrow(boolean forward) {
            this.forward = forward;
            icon = new Icon(forward ? Icon.CHEVRON_RIGHT : Icon.CHEVRON_LEFT, 44, Color.WHITE);
            getChildren().add(icon);
            setPrefWidth(Theme.SIDE);
            setMinWidth(Theme.SIDE);
            setMaxWidth(Theme.SIDE);
            setStyle("-fx-background-color: rgba(20,20,20,0.55);");
            setCursor(Cursor.HAND);
            setOpacity(0);
            setMouseTransparent(true);
            setOnMouseEntered(e -> { icon.setScaleX(1.25); icon.setScaleY(1.25); SoundManager.get().play(Sfx.HOVER); });
            setOnMouseExited(e -> { icon.setScaleX(1); icon.setScaleY(1); });
            setOnMouseClicked(e -> pageBy(forward ? 1 : -1));
        }

        void setAvailable(boolean value) {
            if (available == value) return;
            available = value;
            setMouseTransparent(!value);
            if (fade != null) fade.stop();
            fade = new Timeline(new KeyFrame(Duration.millis(180), new KeyValue(opacityProperty(), value ? 1 : 0)));
            fade.play();
        }
    }
}
