package com.streamverse.player;

import com.streamverse.model.Movie;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Worker;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Locale;

/**
 * Video player component: a WebView that shows the YouTube embed (served by {@link LocalPlayerServer}) plus a
 * clean Java API (load / play / pause / seek / mute) and observable state for the custom controls.
 *
 * Java -> YouTube : executeScript("cmd('pauseVideo')")
 * YouTube -> Java : a 250 ms poller reads executeScript("snapshot()") = "state|time|duration|muted|error"
 */
public class YouTubePlayerPane extends StackPane {

    public enum State { IDLE, LOADING, PLAYING, PAUSED, BUFFERING, ENDED, ERROR }

    /** A desktop-Chrome user agent: YouTube serves its normal embed instead of an "unsupported browser" page. */
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) "
            + "Chrome/124.0.0.0 Safari/537.36";
    private static final long LOAD_TIMEOUT_MS = 25_000;

    private final WebView webView = new WebView();
    private final WebEngine engine = webView.getEngine();
    private final ObjectProperty<State> state = new SimpleObjectProperty<>(State.IDLE);
    private final BooleanProperty ready = new SimpleBooleanProperty(false);
    private final DoubleProperty currentTime = new SimpleDoubleProperty();
    private final DoubleProperty duration = new SimpleDoubleProperty();
    private final BooleanProperty muted = new SimpleBooleanProperty();
    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final StringProperty errorCode = new SimpleStringProperty("");            // YouTube error number, "" if none
    private final Timeline poller = new Timeline(new KeyFrame(Duration.millis(250), e -> poll()));
    private long loadStarted;

    public YouTubePlayerPane() {
        webView.setContextMenuEnabled(false);
        engine.setUserAgent(USER_AGENT);
        poller.setCycleCount(Animation.INDEFINITE);
        engine.setOnError(ev -> fail("Could not load the player: " + ev.getMessage()));
        engine.getLoadWorker().stateProperty().addListener((o, a, s) -> {
            if (s == Worker.State.FAILED && state.get() == State.LOADING) {
                fail("Could not reach the trailer. Check your internet connection.");
            }
        });
        setStyle("-fx-background-color: black;");
        getChildren().add(webView);
    }

    // ------------------------------------------------------------------ commands

    public void load(Movie movie) { load(movie, 0); }

    /** Loads the trailer and starts it at the given second (used to resume where the viewer left off). */
    public void load(Movie movie, double startSeconds) {
        stop();
        state.set(State.LOADING);
        loadStarted = System.currentTimeMillis();
        try {
            engine.load(LocalPlayerServer.get().playerUrl(movie.videoId(), (int) startSeconds));
            poller.playFromStart();
        } catch (IOException ex) {
            fail("Could not start the local player: " + ex.getMessage());
        }
    }

    /** Stops playback by replacing the page (also guarantees the audio stops when the overlay closes). */
    public void stop() {
        poller.stop();
        ready.set(false);
        currentTime.set(0);
        duration.set(0);
        errorMessage.set("");
        errorCode.set("");
        state.set(State.IDLE);
        engine.load("about:blank");
    }

    public void play() { exec("cmd('playVideo')"); }

    public void pause() { exec("cmd('pauseVideo')"); }

    public void toggle() {
        switch (state.get()) {
            case PLAYING, BUFFERING -> pause();
            case ENDED -> { exec("cmd('seekTo',[0,true])"); play(); }
            case PAUSED, LOADING -> play();
            default -> { }
        }
    }

    public void seekFraction(double fraction) {
        if (duration.get() <= 0) return;
        exec(String.format(Locale.ROOT, "cmd('seekTo',[%.2f,true])", fraction * duration.get()));
    }

    /** Skip forward (+) or rewind (-) by the given number of seconds; clamped to 0..duration. */
    public void seekBy(double seconds) {
        double t = Math.max(0, currentTime.get() + seconds);
        if (duration.get() > 0) t = Math.min(t, duration.get() - 0.3);
        currentTime.set(t);                                        // the seek bar moves immediately
        exec(String.format(Locale.ROOT, "skip(%.2f)", seconds));   // the page knows the exact current time
        if (state.get() == State.ENDED) play();                    // rewinding after the end restarts playback
    }

    public void toggleMute() {
        exec(muted.get() ? "cmd('unMute')" : "cmd('mute')");
        muted.set(!muted.get()); // optimistic; the poller confirms it
    }

    // ------------------------------------------------------------------ polling

    private void exec(String js) {
        try { engine.executeScript(js); } catch (Exception ignored) { /* page not ready yet */ }
    }

    private void poll() {
        State current = state.get();
        if (current == State.IDLE || current == State.ERROR) return;
        if (!ready.get() && System.currentTimeMillis() - loadStarted > LOAD_TIMEOUT_MS) {
            fail("The trailer is taking too long to load.");
            return;
        }
        Object result;
        try {
            result = engine.executeScript("typeof snapshot==='function'?snapshot():''");
        } catch (Exception e) {
            return; // page still loading
        }
        if (!(result instanceof String)) return;
        String[] p = ((String) result).split("\\|", -1);
        if (p.length < 5) return;

        if (!p[4].isEmpty()) { errorCode.set(p[4]); fail(messageForError(p[4])); return; }

        currentTime.set(parse(p[1]));
        double d = parse(p[2]);
        if (d > 0) duration.set(d);
        muted.set("1".equals(p[3]));

        switch ((int) parse(p[0])) {
            case 1 -> { ready.set(true); state.set(State.PLAYING); }
            case 2, 5 -> { ready.set(true); state.set(State.PAUSED); }   // 5 = cued (autoplay blocked)
            case 0 -> { ready.set(true); state.set(State.ENDED); }
            case 3 -> { if (ready.get()) state.set(State.BUFFERING); }
            default -> { }                                               // -1 / -2: still starting
        }
    }

    private void fail(String message) {
        poller.stop();
        errorMessage.set(message);
        state.set(State.ERROR);
    }

    private static String messageForError(String code) {
        return switch (code) {
            case "100" -> "This trailer was removed or is private (YouTube error 100).";
            case "101", "150" -> "The owner of this trailer disabled playback inside other apps (YouTube error " + code + ").";
            case "153" -> "YouTube rejected the embedded player (error 153).";
            default -> "This trailer cannot be played inside the app (YouTube error " + code + ").";
        };
    }

    private static double parse(String s) {
        try { return Double.parseDouble(s); } catch (NumberFormatException e) { return -2; }
    }

    // ------------------------------------------------------------------ observable state

    public ObjectProperty<State> stateProperty() { return state; }
    public ReadOnlyBooleanProperty readyProperty() { return ready; }
    public DoubleProperty currentTimeProperty() { return currentTime; }
    public DoubleProperty durationProperty() { return duration; }
    public BooleanProperty mutedProperty() { return muted; }
    public StringProperty errorMessageProperty() { return errorMessage; }
    public StringProperty errorCodeProperty() { return errorCode; }
}
