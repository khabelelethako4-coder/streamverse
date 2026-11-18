package com.streamverse.ui;

import com.streamverse.library.UserLibrary;
import com.streamverse.model.Movie;
import com.streamverse.ui.controls.CoverImage;
import com.streamverse.ui.controls.CustomButton;
import com.streamverse.ui.controls.CustomButton.Style;
import com.streamverse.ui.controls.Icon;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.List;
import java.util.function.Consumer;

/** Big rotating "featured" banner at the top of the home screen (cross-fades every 9 seconds). */
public class HeroBanner extends StackPane {

    private final List<Movie> featured;
    private final CoverImage front = new CoverImage();
    private final CoverImage back = new CoverImage();
    private final Label title = new Label();
    private final Label meta = new Label();
    private final Label blurb = new Label();
    private final VBox textBox;
    private final HBox dots = new HBox(6);
    private final Timeline rotator;
    private final Consumer<Movie> onPlay;
    private final CustomButton myList;
    private final javafx.collections.ListChangeListener<String> favListener = c -> refreshMyList();
    private int index = -1;
    private boolean busy;

    public HeroBanner(List<Movie> featured, Consumer<Movie> onPlay, Runnable onBrowse, Runnable onSurprise) {
        this.featured = featured;
        this.onPlay = onPlay;
        setAlignment(Pos.BOTTOM_LEFT);
        setStyle("-fx-background-color: transparent;");   // the stage picture shows through when no backdrop has loaded
        prefHeightProperty().bind(Bindings.createDoubleBinding(
                () -> Math.max(430, Math.min(620, getWidth() * 0.46)), widthProperty()));
        setMinHeight(USE_PREF_SIZE);
        setMaxHeight(USE_PREF_SIZE);

        // dark overlays: left (for text legibility) and bottom (blend into the page background)
        Region leftShade = new Region();
        leftShade.setBackground(new Background(new BackgroundFill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(14, 6, 8, 0.93)), new Stop(0.45, Color.rgb(14, 6, 8, 0.52)),
                new Stop(1, Color.rgb(14, 6, 8, 0))), CornerRadii.EMPTY, Insets.EMPTY)));
        Region bottomShade = new Region();
        bottomShade.setBackground(new Background(new BackgroundFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0.55, Theme.alpha(Theme.HERO_FADE, 0)), new Stop(1, Theme.HERO_FADE)), CornerRadii.EMPTY, Insets.EMPTY)));
        leftShade.setMouseTransparent(true);
        bottomShade.setMouseTransparent(true);

        Label tag = new Label("FEATURED TRAILER");
        tag.setFont(Theme.font(FontWeight.BOLD, 13));
        tag.setTextFill(Theme.ACCENT);
        title.setFont(Theme.font(FontWeight.EXTRA_BOLD, 54));
        title.setTextFill(Color.WHITE);
        title.setWrapText(true);
        title.setMaxWidth(640);
        title.setEffect(new DropShadow(14, Color.rgb(0, 0, 0, 0.8)));
        meta.setFont(Theme.font(FontWeight.SEMI_BOLD, 15));
        meta.setTextFill(Theme.TEXT_DIM);
        blurb.setFont(Theme.font(FontWeight.NORMAL, 17));
        blurb.setTextFill(Color.WHITE);
        blurb.setWrapText(true);
        blurb.setMaxWidth(560);
        blurb.setEffect(new DropShadow(8, Color.rgb(0, 0, 0, 0.8)));

        CustomButton play = new CustomButton("Play Trailer", Icon.PLAY, Style.PRIMARY)
                .setOnAction(() -> onPlay.accept(featured.get(index)));
        myList = new CustomButton("My List", Icon.ADD, Style.SECONDARY).setOnAction(() -> {
            Movie m = featured.get(index);
            boolean now = UserLibrary.get().toggleFavorite(m.key());
            Toast.show(now ? "Added \u201C" + m.title() + "\u201D to My List"
                           : "Removed \u201C" + m.title() + "\u201D from My List");
        });
        CustomButton browse = new CustomButton("Browse Titles", Icon.INFO, Style.SECONDARY)
                .setOnAction(onBrowse);
        CustomButton surprise = com.streamverse.ui.controls.Buttons.surpriseMe(onSurprise);
        HBox buttons = new HBox(12, play, myList, surprise, browse);
        UserLibrary.get().favorites().addListener(favListener);

        textBox = new VBox(12, tag, title, meta, blurb, buttons);
        textBox.setAlignment(Pos.BOTTOM_LEFT);
        textBox.setPadding(new Insets(0, Theme.SIDE, 96, Theme.SIDE));
        textBox.setPickOnBounds(false);
        textBox.setMaxWidth(USE_PREF_SIZE);

        dots.setAlignment(Pos.BOTTOM_RIGHT);
        dots.setPadding(new Insets(0, Theme.SIDE, 100, 0));
        dots.setPickOnBounds(false);
        dots.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        StackPane.setAlignment(dots, Pos.BOTTOM_RIGHT);
        StackPane.setAlignment(textBox, Pos.BOTTOM_LEFT);

        getChildren().addAll(back, front, leftShade, bottomShade, textBox, dots);

        rotator = new Timeline(new KeyFrame(Duration.seconds(9), e -> show((index + 1) % featured.size())));
        rotator.setCycleCount(Animation.INDEFINITE);
        buildDots();
        if (!featured.isEmpty()) {
            show(0);
            rotator.play();
        }
    }

    private void show(int next) {
        if (busy || featured.isEmpty()) return;
        Movie m = featured.get(next);
        boolean first = index < 0;
        index = next;
        refreshDots();

        if (first) {
            loadBackdrop(m, front);
            setText(m);
            return;
        }
        busy = true;
        loadBackdrop(m, back);                                  // new picture waits underneath
        FadeTransition textOut = new FadeTransition(Duration.millis(260), textBox);
        textOut.setToValue(0);
        textOut.setOnFinished(e -> {
            setText(m);
            FadeTransition textIn = new FadeTransition(Duration.millis(380), textBox);
            textIn.setToValue(1);
            textIn.play();
        });
        textOut.play();
        FadeTransition fade = new FadeTransition(Duration.millis(900), front);   // old picture fades away
        fade.setToValue(0);
        fade.setOnFinished(e -> {
            front.setImage(back.getImage(), back.isLetterboxed());
            front.setUserData(m);
            front.setOpacity(1);
            busy = false;
        });
        fade.play();
    }

    /** The hero's list button mirrors whether the featured movie is already in My List. */
    private void refreshMyList() {
        if (index < 0 || myList == null) return;
        boolean fav = UserLibrary.get().isFavorite(featured.get(index).key());
        myList.setText(fav ? "In My List" : "My List");
        myList.setIconPath(fav ? Icon.CHECK : Icon.ADD);
    }

    private void setText(Movie m) {
        title.setText(m.title());
        meta.setText(m.year() + "   \u2022   " + m.rating() + "   \u2022   " + m.genre());
        blurb.setText(m.description());
        refreshMyList();
    }

    /** Tries the 1280x720 image first; falls back to the (always present) 480x360 thumbnail. */
    private void loadBackdrop(Movie m, CoverImage target) {
        target.setUserData(m);
        Image max = ImageCache.backdrop(m);
        Runnable fallback = () -> {
            if (target.getUserData() == m) target.setImage(ImageCache.thumbnail(m), true);
        };
        target.setImage(max, false);
        if (max.isError()) { fallback.run(); return; }
        max.errorProperty().addListener((o, a, err) -> { if (err) fallback.run(); });
        max.progressProperty().addListener((o, a, p) -> {
            // YouTube answers a missing maxres image with a tiny grey placeholder
            if (p.doubleValue() >= 1.0 && max.getWidth() < 400) fallback.run();
        });
    }

    private void buildDots() {
        dots.getChildren().clear();
        for (int i = 0; i < featured.size(); i++) {
            Region d = new Region();
            d.setPrefSize(26, 4);
            d.setMinSize(26, 4);
            d.setCursor(Cursor.HAND);
            final int target = i;
            d.setOnMouseClicked(e -> { if (target != index) { show(target); rotator.playFromStart(); } });
            dots.getChildren().add(d);
        }
    }

    private void refreshDots() {
        for (int i = 0; i < dots.getChildren().size(); i++) {
            dots.getChildren().get(i).setStyle("-fx-background-color: " + (i == index ? "#ffffff" : "rgba(255,255,255,0.35)")
                    + "; -fx-background-radius: 2;");
        }
    }
}
