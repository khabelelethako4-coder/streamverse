package com.streamverse.ui.controls;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A button/label icon that is a real PICTURE: a white transparent PNG stored in src/main/resources/icons/.
 * It is loaded by URL, e.g. {@code Icon.class.getResource("/icons/play.png")} gives
 * {@code file:/.../icons/play.png} (or {@code jar:file:/...!/icons/play.png} inside a packaged jar).
 * The picture is recoloured in code (black on a white button, red for a saved heart ...), so one file serves every colour.
 * The constants below are the picture names; tools/generate_icons.py (re)creates all the files.
 */
public class Icon extends Pane {

    public static final String PLAY = "play";
    public static final String PAUSE = "pause";
    public static final String CLOSE = "close";
    public static final String VOLUME = "volume";
    public static final String MUTE = "mute";
    public static final String FULLSCREEN = "fullscreen";
    public static final String EXIT_FULLSCREEN = "exit_fullscreen";
    public static final String EXTERNAL = "external";
    public static final String SEARCH = "search";
    public static final String CHEVRON_LEFT = "chevron_left";
    public static final String CHEVRON_RIGHT = "chevron_right";
    public static final String INFO = "info";
    public static final String REPLAY = "replay";
    public static final String REWIND_10 = "replay_10";      // circular arrow with "10", goes back 10 seconds
    public static final String FORWARD_10 = "forward_10";    // circular arrow with "10", skips 10 seconds
    public static final String HEART = "heart";
    public static final String HEART_OUTLINE = "heart_outline";
    public static final String SKIP_NEXT = "skip_next";
    public static final String ADD = "add";
    public static final String CHECK = "check";
    public static final String DROP_DOWN = "drop_down";
    public static final String DOWNLOAD = "download";
    public static final String DOWNLOAD_DONE = "download_done";
    public static final String DELETE = "delete";
    public static final String BELL = "bell";
    public static final String BELL_OUTLINE = "bell_outline";
    public static final String SHUFFLE = "shuffle";
    public static final String CALENDAR = "calendar";

    private static final Map<String, Image> CACHE = new ConcurrentHashMap<>();

    private final ImageView view = new ImageView();
    private final ObjectProperty<Paint> fill = new SimpleObjectProperty<>(Color.WHITE);
    private final double size;

    public Icon(String name, double size, Paint fill) {
        this.size = size;
        view.setFitWidth(size);
        view.setFitHeight(size);
        view.setPreserveRatio(true);
        view.setSmooth(true);
        getChildren().add(view);
        setPrefSize(size, size);
        setMinSize(size, size);
        setMaxSize(size, size);
        setMouseTransparent(true);
        this.fill.addListener((o, a, b) -> tint());
        this.fill.set(fill);
        setPath(name);
    }

    /** URL of the picture file for an icon name (what the Image is loaded from). */
    public static String urlOf(String name) {
        URL url = Icon.class.getResource("/icons/" + name + ".png");
        return url == null ? null : url.toExternalForm();
    }

    /** Swap the picture (kept under the old name so existing code that changes icons keeps working). */
    public void setPath(String name) {
        Image img = CACHE.computeIfAbsent(name, n -> {
            String url = urlOf(n);
            if (url == null) {
                System.err.println("[icon] missing picture /icons/" + n + ".png");
                return null;
            }
            return new Image(url);               // 96x96 source, scaled down smoothly by the ImageView
        });
        view.setImage(img);
    }

    public ObjectProperty<Paint> fillProperty() { return fill; }

    /** White pictures stay as they are; any other colour is painted over the visible pixels only (SRC_ATOP). */
    private void tint() {
        Paint p = fill.get();
        if (p instanceof Color c && !Color.WHITE.equals(c)) {
            view.setEffect(new Blend(BlendMode.SRC_ATOP, null, new ColorInput(0, 0, size, size, c)));
        } else {
            view.setEffect(null);
        }
    }
}
