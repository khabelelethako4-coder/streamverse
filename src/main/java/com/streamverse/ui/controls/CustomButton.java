package com.streamverse.ui.controls;

import com.streamverse.audio.SoundManager;
import com.streamverse.audio.SoundManager.Sfx;
import com.streamverse.ui.Theme;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

/**
 * A fully custom button. The assignment forbids javafx.scene.control.Button, so this is a
 * StackPane = coloured Region (background) + optional vector Icon + optional Label.
 * It supplies everything a button needs: hover/pressed colour fades, scale "pop",
 * click + hover sounds, keyboard activation (Enter/Space) and a Runnable action.
 */
public class CustomButton extends StackPane {

    /** Visual flavours. Colours are (normal, hover, pressed, text). */
    public enum Style {
        PRIMARY(Color.web("#ffffff"), Color.web("#e0e0e0"), Color.web("#bdbdbd"), Color.BLACK, 6),
        SECONDARY(Color.web("#6d6d6e", 0.70), Color.web("#6d6d6e", 0.45), Color.web("#6d6d6e", 0.30), Color.WHITE, 6),
        ACCENT(Theme.ACCENT, Color.web("#ff2b36"), Theme.ACCENT_DARK, Color.WHITE, 6),
        ICON(Color.web("#2a2a2a", 0.60), Color.web("#ffffff", 0.24), Color.web("#ffffff", 0.12), Color.WHITE, 999);

        final Color normal, hover, pressed, text;
        final double radius;

        Style(Color normal, Color hover, Color pressed, Color text, double radius) {
            this.normal = normal; this.hover = hover; this.pressed = pressed;
            this.text = text; this.radius = radius;
        }
    }

    private final Style style;
    private final Region background = new Region();
    private final Label label = new Label();
    private final HBox row = new HBox(8);
    private Icon icon;
    private final ObjectProperty<Color> fill = new SimpleObjectProperty<>();
    private final ScaleTransition scaleAnim = new ScaleTransition(Duration.millis(120), this);
    private Timeline colorAnim;
    private boolean hovering, pressing;
    private Runnable action;

    public CustomButton(String text, String iconPath, Style style) {
        this.style = style;
        fill.set(style.normal);

        // the background colour is bound to the animated 'fill' property
        background.backgroundProperty().bind(Bindings.createObjectBinding(
                () -> new Background(new BackgroundFill(fill.get(), new CornerRadii(style.radius), Insets.EMPTY)), fill));

        row.setAlignment(Pos.CENTER);
        if (iconPath != null) {
            icon = new Icon(iconPath, style == Style.ICON ? 22 : 24, style.text);
            row.getChildren().add(icon);
        }
        if (text != null && !text.isEmpty()) {
            label.setText(text);
            label.setFont(Theme.font(FontWeight.BOLD, 17));
            label.setTextFill(style.text);
            row.getChildren().add(label);
        }
        getChildren().addAll(background, row);

        if (style == Style.ICON) {
            setPrefSize(42, 42); setMinSize(42, 42); setMaxSize(42, 42);
        } else {
            row.setPadding(new Insets(10, 26, 10, 22));   // padding on the content row so the background Region covers it
            setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
            setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        }
        setCursor(Cursor.HAND);
        setFocusTraversable(true);

        setOnMouseEntered(e -> { hovering = true; refresh(); SoundManager.get().play(Sfx.HOVER); });
        setOnMouseExited(e -> { hovering = false; pressing = false; refresh(); });
        setOnMousePressed(e -> { pressing = true; refresh(); });
        setOnMouseReleased(e -> { pressing = false; refresh(); });
        setOnMouseClicked(e -> { if (e.getButton() == MouseButton.PRIMARY) fire(); });
        setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.SPACE) { fire(); e.consume(); }
        });
    }

    public CustomButton(String text, Style style) { this(text, null, style); }

    public CustomButton setOnAction(Runnable action) { this.action = action; return this; }

    public void setIconPath(String path) { if (icon != null) icon.setPath(path); }

    public void setText(String text) { label.setText(text); }

    private void fire() {
        SoundManager.get().play(Sfx.CLICK);
        if (action != null) action.run();
    }

    /** Animate colour and scale towards the state implied by hovering/pressing. */
    private void refresh() {
        Color target = pressing ? style.pressed : hovering ? style.hover : style.normal;
        if (colorAnim != null) colorAnim.stop();
        colorAnim = new Timeline(new KeyFrame(Duration.millis(130), new KeyValue(fill, target)));
        colorAnim.play();

        double hoverScale = style == Style.ICON ? 1.12 : 1.05;
        double s = pressing ? 0.94 : hovering ? hoverScale : 1.0;
        scaleAnim.stop();
        scaleAnim.setToX(s);
        scaleAnim.setToY(s);
        scaleAnim.playFromStart();
    }
}
