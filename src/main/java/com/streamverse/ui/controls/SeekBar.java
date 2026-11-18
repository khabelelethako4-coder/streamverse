package com.streamverse.ui.controls;

import com.streamverse.ui.Theme;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Cursor;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.function.DoubleConsumer;

/** Custom progress/seek slider (replaces javafx Slider): click or drag to seek. */
public class SeekBar extends Pane {
    private final Region track = new Region();
    private final Region fill = new Region();
    private final Circle knob = new Circle(6, Color.WHITE);
    private final DoubleProperty progress = new SimpleDoubleProperty(0);
    private final DoubleProperty thickness = new SimpleDoubleProperty(4);
    private boolean dragging;
    private DoubleConsumer onSeek = f -> { };

    public SeekBar() {
        setPrefHeight(22);
        setMinHeight(22);
        setCursor(Cursor.HAND);
        track.setStyle("-fx-background-color: rgba(255,255,255,0.28); -fx-background-radius: 3;");
        fill.setStyle("-fx-background-color: #E50914; -fx-background-radius: 3;");
        knob.setScaleX(0); knob.setScaleY(0);
        getChildren().addAll(track, fill, knob);

        progress.addListener((o, a, b) -> requestLayout());
        thickness.addListener((o, a, b) -> requestLayout());

        setOnMouseEntered(e -> grow(true));
        setOnMouseExited(e -> { if (!dragging) grow(false); });
        setOnMousePressed(e -> { dragging = true; set(e.getX()); });
        setOnMouseDragged(e -> set(e.getX()));
        setOnMouseReleased(e -> {
            dragging = false;
            onSeek.accept(progress.get());
            if (!isHover()) grow(false);
        });
    }

    public void setOnSeek(DoubleConsumer c) { this.onSeek = c; }

    /** Called by the player every 250 ms; ignored while the user is dragging. */
    public void setProgress(double fraction) {
        if (!dragging) progress.set(Math.max(0, Math.min(1, fraction)));
    }

    private void set(double x) {
        progress.set(Math.max(0, Math.min(1, getWidth() <= 0 ? 0 : x / getWidth())));
    }

    private void grow(boolean on) {
        new Timeline(new KeyFrame(Duration.millis(120),
                new KeyValue(thickness, on ? 6 : 4),
                new KeyValue(knob.scaleXProperty(), on ? 1 : 0),
                new KeyValue(knob.scaleYProperty(), on ? 1 : 0))).play();
    }

    @Override
    protected void layoutChildren() {
        double w = getWidth(), h = getHeight(), t = thickness.get(), y = (h - t) / 2;
        track.resizeRelocate(0, y, w, t);
        fill.resizeRelocate(0, y, w * progress.get(), t);
        knob.setCenterX(w * progress.get());
        knob.setCenterY(h / 2);
    }
}
