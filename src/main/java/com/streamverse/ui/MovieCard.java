package com.streamverse.ui;

import com.streamverse.audio.SoundManager;
import com.streamverse.audio.SoundManager.Sfx;
import com.streamverse.downloads.DownloadService;
import com.streamverse.library.UserLibrary;
import com.streamverse.model.Movie;
import com.streamverse.net.TrailerResolver;
import com.streamverse.ui.controls.CoverImage;
import com.streamverse.ui.controls.Icon;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.WeakListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.util.function.Consumer;

/**
 * One movie: 16:9 thumbnail + title + "year - rating".
 *
 * Hover effect (one animated number 'hover' 0..1 drives everything, so all parts stay in sync):
 *   thumbnail zooms 9 %, red glow appears, white frame draws in, bottom shade fades in,
 *   a play badge pops up, a "Play trailer" caption slides in and the title turns white.
 * Cards at the row edge are nudged inwards (see {@link ShiftProvider}) so the zoom is never clipped.
 */
public class MovieCard extends VBox {

    public static final double TEXT_H = 48;          // height reserved under the thumbnail for title + meta
    private static final double HOVER_SCALE = 1.09;

    /** Lets the row tell the card how far it must slide sideways so the zoomed thumbnail stays visible. */
    @FunctionalInterface
    public interface ShiftProvider {
        double shiftFor(MovieCard card, double extraPerSide);
    }

    private final Movie movie;
    private final StackPane thumb;                     // scaled + glowing wrapper
    private final Region shade = new Region();
    private final Rectangle frame = new Rectangle();
    private final StackPane badge;
    private final CoverImage cover = new CoverImage();
    private final StackPane heart;
    private final Icon heartIcon;
    private final Label chip;
    private final UserLibrary library = UserLibrary.get();
    private final ListChangeListener<String> favListener = c -> refreshHeart();   // strong ref: the library only holds a weak one
    private boolean imageRequested;
    private String loadedVideoId;                       // which YouTube code the shown picture came from (changes after a repair)
    private final StackPane download;
    private final Icon downloadIcon;
    private final Label downloadPercent = new Label();
    private final DoubleProperty watched = new SimpleDoubleProperty(0);          // 0..1 how much of the trailer was watched
    private Label rankLabel;
    private final ChangeListener<Number> downloadListener = (o, a, b) -> refreshDownload();
    private final ChangeListener<Number> progressListener = (o, a, b) -> refreshProgress();
    private final ChangeListener<Number> repairListener = (o, a, b) -> reloadIfRepaired();
    private final Label caption = new Label("\u25B6  Play trailer");
    private final Label title = new Label();
    private final DropShadow glow = new DropShadow();
    private final DoubleProperty hover = new SimpleDoubleProperty(0);
    private final ShiftProvider shifter;
    private Timeline hoverAnim;
    private double shift;

    public MovieCard(Movie movie, ReadOnlyDoubleProperty widthProp, Consumer<Movie> onPlay, ShiftProvider shifter) {
        this(movie, widthProp, onPlay, shifter, 0);
    }

    /** @param rank 1..10 draws a big "Top 10" number on the picture; 0 = none */
    public MovieCard(Movie movie, ReadOnlyDoubleProperty widthProp, Consumer<Movie> onPlay, ShiftProvider shifter, int rank) {
        super(9);
        this.movie = movie;
        this.shifter = shifter;
        setAlignment(Pos.TOP_LEFT);
        setCursor(Cursor.HAND);
        lock(this, widthProp);

        DoubleBinding heightBinding = widthProp.multiply(9.0 / 16.0);

        // ---------- thumbnail layers (clipped to rounded corners) ----------
        StackPane inner = new StackPane();
        lock(inner, widthProp);
        inner.prefHeightProperty().bind(heightBinding);
        inner.minHeightProperty().bind(heightBinding);
        inner.maxHeightProperty().bind(heightBinding);
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(widthProp);
        clip.heightProperty().bind(heightBinding);
        clip.setArcWidth(18);
        clip.setArcHeight(18);
        inner.setClip(clip);

        // placeholder (visible while loading or when offline)
        Region placeholder = new Region();
        placeholder.setBackground(new Background(new BackgroundFill(Theme.placeholder(movie.title()), CornerRadii.EMPTY, Insets.EMPTY)));
        Label ghost = new Label(movie.title());
        ghost.setFont(Theme.font(FontWeight.BOLD, 16));
        ghost.setTextFill(Color.rgb(255, 255, 255, 0.55));
        ghost.setWrapText(true);
        ghost.setTextAlignment(TextAlignment.CENTER);
        ghost.setAlignment(Pos.CENTER);
        ghost.maxWidthProperty().bind(widthProp.multiply(0.8));
        ghost.setMouseTransparent(true);


        shade.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0.45, Color.rgb(0, 0, 0, 0)), new Stop(1, Color.rgb(0, 0, 0, 0.85))),
                CornerRadii.EMPTY, Insets.EMPTY)));
        shade.setOpacity(0);
        shade.setMouseTransparent(true);

        frame.widthProperty().bind(widthProp);
        frame.heightProperty().bind(heightBinding);
        frame.setArcWidth(18);
        frame.setArcHeight(18);
        frame.setFill(Color.TRANSPARENT);
        frame.setStroke(Color.WHITE);
        frame.setStrokeWidth(3);
        frame.setOpacity(0);
        frame.setMouseTransparent(true);

        Circle badgeBg = new Circle(25, Color.rgb(0, 0, 0, 0.62));
        badgeBg.setStroke(Color.WHITE);
        badgeBg.setStrokeWidth(2);
        badge = new StackPane(badgeBg, new Icon(Icon.PLAY, 28, Color.WHITE));
        badge.setMaxSize(50, 50);
        badge.setOpacity(0);
        badge.setMouseTransparent(true);

        caption.setFont(Theme.font(FontWeight.BOLD, 12));
        caption.setTextFill(Color.WHITE);
        caption.setOpacity(0);
        caption.setMouseTransparent(true);
        StackPane.setAlignment(caption, Pos.BOTTOM_LEFT);
        StackPane.setMargin(caption, new Insets(0, 0, 10, 12));

        // rating chip (top-left) and My List heart (top-right)
        chip = new Label(movie.rating());
        chip.setFont(Theme.font(FontWeight.BOLD, 11));
        chip.setTextFill(Color.WHITE);
        chip.setPadding(new Insets(2, 7, 2, 7));
        chip.setStyle("-fx-background-color: rgba(0,0,0,0.62); -fx-background-radius: 4; "
                + "-fx-border-color: rgba(255,255,255,0.55); -fx-border-radius: 4;");
        chip.setOpacity(0);
        chip.setMouseTransparent(true);
        StackPane.setAlignment(chip, Pos.TOP_LEFT);
        StackPane.setMargin(chip, new Insets(10, 0, 0, 10));

        Circle heartBg = new Circle(16, Color.rgb(0, 0, 0, 0.6));
        heartIcon = new Icon(Icon.HEART_OUTLINE, 19, Color.WHITE);
        heart = new StackPane(heartBg, heartIcon);
        heart.setMaxSize(32, 32);
        heart.setCursor(Cursor.HAND);
        StackPane.setAlignment(heart, Pos.TOP_RIGHT);
        StackPane.setMargin(heart, new Insets(8, 8, 0, 0));
        heart.setOnMouseEntered(e -> { heart.setScaleX(1.18); heart.setScaleY(1.18); });
        heart.setOnMouseExited(e -> { heart.setScaleX(1); heart.setScaleY(1); });
        heart.setOnMouseClicked(e -> {
            if (e.getButton() != MouseButton.PRIMARY) return;
            SoundManager.get().play(Sfx.CLICK);
            boolean now = library.toggleFavorite(movie.key());
            Toast.show(now ? "Added \u201C" + movie.title() + "\u201D to My List"
                           : "Removed \u201C" + movie.title() + "\u201D from My List");
            e.consume();                                  // don't also open the trailer
        });
        library.favorites().addListener(new WeakListChangeListener<>(favListener));

        // download badge (left of the heart): arrow -> % while saving -> green tick when saved
        Circle downloadBg = new Circle(16, Color.rgb(0, 0, 0, 0.6));
        downloadIcon = new Icon(Icon.DOWNLOAD, 19, Color.WHITE);
        downloadPercent.setFont(Theme.font(FontWeight.BOLD, 10));
        downloadPercent.setTextFill(Color.WHITE);
        downloadPercent.setVisible(false);
        download = new StackPane(downloadBg, downloadIcon, downloadPercent);
        download.setMaxSize(32, 32);
        download.setCursor(Cursor.HAND);
        StackPane.setAlignment(download, Pos.TOP_RIGHT);
        StackPane.setMargin(download, new Insets(8, 48, 0, 0));
        download.setOnMouseEntered(e -> { download.setScaleX(1.18); download.setScaleY(1.18); });
        download.setOnMouseExited(e -> { download.setScaleX(1); download.setScaleY(1); });
        download.setOnMouseClicked(e -> {
            if (e.getButton() != MouseButton.PRIMARY) return;
            SoundManager.get().play(Sfx.CLICK);
            DownloadService svc = DownloadService.get();
            if (svc.progressOf(movie) == null) {
                if (svc.isDownloaded(movie)) { svc.remove(movie); Toast.show("Removed \u201C" + movie.title() + "\u201D from Downloads"); }
                else { svc.download(movie); Toast.show("Downloading \u201C" + movie.title() + "\u201D\u2026"); }
            }
            e.consume();
        });
        DownloadService.get().versionProperty().addListener(new WeakChangeListener<>(downloadListener));

        // "how much have I watched" bar along the bottom edge (Continue Watching)
        Region barTrack = new Region();
        barTrack.setStyle("-fx-background-color: rgba(255,255,255,0.28);");
        barTrack.setPrefHeight(4); barTrack.setMaxHeight(4);
        Region barFill = new Region();
        barFill.setStyle("-fx-background-color: #E50914;");
        barFill.setPrefHeight(4); barFill.setMaxHeight(4);
        barFill.maxWidthProperty().bind(widthProp.multiply(watched));
        barFill.prefWidthProperty().bind(widthProp.multiply(watched));
        StackPane bar = new StackPane(barTrack, barFill);
        StackPane.setAlignment(barFill, Pos.BOTTOM_LEFT);
        StackPane.setAlignment(barTrack, Pos.BOTTOM_LEFT);
        bar.setMaxHeight(4);
        bar.setMouseTransparent(true);
        StackPane.setAlignment(bar, Pos.BOTTOM_CENTER);
        bar.visibleProperty().bind(watched.greaterThan(0.01));
        watched.set(library.progressFraction(movie.key()));
        library.progressVersion().addListener(new WeakChangeListener<>(progressListener));
        TrailerResolver.get().fixVersionProperty().addListener(new WeakChangeListener<>(repairListener));

        inner.getChildren().addAll(placeholder, ghost, cover, shade, frame, chip, download, heart, badge, caption, bar);
        if (rank > 0) {
            rankLabel = new Label(String.valueOf(rank));
            rankLabel.setFont(Theme.font(FontWeight.EXTRA_BOLD, 66));
            rankLabel.setTextFill(Color.WHITE);
            rankLabel.setEffect(new DropShadow(12, Color.BLACK));
            rankLabel.setOpacity(0.92);
            rankLabel.setMouseTransparent(true);
            StackPane.setAlignment(rankLabel, Pos.BOTTOM_LEFT);
            StackPane.setMargin(rankLabel, new Insets(0, 0, -8, 12));
            inner.getChildren().add(rankLabel);
        }

        glow.setColor(Color.TRANSPARENT);
        glow.setRadius(0);
        glow.setSpread(0.1);
        thumb = new StackPane(inner);
        thumb.setMaxSize(StackPane.USE_PREF_SIZE, StackPane.USE_PREF_SIZE);

        // ---------- text under the thumbnail ----------
        title.setText(movie.title());
        title.setFont(Theme.font(FontWeight.BOLD, 14));
        title.setTextFill(Theme.TEXT_DIM);
        title.setMaxWidth(Double.MAX_VALUE);
        Label meta = new Label(movie.metaLine());
        meta.setFont(Theme.font(FontWeight.NORMAL, 12));
        meta.setTextFill(Theme.MUTED);
        VBox text = new VBox(2, title, meta);
        text.setPrefHeight(TEXT_H - 9);
        text.setMinHeight(TEXT_H - 9);
        text.setMaxHeight(TEXT_H - 9);

        getChildren().addAll(thumb, text);

        // ---------- interaction ----------
        refreshHeart();
        refreshDownload();
        hover.addListener((o, a, t) -> apply(t.doubleValue()));
        setOnMouseEntered(e -> {
            shift = shifter.shiftFor(this, widthProp.get() * (HOVER_SCALE - 1) / 2);
            setViewOrder(-1);                                  // draw above neighbours
            animateHover(1);
            SoundManager.get().play(Sfx.HOVER);
        });
        setOnMouseExited(e -> animateHover(0));
        setOnMouseClicked(e -> {
            // isStillSincePress() is false after a drag-swipe, so swiping the row never opens a trailer
            if (e.getButton() == MouseButton.PRIMARY && e.isStillSincePress()) {
                SoundManager.get().play(Sfx.CLICK);
                onPlay.accept(movie);
            }
        });
    }

    public Movie getMovie() { return movie; }

    /** Starts downloading the thumbnail (called by the row when it comes near the screen). Safe to call repeatedly. */
    public void loadImage() {
        if (imageRequested) return;
        imageRequested = true;
        loadedVideoId = movie.videoId();
        cover.setImage(ImageCache.thumbnail(movie), true);
    }

    private void refreshProgress() { watched.set(library.progressFraction(movie.key())); }

    /** After the app repaired this movie's trailer code, show the picture of the new code. */
    private void reloadIfRepaired() {
        if (imageRequested && !movie.videoId().equals(loadedVideoId)) {
            imageRequested = false;
            loadImage();
        }
    }

    /** Arrow = can download, % = downloading, green tick = saved (click again to remove). */
    private void refreshDownload() {
        DownloadService svc = DownloadService.get();
        javafx.beans.property.ReadOnlyDoubleProperty p = svc.progressOf(movie);
        downloadPercent.textProperty().unbind();
        if (p != null) {
            downloadIcon.setVisible(false);
            downloadPercent.setVisible(true);
            downloadPercent.textProperty().bind(p.multiply(100).asString("%.0f%%"));
        } else {
            downloadPercent.setVisible(false);
            downloadIcon.setVisible(true);
            boolean done = svc.isDownloaded(movie);
            downloadIcon.setPath(done ? Icon.DOWNLOAD_DONE : Icon.DOWNLOAD);
            downloadIcon.fillProperty().set(done ? Color.web("#46d369") : Color.WHITE);
        }
        refreshDownloadVisibility();
    }

    private void refreshDownloadVisibility() {
        DownloadService svc = DownloadService.get();
        boolean sticky = svc.progressOf(movie) != null || svc.isDownloaded(movie);
        download.setOpacity(Math.max(hover.get(), sticky ? 0.95 : 0));
        download.setMouseTransparent(hover.get() < 0.6 && !sticky);
    }

    /** Filled red heart when the movie is in My List, outline otherwise; always visible if favourite. */
    private void refreshHeart() {
        boolean fav = library.isFavorite(movie.key());
        heartIcon.setPath(fav ? Icon.HEART : Icon.HEART_OUTLINE);
        heartIcon.fillProperty().set(fav ? Theme.ACCENT : Color.WHITE);
        heart.setOpacity(Math.max(hover.get(), fav ? 0.95 : 0));
        heart.setMouseTransparent(hover.get() < 0.6 && !fav);
    }

    private static void lock(javafx.scene.layout.Region r, ReadOnlyDoubleProperty w) {
        r.prefWidthProperty().bind(w);
        r.minWidthProperty().bind(w);
        r.maxWidthProperty().bind(w);
    }

    private void animateHover(double target) {
        if (hoverAnim != null) hoverAnim.stop();
        hoverAnim = new Timeline(new KeyFrame(Duration.millis(180),
                new KeyValue(hover, target, Interpolator.EASE_OUT)));
        if (target == 0) hoverAnim.setOnFinished(e -> setViewOrder(0));
        hoverAnim.play();
    }

    /** Applies the hover progress t (0 = idle, 1 = fully hovered) to every visual part. */
    private void apply(double t) {
        double s = 1 + (HOVER_SCALE - 1) * t;
        thumb.setScaleX(s);
        thumb.setScaleY(s);
        thumb.setTranslateX(shift * t);
        thumb.setEffect(t > 0.001 ? glow : null);
        glow.setRadius(32 * t);
        glow.setColor(Color.rgb(229, 9, 20, 0.60 * t));
        shade.setOpacity(t);
        frame.setOpacity(t * 0.9);
        badge.setOpacity(t);
        badge.setScaleX(0.6 + 0.4 * t);
        badge.setScaleY(0.6 + 0.4 * t);
        caption.setOpacity(t);
        caption.setTranslateY(8 * (1 - t));
        title.setTextFill(Theme.TEXT_DIM.interpolate(Color.WHITE, t));
        chip.setOpacity(t);
        refreshHeart();
        refreshDownloadVisibility();
        if (rankLabel != null) rankLabel.setOpacity(0.92 * (1 - t));
    }
}
