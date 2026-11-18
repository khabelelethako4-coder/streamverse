package com.streamverse.ui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/** Base of every full page: a transparent vertical scroll area (so the stage backdrop shows) with a content column. */
public abstract class ScrollPage extends StackPane {
    protected final VBox content = new VBox(18);
    protected final ScrollPane scroll = new ScrollPane(content);
    private final DoubleProperty scrollY = new SimpleDoubleProperty();

    protected ScrollPage(Insets padding) {
        content.setPadding(padding);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setPannable(false);
        getChildren().add(scroll);
        scroll.vvalueProperty().addListener((o, a, b) -> scrollY.set(currentScrollY()));
    }

    protected ScrollPage() { this(new Insets(104, Theme.SIDE, 60, Theme.SIDE)); }

    /** How many pixels the page is scrolled down (the nav bar uses it to turn solid). */
    public DoubleProperty scrollYProperty() { return scrollY; }

    protected double currentScrollY() {
        double max = Math.max(0, content.getHeight() - scroll.getViewportBounds().getHeight());
        return scroll.getVvalue() * max;
    }

    protected static javafx.scene.control.Label heading(String text) {
        javafx.scene.control.Label l = new javafx.scene.control.Label(text);
        l.setFont(Theme.font(javafx.scene.text.FontWeight.EXTRA_BOLD, 38));
        l.setTextFill(javafx.scene.paint.Color.WHITE);
        l.setEffect(new javafx.scene.effect.DropShadow(10, javafx.scene.paint.Color.rgb(0, 0, 0, 0.8)));
        return l;
    }

    protected static javafx.scene.control.Label subtitle(String text) {
        javafx.scene.control.Label l = new javafx.scene.control.Label(text);
        l.setFont(Theme.font(javafx.scene.text.FontWeight.NORMAL, 15));
        l.setTextFill(Theme.MUTED);
        l.setWrapText(true);
        return l;
    }

    public void scrollToTop() { scroll.setVvalue(0); }

    /** Called each time the page is shown: refresh anything that may have changed while it was hidden. */
    public void onShow() { scrollToTop(); }
}
