package com.streamverse.audio;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.media.AudioClip;

import java.net.URL;
import java.util.EnumMap;
import java.util.Map;

/**
 * Plays the short UI sound effects (hover tick, click, open/close sweeps...).
 * Singleton so every control can simply call {@code SoundManager.get().play(Sfx.CLICK)}.
 * All failures are swallowed on purpose: a machine without an audio device
 * must never crash the app.
 */
public final class SoundManager {

    public enum Sfx {
        HOVER("hover", 0.30), NAV("nav", 0.35), CLICK("click", 0.55),
        OPEN("open", 0.60), CLOSE("close", 0.50), ERROR("error", 0.55);

        final String file;
        final double volume;

        Sfx(String file, double volume) {
            this.file = file;
            this.volume = volume;
        }
    }

    private static final SoundManager INSTANCE = new SoundManager();
    private static final long HOVER_COOLDOWN_NS = 70_000_000L; // avoid machine-gun ticks

    private final Map<Sfx, AudioClip> clips = new EnumMap<>(Sfx.class);
    private final BooleanProperty enabled = new SimpleBooleanProperty(true);
    private long lastHover;

    private SoundManager() {
        for (Sfx sfx : Sfx.values()) {
            try {
                URL url = SoundManager.class.getResource("/sounds/" + sfx.file + ".wav");
                if (url != null) {
                    AudioClip clip = new AudioClip(url.toExternalForm());
                    clip.setVolume(sfx.volume);
                    clips.put(sfx, clip);
                }
            } catch (Throwable t) {
                System.err.println("[sound] could not load " + sfx.file + ": " + t);
            }
        }
    }

    public static SoundManager get() { return INSTANCE; }

    public void play(Sfx sfx) {
        if (!enabled.get()) return;
        if (sfx == Sfx.HOVER) {
            long now = System.nanoTime();
            if (now - lastHover < HOVER_COOLDOWN_NS) return;
            lastHover = now;
        }
        AudioClip clip = clips.get(sfx);
        if (clip != null) {
            try { clip.play(); } catch (Throwable ignored) { }
        }
    }

    public BooleanProperty enabledProperty() { return enabled; }
}
