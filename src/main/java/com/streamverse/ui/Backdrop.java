package com.streamverse.ui;

import com.streamverse.ui.controls.CoverImage;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

import java.net.URL;

/**
 * The theatre-stage picture used as the page background. It stays fixed while the page scrolls over it and is
 * covered by a dark gradient (lighter at the top, darker at the bottom) so thumbnails and text remain readable.
 */
public class Backdrop extends StackPane {

    /** Dark tone mixed into the picture; also what the hero banner fades into. */
    public static final Color TINT = Color.rgb(14, 6, 8);

    public Backdrop(double topAlpha, double bottomAlpha) {
        CoverImage picture = new CoverImage();
        URL url = Backdrop.class.getResource("/images/stage.jpg");
        if (url != null) picture.setImage(new Image(url.toExternalForm()), false);

        Region shade = new Region();
        shade.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Theme.alpha(TINT, topAlpha)),
                        new Stop(1, Theme.alpha(TINT, bottomAlpha))),
                CornerRadii.EMPTY, Insets.EMPTY)));

        getChildren().addAll(picture, shade);
        setMouseTransparent(true);
        setStyle("-fx-background-color: #0e0608;");
    }
}
