package com.streamverse.ui;

import java.util.function.UnaryOperator;

import com.streamverse.audio.SoundManager;
import com.streamverse.audio.SoundManager.Sfx;
import com.streamverse.library.UserLibrary;
import com.streamverse.model.Movie;
import com.streamverse.net.TrailerResolver;
import com.streamverse.player.YouTubePlayerPane;
import com.streamverse.player.YouTubePlayerPane.State;
import com.streamverse.ui.controls.Buttons;
import com.streamverse.ui.controls.CustomButton;
import com.streamverse.ui.controls.Icon;
import com.streamverse.ui.controls.LoadingAnimation;
import com.streamverse.ui.controls.SeekBar;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Full-window "cinema" layer that sits on top of the home screen.
 * Layout: scrim -> panel [ header | video area (WebView + loading + error + flash) | custom control bar | blurb ].
 * Shortcuts: Space = play/pause, Left/J = back 10 s, Right/L = forward 10 s, M = mute, F = full screen, N = next trailer, Esc = close.
 * Double-click the left/right half of the video to rewind/skip 10 s.
 */
public class PlayerOverlay extends StackPane {

    private final HostServices hostServices;
    private final UnaryOperator<Movie> nextProvider;
    private final CustomButton heartBtn = Buttons.heart(null);
    private final YouTubePlayerPane player = new YouTubePlayerPane();
    private final LoadingAnimation loading = new LoadingAnimation();
    private final VBox errorPane = new VBox(14);
    private final Label errorText = new Label();
    private final Label title = new Label();
    private final Label meta = new Label();
    private final Label blurb = new Label();
    private final Label timeLabel = new Label("0:00 / 0:00");
    private final SeekBar seekBar = new SeekBar();
    private final CustomButton playPause = Buttons.playPause(null);
    private final CustomButton muteBtn = Buttons.mute(null);
    private final CustomButton fullBtn = Buttons.fullScreen(null);
    private final StackPane flash = new StackPane();
    private final StackPane skipFlash = new StackPane();
    private final Icon skipFlashIcon = new Icon(Icon.FORWARD_10, 44, Color.WHITE);
    private final PauseTransition pendingToggle = new PauseTransition(Duration.millis(260));   // waits to see if it was a double-click
    private final Icon flashIcon = new Icon(Icon.PLAY, 34, Color.WHITE);
    private Movie current;
    private final java.util.Set<String> repairTried = new java.util.HashSet<>();     // movies we already tried to repair this session
    private final Timeline autosave = new Timeline(new KeyFrame(Duration.seconds(10), e -> {
        if (isVisible() && player.stateProperty().get() == State.PLAYING) saveProgress();
    }));

    public PlayerOverlay(HostServices hostServices, UnaryOperator<Movie> nextProvider) {
        this.hostServices = hostServices;
        this.nextProvider = nextProvider;
        setVisible(false);
        setOpacity(0);

        Region scrim = new Region();
        scrim.setStyle("-fx-background-color: rgba(0,0,0,0.90);");
        scrim.setOnMouseClicked(e -> close());   // click outside the panel closes it

        // ---------------- header
        title.setFont(Theme.font(FontWeight.BOLD, 24));
        title.setTextFill(Color.WHITE);
        meta.setFont(Theme.font(FontWeight.NORMAL, 14));
        meta.setTextFill(Theme.MUTED);
        VBox titles = new VBox(2, title, meta);
        CustomButton closeBtn = Buttons.close(this::close);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox header = new HBox(12, titles, spacer, closeBtn);
        header.setAlignment(Pos.CENTER_LEFT);

        // ---------------- video area
        Region clickLayer = new Region();                      // catches clicks above the WebView
        clickLayer.setOnMouseClicked(e -> {
            if (player.stateProperty().get() == State.ERROR) return;
            if (e.getClickCount() >= 2) {                      // double-click: left half = back 10 s, right half = forward 10 s
                pendingToggle.stop();
                skip(e.getX() < clickLayer.getWidth() / 2 ? -10 : 10);
            } else {                                           // single click: play/pause (after a short wait)
                pendingToggle.setOnFinished(ev -> togglePlayback(true));
                pendingToggle.playFromStart();
            }
        });

        Circle flashBg = new Circle(40, Color.rgb(0, 0, 0, 0.6));
        flash.getChildren().addAll(flashBg, flashIcon);
        flash.setMaxSize(80, 80);
        flash.setOpacity(0);
        flash.setMouseTransparent(true);

        Circle skipBg = new Circle(42, Color.rgb(0, 0, 0, 0.62));
        skipFlash.getChildren().addAll(skipBg, skipFlashIcon);
        skipFlash.setMaxSize(84, 84);
        skipFlash.setOpacity(0);
        skipFlash.setMouseTransparent(true);

        buildErrorPane();

        StackPane videoBox = new StackPane(player, clickLayer, flash, skipFlash, loading, errorPane);
        videoBox.setStyle("-fx-background-color: black;");
        videoBox.prefWidthProperty().bind(Bindings.createDoubleBinding(this::videoWidth, widthProperty(), heightProperty()));
        videoBox.prefHeightProperty().bind(videoBox.prefWidthProperty().multiply(9.0 / 16.0));
        videoBox.minHeightProperty().bind(videoBox.prefHeightProperty());
        videoBox.maxHeightProperty().bind(videoBox.prefHeightProperty());
        loading.setVisible(true);

        // ---------------- control bar
        playPause.setOnAction(() -> togglePlayback(false));
        muteBtn.setOnAction(player::toggleMute);
        fullBtn.setOnAction(this::toggleFullScreen);
        CustomButton ytBtn = Buttons.watchOnYouTubeIcon(() -> { if (current != null) hostServices.showDocument(current.watchUrl()); });
        timeLabel.setFont(Theme.font(FontWeight.SEMI_BOLD, 13));
        timeLabel.setTextFill(Theme.TEXT_DIM);
        timeLabel.setMinWidth(92);
        HBox.setHgrow(seekBar, Priority.ALWAYS);
        heartBtn.setOnAction(() -> {
            if (current == null) return;
            boolean now = UserLibrary.get().toggleFavorite(current.key());
            Toast.show(now ? "Added \u201C" + current.title() + "\u201D to My List"
                           : "Removed \u201C" + current.title() + "\u201D from My List");
        });
        UserLibrary.get().favorites().addListener((javafx.collections.ListChangeListener<String>) c -> refreshHeart());
        CustomButton nextBtn = Buttons.next(this::playNext);
        CustomButton rewindBtn = Buttons.rewind10(() -> skip(-10));
        CustomButton forwardBtn = Buttons.forward10(() -> skip(10));
        HBox controls = new HBox(10, playPause, rewindBtn, forwardBtn, nextBtn, seekBar, timeLabel, heartBtn, muteBtn, ytBtn, fullBtn);
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(10, 0, 4, 0));
        seekBar.setOnSeek(player::seekFraction);

        blurb.setFont(Theme.font(FontWeight.NORMAL, 14));
        blurb.setTextFill(Theme.MUTED);
        blurb.setWrapText(true);

        VBox panel = new VBox(10, header, videoBox, controls, blurb);
        panel.setAlignment(Pos.TOP_LEFT);
        panel.prefWidthProperty().bind(videoBox.prefWidthProperty());
        panel.maxWidthProperty().bind(videoBox.prefWidthProperty());
        panel.setMaxHeight(Region.USE_PREF_SIZE);

        getChildren().addAll(scrim, panel);
        StackPane.setAlignment(panel, Pos.CENTER);

        wireState();
        autosave.setCycleCount(Animation.INDEFINITE);
        autosave.play();
        sceneProperty().addListener((o, a, scene) -> {
            if (scene != null) scene.addEventFilter(KeyEvent.KEY_PRESSED, this::onKey);
        });
    }

    // ================================================================= public API

    public void open(Movie movie) {
        Movie previous = current;
        current = previous;                              // saveProgress() below still refers to the previous movie
        title.setText(movie.title());
        meta.setText(movie.year() + "  \u2022  " + movie.rating() + "  \u2022  " + movie.genre());
        blurb.setText(movie.description());
        loading.setMovieTitle(movie.title());
        loading.setVisible(true);
        loading.setOpacity(1);
        errorPane.setVisible(false);
        seekBar.setProgress(0);
        timeLabel.setText("0:00 / 0:00");
        playPause.setIconPath(Icon.PAUSE);

        saveProgress();                                  // remember where we stopped in the previous trailer
        current = movie;
        boolean comingSoon = isComingSoon(movie);
        if (!comingSoon) UserLibrary.get().addRecent(movie.key());
        double resume = comingSoon ? 0 : UserLibrary.get().resumeSeconds(movie.key());
        if (resume > 10) Toast.show("Resuming \u201C" + movie.title() + "\u201D at " + format(resume));
        refreshHeart();
        boolean wasOpen = isVisible() && getOpacity() > 0.9;
        SoundManager.get().play(wasOpen ? Sfx.NAV : Sfx.OPEN);
        if (!wasOpen) {
            setVisible(true);
            setOpacity(0);
            FadeTransition fade = new FadeTransition(Duration.millis(260), this);
            fade.setToValue(1);
            fade.play();
        }
        player.load(movie, resume);
        requestFocus();
    }

    public void close() {
        if (!isVisible()) return;
        SoundManager.get().play(Sfx.CLOSE);
        saveProgress();
        player.stop();                              // stops sound immediately
        FadeTransition fade = new FadeTransition(Duration.millis(200), this);
        fade.setToValue(0);
        fade.setOnFinished(e -> setVisible(false));
        fade.play();
    }

    public boolean isOpen() { return isVisible(); }

    private static boolean isComingSoon(Movie m) { return "Soon".equals(m.rating()); }

    /** Stores how far the viewer got (Continue Watching bar + resume). Tiny/unknown positions are ignored. */
    private void saveProgress() {
        if (current == null || isComingSoon(current)) return;
        UserLibrary.get().saveProgress(current.key(), player.currentTimeProperty().get(), player.durationProperty().get());
    }

    /** YouTube errors that mean "this code is dead / may not be embedded": 2 bad id, 100 removed, 101/150 embedding off. */
    private static boolean isRepairable(String code) {
        return "2".equals(code) || "100".equals(code) || "101".equals(code) || "150".equals(code);
    }

    /** The trailer cannot play: look for a working official trailer on YouTube, remember it, and try again automatically. */
    private void startRepair() {
        Movie m = current;
        loading.setMovieTitle("Finding a working trailer for " + m.title() + "\u2026");
        loading.setVisible(true);
        loading.setOpacity(1);
        errorPane.setVisible(false);
        TrailerResolver.get().repair(m).thenAccept(found -> Platform.runLater(() -> {
            if (current != m || !isVisible()) return;
            if (found.isPresent()) {
                Toast.show("Found a working trailer for \u201C" + m.title() + "\u201D");
                loading.setMovieTitle(m.title());
                player.load(m, isComingSoon(m) ? 0 : UserLibrary.get().resumeSeconds(m.key()));
            } else {
                showError();
            }
        }));
    }

    private void showError() {
        SoundManager.get().play(Sfx.ERROR);
        errorText.setText(player.errorMessageProperty().get());
        loading.setVisible(false);
        errorPane.setVisible(true);
        errorPane.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(250), errorPane);
        ft.setToValue(1);
        ft.play();
    }

    private void playNext() {
        Movie next = current == null ? null : nextProvider.apply(current);
        if (next != null) open(next);
    }

    private void refreshHeart() {
        boolean fav = current != null && UserLibrary.get().isFavorite(current.key());
        heartBtn.setIconPath(fav ? Icon.HEART : Icon.HEART_OUTLINE);
    }

    // ================================================================= behaviour

    private double videoWidth() {
        double w = getWidth(), h = getHeight();
        double byHeight = (h - 190) * 16.0 / 9.0;     // leave room for header, controls, blurb
        return Math.max(480, Math.min(w * 0.90, byHeight));
    }

    private void buildErrorPane() {
        errorPane.setAlignment(Pos.CENTER);
        errorPane.setPadding(new Insets(24));
        errorPane.setStyle("-fx-background-color: rgba(10,10,10,0.94);");
        errorPane.setVisible(false);
        Icon warn = new Icon(Icon.INFO, 52, Theme.ACCENT);
        Label head = new Label("This trailer can't play here");
        head.setFont(Theme.font(FontWeight.BOLD, 22));
        head.setTextFill(Color.WHITE);
        errorText.setFont(Theme.font(FontWeight.NORMAL, 14));
        errorText.setTextFill(Theme.MUTED);
        errorText.setWrapText(true);
        errorText.setTextAlignment(TextAlignment.CENTER);
        errorText.setAlignment(Pos.CENTER);
        errorText.setMaxWidth(520);
        CustomButton watch = Buttons.watchOnYouTube(() -> { if (current != null) hostServices.showDocument(current.watchUrl()); });
        CustomButton retry = Buttons.tryAgain(() -> { if (current != null) { repairTried.remove(current.key()); open(current); } });
        HBox buttons = new HBox(12, watch, retry);
        buttons.setAlignment(Pos.CENTER);
        errorPane.getChildren().addAll(warn, head, errorText, buttons);
    }

    private void wireState() {
        player.stateProperty().addListener((o, a, s) -> {
            boolean playing = s == State.PLAYING || s == State.BUFFERING || s == State.LOADING;  // autoplay is on
            playPause.setIconPath(playing ? Icon.PAUSE : Icon.PLAY);
            if (s == State.ERROR) {
                String code = player.errorCodeProperty().get();
                if (current != null && isRepairable(code) && repairTried.add(current.key())) startRepair();
                else showError();
            } else if (s == State.LOADING) {
                errorPane.setVisible(false);
            }
        });
        // fade the loading animation out as soon as the first frame/state arrives
        player.readyProperty().addListener((o, was, now) -> {
            if (now) {
                FadeTransition ft = new FadeTransition(Duration.millis(350), loading);
                ft.setToValue(0);
                ft.setOnFinished(e -> loading.setVisible(false));
                ft.play();
            }
        });
        player.mutedProperty().addListener((o, a, m) -> muteBtn.setIconPath(m ? Icon.MUTE : Icon.VOLUME));
        player.currentTimeProperty().addListener((o, a, t) -> updateTime());
        player.durationProperty().addListener((o, a, t) -> updateTime());
    }

    private void updateTime() {
        double cur = player.currentTimeProperty().get(), dur = player.durationProperty().get();
        timeLabel.setText(format(cur) + " / " + format(dur));
        if (dur > 0) seekBar.setProgress(cur / dur);
    }

    private static String format(double seconds) {
        int s = (int) Math.max(0, Math.round(seconds));
        return (s / 60) + ":" + String.format("%02d", s % 60);
    }

    private void togglePlayback(boolean fromVideoClick) {
        State s = player.stateProperty().get();
        boolean willPlay = !(s == State.PLAYING || s == State.BUFFERING);
        player.toggle();
        if (fromVideoClick) flash(willPlay ? Icon.PLAY : Icon.PAUSE);
    }

    /** Big translucent play/pause symbol that pops and fades when the video is clicked. */
    private void flash(String iconPath) {
        flashIcon.setPath(iconPath);
        flash.setScaleX(0.8); flash.setScaleY(0.8); flash.setOpacity(1);
        ScaleTransition st = new ScaleTransition(Duration.millis(450), flash);
        st.setToX(1.5); st.setToY(1.5);
        FadeTransition ft = new FadeTransition(Duration.millis(450), flash);
        ft.setToValue(0);
        new ParallelTransition(st, ft).play();
    }

    /** Rewind (negative) or skip (positive) by that many seconds, with a sound and an on-screen pop-up. */
    private void skip(double seconds) {
        if (player.stateProperty().get() == State.ERROR || player.stateProperty().get() == State.IDLE) return;
        SoundManager.get().play(Sfx.NAV);
        player.seekBy(seconds);
        boolean forward = seconds > 0;
        skipFlashIcon.setPath(forward ? Icon.FORWARD_10 : Icon.REWIND_10);
        StackPane.setAlignment(skipFlash, forward ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        StackPane.setMargin(skipFlash, new Insets(0, forward ? 110 : 0, 0, forward ? 0 : 110));
        skipFlash.setScaleX(0.8); skipFlash.setScaleY(0.8); skipFlash.setOpacity(1);
        ScaleTransition st = new ScaleTransition(Duration.millis(500), skipFlash);
        st.setToX(1.25); st.setToY(1.25);
        FadeTransition ft = new FadeTransition(Duration.millis(500), skipFlash);
        ft.setToValue(0);
        new ParallelTransition(st, ft).play();
    }

    private void toggleFullScreen() {
        if (getScene() != null && getScene().getWindow() instanceof Stage stage) {
            stage.setFullScreenExitHint("Press Esc to leave full screen");
            stage.setFullScreen(!stage.isFullScreen());
            fullBtn.setIconPath(stage.isFullScreen() ? Icon.EXIT_FULLSCREEN : Icon.FULLSCREEN);
        }
    }

    private void onKey(KeyEvent e) {
        if (!isVisible()) return;
        KeyCode c = e.getCode();
        boolean fullscreen = getScene().getWindow() instanceof Stage s && s.isFullScreen();
        if (c == KeyCode.ESCAPE) {
            if (!fullscreen) { close(); e.consume(); }       // in full screen Esc first leaves full screen
        } else if (c == KeyCode.SPACE) { togglePlayback(true); e.consume(); }
        else if (c == KeyCode.UP) { player.changeVolume(5); e.consume(); }
        else if (c == KeyCode.DOWN) { player.changeVolume(-5); e.consume(); }
        else if (c == KeyCode.M) { player.toggleMute(); e.consume(); }
        else if (c == KeyCode.F) { toggleFullScreen(); e.consume(); }
        else if (c == KeyCode.N) { playNext(); e.consume(); }
        else if (c == KeyCode.LEFT || c == KeyCode.J) { skip(-10); e.consume(); }
        else if (c == KeyCode.RIGHT || c == KeyCode.L) { skip(10); e.consume(); }
    }
}
