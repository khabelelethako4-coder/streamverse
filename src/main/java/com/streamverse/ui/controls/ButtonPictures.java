package com.streamverse.ui.controls;

import javafx.scene.image.Image;

/**
 * The register of every PICTURE used on a button. Each constant knows its file name and has functions that return the
 * picture's URL and the loaded Image, so the rest of the code just calls e.g. {@code ButtonPictures.PLAY.url()}.
 *
 *   ButtonPictures.PLAY.url()   ->  file:/.../icons/play.png        (jar:file:...!/icons/play.png inside a packaged jar)
 *   ButtonPictures.PLAY.image() ->  the picture, loaded from that URL (cached)
 *
 * {@link #logAll()} prints every name and URL (the app calls it at start-up).
 */
public enum ButtonPictures {
    PLAY(Icon.PLAY), PAUSE(Icon.PAUSE), REWIND_10(Icon.REWIND_10), FORWARD_10(Icon.FORWARD_10), NEXT(Icon.SKIP_NEXT),
    CLOSE(Icon.CLOSE), VOLUME(Icon.VOLUME), MUTE(Icon.MUTE), FULLSCREEN(Icon.FULLSCREEN), EXIT_FULLSCREEN(Icon.EXIT_FULLSCREEN),
    WATCH_ON_YOUTUBE(Icon.EXTERNAL), SEARCH(Icon.SEARCH), INFO(Icon.INFO), TRY_AGAIN(Icon.REPLAY),
    HEART(Icon.HEART), HEART_OUTLINE(Icon.HEART_OUTLINE), ADD(Icon.ADD), CHECK(Icon.CHECK), GENRES_ARROW(Icon.DROP_DOWN),
    ARROW_LEFT(Icon.CHEVRON_LEFT), ARROW_RIGHT(Icon.CHEVRON_RIGHT),
    DOWNLOAD(Icon.DOWNLOAD), DOWNLOADED(Icon.DOWNLOAD_DONE), DELETE(Icon.DELETE),
    REMIND_ON(Icon.BELL), REMIND_OFF(Icon.BELL_OUTLINE), SURPRISE_ME(Icon.SHUFFLE), CALENDAR(Icon.CALENDAR);

    private final String file;
    private Image cached;

    ButtonPictures(String file) { this.file = file; }

    /** Name of the picture file without ".png". */
    public String file() { return file; }

    /** URL the picture is loaded from. */
    public String url() { return Icon.urlOf(file); }

    /** The picture itself, loaded from {@link #url()} (once). */
    public Image image() {
        if (cached == null && url() != null) cached = new Image(url());
        return cached;
    }

    /** Prints "NAME -> URL" for every button picture. */
    public static void logAll() {
        System.out.println("[pictures] button pictures and their URLs:");
        for (ButtonPictures p : values()) System.out.printf("[pictures]   %-17s %s%n", p.name(), p.url());
    }
}
