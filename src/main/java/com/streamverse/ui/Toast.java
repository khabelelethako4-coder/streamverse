package com.streamverse.ui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

/** Small pill message at the bottom of the window ("Added to My List"). Call {@code Toast.show("...")} from anywhere. */
public class Toast extends StackPane {
    private static Toast instance;
    private final Label label = new Label();
    private SequentialTransition running;

    public Toast() {
        setPickOnBounds(false);
        setMouseTransparent(true);
        label.setFont(Theme.font(FontWeight.SEMI_BOLD, 15));
        label.setTextFill(Color.WHITE);
        label.setPadding(new Insets(12, 24, 12, 24));
        label.setStyle("-fx-background-color: rgba(32,32,32,0.97); -fx-background-radius: 30; "
                + "-fx-border-color: rgba(255,255,255,0.18); -fx-border-radius: 30;");
        label.setEffect(new DropShadow(18, Color.rgb(0, 0, 0, 0.7)));
        label.setOpacity(0);
        label.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        getChildren().add(label);
        StackPane.setAlignment(label, Pos.BOTTOM_CENTER);
        StackPane.setMargin(label, new Insets(0, 0, 44, 0));
        instance = this;
    }

    public static void show(String message) {
        if (instance != null) instance.display(message);
    }

    private void display(String message) {
        if (running != null) running.stop();
        label.setText(message);
        label.setTranslateY(14);
        FadeTransition in = new FadeTransition(Duration.millis(200), label);
        in.setToValue(1);
        TranslateTransition up = new TranslateTransition(Duration.millis(200), label);
        up.setToY(0);
        FadeTransition out = new FadeTransition(Duration.millis(350), label);
        out.setToValue(0);
        running = new SequentialTransition(new javafx.animation.ParallelTransition(in, up),
                new PauseTransition(Duration.millis(1900)), out);
        running.play();
    }
}
