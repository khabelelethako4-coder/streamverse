package com.streamverse.ui;

import com.streamverse.audio.SoundManager;
import com.streamverse.audio.SoundManager.Sfx;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.layout.StackPane;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

/** Short logo splash when the app starts (click to skip). */
public class IntroOverlay extends StackPane {
    private SequentialTransition sequence;

    public IntroOverlay() {
        setStyle("-fx-background-color: black;");
        Label logo = new Label("STREAMVERSE");
        logo.setFont(Theme.font(FontWeight.EXTRA_BOLD, 72));
        logo.setTextFill(Theme.ACCENT);
        logo.setEffect(new DropShadow(30, Color.rgb(229, 9, 20, 0.7)));
        logo.setOpacity(0);
        logo.setScaleX(0.8);
        logo.setScaleY(0.8);
        getChildren().addAll(new Backdrop(0.35, 0.80), logo);

        FadeTransition in = new FadeTransition(Duration.millis(700), logo);
        in.setToValue(1);
        ScaleTransition grow = new ScaleTransition(Duration.millis(1400), logo);
        grow.setToX(1.05);
        grow.setToY(1.05);
        FadeTransition out = new FadeTransition(Duration.millis(550), this);
        out.setToValue(0);
        sequence = new SequentialTransition(new ParallelTransition(in, grow), new PauseTransition(Duration.millis(500)), out);
        sequence.setOnFinished(e -> setVisible(false));
        setOnMouseClicked(e -> { sequence.stop(); setVisible(false); });
    }

    public void play() {
        SoundManager.get().play(Sfx.OPEN);
        sequence.play();
    }
}
