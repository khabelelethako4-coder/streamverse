package com.streamverse.ui;

import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/** One place for the colours, fonts and layout constants so the whole UI stays consistent. */
public final class Theme {
    private Theme() { }

    public static final Color BG = Color.web("#141414");
    public static final Color ACCENT = Color.web("#E50914");
    public static final Color ACCENT_DARK = Color.web("#B20710");
    public static final Color TEXT = Color.WHITE;
    public static final Color TEXT_DIM = Color.web("#e5e5e5");
    public static final Color MUTED = Color.web("#b3b3b3");

    /** Colour the hero banner fades into: the stage picture after the dark tint has been applied. */
    public static final Color HERO_FADE = Color.rgb(30, 11, 9);

    /** Left/right page margin shared by the nav bar, hero and rows. */
    public static final double SIDE = 56;

    public static Font font(FontWeight weight, double size) {
        return Font.font("System", weight, size);
    }

    /** Colourful, deterministic gradient shown behind a thumbnail until (or if) its image loads. */
    public static LinearGradient placeholder(String seed) {
        double hue = Math.abs(seed.hashCode()) % 360;
        return new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.hsb(hue, 0.55, 0.42)),
                new Stop(1, Color.hsb((hue + 40) % 360, 0.65, 0.16)));
    }

    public static Color alpha(Color c, double a) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), a);
    }
}
