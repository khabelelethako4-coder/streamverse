package com.streamverse.ui.controls;

import javafx.animation.FadeTransition;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Shows an image like CSS "background-size: cover": it fills the whole pane and crops the overflow
 * instead of stretching. Also removes YouTube's black letterbox bars from hqdefault thumbnails and
 * fades the picture in once it has finished loading from the network.
 */
public class CoverImage extends Pane {
    private final ImageView view = new ImageView();
    private Image image;
    private boolean letterboxed;

    public CoverImage() {
        view.setPreserveRatio(false);
        view.setSmooth(true);
        getChildren().add(view);
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(widthProperty());
        clip.heightProperty().bind(heightProperty());
        setClip(clip);
        setMouseTransparent(true);
    }

    public Image getImage() { return image; }

    public boolean isLetterboxed() { return letterboxed; }

    /** @param letterboxed true for YouTube hqdefault images (12.5 % black bar top and bottom) */
    public void setImage(Image img, boolean letterboxed) {
        this.image = img;
        this.letterboxed = letterboxed;
        view.setImage(img);
        if (img == null) return;
        if (img.getProgress() >= 1.0 && !img.isError()) {
            view.setOpacity(1);
        } else {
            view.setOpacity(0);
            img.progressProperty().addListener((o, a, p) -> {
                if (p.doubleValue() >= 1.0 && !img.isError() && img == image) {
                    requestLayout();
                    FadeTransition ft = new FadeTransition(Duration.millis(350), view);
                    ft.setToValue(1);
                    ft.play();
                }
            });
        }
        requestLayout();
    }

    @Override
    protected void layoutChildren() {
        double w = getWidth(), h = getHeight();
        if (w <= 0 || h <= 0) return;
        view.setFitWidth(w);
        view.setFitHeight(h);
        view.relocate(0, 0);
        if (image == null || image.getWidth() <= 0 || image.getHeight() <= 0) return;

        double sx = 0, sy = 0, sw = image.getWidth(), sh = image.getHeight();
        if (letterboxed) { sy = sh * 0.125; sh = sh * 0.75; }
        double boxAspect = w / h, srcAspect = sw / sh, vw, vh;
        if (srcAspect > boxAspect) { vh = sh; vw = vh * boxAspect; }
        else { vw = sw; vh = vw / boxAspect; }
        view.setViewport(new Rectangle2D(sx + (sw - vw) / 2, sy + (sh - vh) / 2, vw, vh));
    }
}
