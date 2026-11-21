package com.streamverse.ui;

import javafx.beans.property.DoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Labeled;
import javafx.scene.control.TextInputControl;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/** Applies an adjustable font scale to existing and subsequently added scene-graph nodes. */
final class FontScaleSupport {
    private static final Object BASE_FONT_KEY = new Object();

    private final Map<Parent, ListChangeListener<Node>> listeners = new HashMap<>();
    private double scale = 1;

    void attach(Node root, DoubleProperty fontScale) {
        scale = fontScale.get();
        if (root.getScene() != null) root.applyCss();
        watch(root);
        fontScale.addListener((observable, oldValue, newValue) -> {
            scale = newValue.doubleValue();
            refresh(root);
        });
        refresh(root);
    }

    private void watch(Node node) {
        if (node instanceof Labeled labeled) {
            setScaledFont(node, labeled.getFont(), labeled::setFont);
        } else if (node instanceof TextInputControl input) {
            setScaledFont(node, input.getFont(), input::setFont);
        }

        if (node instanceof Parent parent && !listeners.containsKey(parent)) {
            ListChangeListener<Node> listener = change -> {
                while (change.next()) {
                    if (change.wasRemoved()) {
                        for (Node removed : change.getRemoved()) unwatch(removed);
                    }
                    if (change.wasAdded()) {
                        for (Node added : change.getAddedSubList()) watch(added);
                    }
                }
            };
            listeners.put(parent, listener);
            parent.getChildrenUnmodifiable().addListener(listener);
            for (Node child : parent.getChildrenUnmodifiable()) watch(child);
        }
    }

    private void unwatch(Node node) {
        if (node instanceof Parent parent) {
            ListChangeListener<Node> listener = listeners.remove(parent);
            if (listener != null) parent.getChildrenUnmodifiable().removeListener(listener);
            for (Node child : parent.getChildrenUnmodifiable()) unwatch(child);
        }
    }

    private void refresh(Node node) {
        if (node instanceof Labeled labeled) {
            setScaledFont(node, labeled.getFont(), labeled::setFont);
        } else if (node instanceof TextInputControl input) {
            setScaledFont(node, input.getFont(), input::setFont);
        }
        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) refresh(child);
        }
    }

    private void setScaledFont(Node node, Font font, java.util.function.Consumer<Font> setter) {
        if (font == null) return;
        Map<Object, Object> properties = node.getProperties();
        if (scale == 1.0 && !properties.containsKey(BASE_FONT_KEY)) return;
        if (node.getScene() != null) node.applyCss();
        Font current = currentFont(node, font);
        Font base = (Font) properties.computeIfAbsent(BASE_FONT_KEY, key -> current);
        String style = base.getStyle().toLowerCase(Locale.ROOT);
        FontWeight weight = style.contains("black") ? FontWeight.BLACK
                : style.contains("extra bold") ? FontWeight.EXTRA_BOLD
                : style.contains("semi bold") ? FontWeight.SEMI_BOLD
                : style.contains("bold") ? FontWeight.BOLD
                : style.contains("medium") ? FontWeight.MEDIUM
                : style.contains("light") ? FontWeight.LIGHT
                : style.contains("thin") ? FontWeight.THIN
                : FontWeight.NORMAL;
        FontPosture posture = style.contains("italic") || style.contains("oblique")
                ? FontPosture.ITALIC : FontPosture.REGULAR;
        setter.accept(Font.font(base.getFamily(), weight, posture, base.getSize() * scale));
    }

    private static Font currentFont(Node node, Font fallback) {
        if (node instanceof Labeled labeled) return labeled.getFont();
        if (node instanceof TextInputControl input) return input.getFont();
        return fallback;
    }
}
