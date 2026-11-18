package com.streamverse.ui.controls;

import com.streamverse.audio.SoundManager;
import com.streamverse.audio.SoundManager.Sfx;
import com.streamverse.ui.Theme;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

/** Text link for the top navigation: dims when idle, brightens on hover, red underline slides in when active. */
public class NavLink extends VBox {
    private final Label label;
    private final Region underline = new Region();
    private boolean active, hovering;
    private Timeline anim;

    private Icon trailing;

    public NavLink(String text, Runnable onClick) {
        this(text, null, onClick);
    }

    public NavLink(String text, String trailingIcon, Runnable onClick) {
        super(3);
        setAlignment(Pos.CENTER);
        label = new Label(text);
        label.setFont(Theme.font(FontWeight.SEMI_BOLD, 14));
        label.setTextFill(Theme.MUTED);
        underline.setPrefHeight(3);
        underline.setMaxHeight(3);
        underline.setStyle("-fx-background-color: #E50914; -fx-background-radius: 2;");
        underline.setScaleX(0);
        javafx.scene.Node top = label;
        if (trailingIcon != null) {
            trailing = new Icon(trailingIcon, 20, Theme.MUTED);
            javafx.scene.layout.HBox h = new javafx.scene.layout.HBox(0, label, trailing);
            h.setAlignment(Pos.CENTER);
            top = h;
        }
        getChildren().addAll(top, underline);
        setCursor(Cursor.HAND);

        setOnMouseEntered(e -> { hovering = true; update(); SoundManager.get().play(Sfx.HOVER); });
        setOnMouseExited(e -> { hovering = false; update(); });
        setOnMouseClicked(e -> { SoundManager.get().play(Sfx.NAV); onClick.run(); });
    }

    public String getText() { return label.getText(); }

    public void setActive(boolean value) {
        if (active != value) { active = value; update(); }
    }

    private void update() {
        Color text = (active || hovering) ? Color.WHITE : Theme.MUTED;
        if (anim != null) anim.stop();
        anim = new Timeline(new KeyFrame(Duration.millis(160),
                new KeyValue(label.textFillProperty(), text),
                trailing != null ? new KeyValue(trailing.fillProperty(), (javafx.scene.paint.Paint) text) : new KeyValue(underline.opacityProperty(), 1.0),
                new KeyValue(underline.scaleXProperty(), active ? 1.0 : hovering ? 0.35 : 0.0)));
        anim.play();
    }
}
