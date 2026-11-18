package com.streamverse.ui.controls;

import com.streamverse.ui.Theme;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom "trailer is loading" animation (built only from basic shapes + transitions):
 * a spinning film reel, a counter-rotating arc, two pulsing ripples, a sweeping light bar and
 * animated "..." dots. It only runs while visible, so it costs nothing when hidden.
 */
public class LoadingAnimation extends VBox {
    private final List<Animation> animations = new ArrayList<>();
    private final Label caption = new Label("LOADING TRAILER");
    private final Label subtitle = new Label();

    public LoadingAnimation() {
        super(20);
        setAlignment(Pos.CENTER);
        setMouseTransparent(false);
        setStyle("-fx-background-color: rgba(0,0,0,0.92);");

        // ---- film reel -------------------------------------------------------------
        Group reel = new Group();
        Circle body = new Circle(0, 0, 42, Color.web("#262626"));
        body.setStroke(Theme.ACCENT);
        body.setStrokeWidth(4);
        reel.getChildren().add(body);
        for (int i = 0; i < 6; i++) {
            double a = Math.toRadians(i * 60);
            Circle hole = new Circle(Math.cos(a) * 24, Math.sin(a) * 24, 10, Color.BLACK);
            hole.setStroke(Color.web("#3a3a3a"));
            reel.getChildren().add(hole);
        }
        reel.getChildren().add(new Circle(0, 0, 7, Theme.ACCENT));

        RotateTransition spin = new RotateTransition(Duration.seconds(2.4), reel);
        spin.setByAngle(360);
        spin.setInterpolator(Interpolator.LINEAR);
        spin.setCycleCount(Animation.INDEFINITE);

        // ---- counter-rotating arc (animating startAngle keeps the pivot at the circle centre) ----
        Arc ring = new Arc(0, 0, 58, 58, 0, 105);
        ring.setType(ArcType.OPEN);
        ring.setFill(Color.TRANSPARENT);
        ring.setStroke(Color.WHITE);
        ring.setStrokeWidth(3);
        ring.setStrokeLineCap(StrokeLineCap.ROUND);
        Timeline ringSpin = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(ring.startAngleProperty(), 360)),
                new KeyFrame(Duration.seconds(1.5), new KeyValue(ring.startAngleProperty(), 0, Interpolator.LINEAR)));
        ringSpin.setCycleCount(Animation.INDEFINITE);

        // ---- ripples ---------------------------------------------------------------
        Circle r1 = ripple(), r2 = ripple();
        ParallelTransition p1 = ripplePulse(r1, 0), p2 = ripplePulse(r2, 0.8);

        Group stage = new Group(r1, r2, reel, ring);
        StackPane stageBox = new StackPane(stage);
        stageBox.setPrefSize(170, 170);
        stageBox.setMinSize(170, 170);
        stageBox.setMaxSize(170, 170);

        // ---- caption with animated dots ---------------------------------------------
        caption.setFont(Theme.font(FontWeight.BOLD, 15));
        caption.setTextFill(Color.WHITE);
        subtitle.setFont(Theme.font(FontWeight.NORMAL, 13));
        subtitle.setTextFill(Theme.MUTED);
        String[] dots = {"", ".", "..", "..."};
        int[] n = {0};
        Timeline dotTimer = new Timeline(new KeyFrame(Duration.millis(380), e -> {
            n[0] = (n[0] + 1) % dots.length;
            caption.setText("LOADING TRAILER" + dots[n[0]]);
        }));
        dotTimer.setCycleCount(Animation.INDEFINITE);

        // ---- sweeping light bar -----------------------------------------------------
        Pane track = new Pane();
        track.setPrefSize(190, 3);
        track.setMinSize(190, 3);
        track.setMaxSize(190, 3);
        track.setStyle("-fx-background-color: #2e2e2e; -fx-background-radius: 2;");
        track.setClip(new Rectangle(190, 3));
        Rectangle glow = new Rectangle(0, 0, 80, 3);
        glow.setFill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.TRANSPARENT), new Stop(0.5, Theme.ACCENT), new Stop(1, Color.TRANSPARENT)));
        track.getChildren().add(glow);
        Timeline sweep = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(glow.xProperty(), -80)),
                new KeyFrame(Duration.seconds(1.2), new KeyValue(glow.xProperty(), 190, Interpolator.EASE_BOTH)));
        sweep.setCycleCount(Animation.INDEFINITE);

        getChildren().addAll(stageBox, caption, subtitle, track);

        animations.addAll(List.of(spin, ringSpin, p1, p2, dotTimer, sweep));
        // run only while shown
        visibleProperty().addListener((o, was, now) -> { if (now) play(); else stop(); });
        if (isVisible()) play();
    }

    public void setMovieTitle(String title) { subtitle.setText(title); }

    private static Circle ripple() {
        Circle c = new Circle(0, 0, 42, Color.TRANSPARENT);
        c.setStroke(Theme.ACCENT);
        c.setStrokeWidth(2);
        c.setOpacity(0);
        return c;
    }

    private static ParallelTransition ripplePulse(Circle c, double delaySeconds) {
        ScaleTransition st = new ScaleTransition(Duration.seconds(1.6), c);
        st.setFromX(1); st.setFromY(1); st.setToX(1.9); st.setToY(1.9);
        FadeTransition ft = new FadeTransition(Duration.seconds(1.6), c);
        ft.setFromValue(0.75); ft.setToValue(0);
        ParallelTransition pt = new ParallelTransition(st, ft);
        pt.setDelay(Duration.seconds(delaySeconds));
        pt.setCycleCount(Animation.INDEFINITE);
        return pt;
    }

    private void play() { animations.forEach(Animation::play); }

    private void stop() { animations.forEach(Animation::stop); }
}
