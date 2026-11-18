package com.streamverse.ui.controls;

import com.streamverse.ui.controls.CustomButton.Style;

/**
 * Ready-made custom buttons. Each function takes its picture from {@link ButtonPictures} (by URL) and returns a
 * {@link CustomButton}: a coloured panel + that picture + optional text. There is no javafx Button anywhere.
 */
public final class Buttons {
    private Buttons() { }

    private static CustomButton round(ButtonPictures pic, Runnable action) {
        return new CustomButton(null, pic.file(), Style.ICON).setOnAction(action);
    }

    // ---- player controls (round picture buttons)
    public static CustomButton playPause(Runnable a) { return round(ButtonPictures.PAUSE, a); }
    public static CustomButton rewind10(Runnable a) { return round(ButtonPictures.REWIND_10, a); }
    public static CustomButton forward10(Runnable a) { return round(ButtonPictures.FORWARD_10, a); }
    public static CustomButton next(Runnable a) { return round(ButtonPictures.NEXT, a); }
    public static CustomButton close(Runnable a) { return round(ButtonPictures.CLOSE, a); }
    public static CustomButton mute(Runnable a) { return round(ButtonPictures.VOLUME, a); }
    public static CustomButton fullScreen(Runnable a) { return round(ButtonPictures.FULLSCREEN, a); }
    public static CustomButton watchOnYouTubeIcon(Runnable a) { return round(ButtonPictures.WATCH_ON_YOUTUBE, a); }
    public static CustomButton heart(Runnable a) { return round(ButtonPictures.HEART_OUTLINE, a); }

    // ---- picture + text buttons
    public static CustomButton playTrailer(Runnable a) { return new CustomButton("Play Trailer", ButtonPictures.PLAY.file(), Style.PRIMARY).setOnAction(a); }
    public static CustomButton watchTrailer(Runnable a) { return new CustomButton("Watch trailer", ButtonPictures.PLAY.file(), Style.PRIMARY).setOnAction(a); }
    public static CustomButton myList(Runnable a) { return new CustomButton("My List", ButtonPictures.ADD.file(), Style.SECONDARY).setOnAction(a); }
    public static CustomButton browse(Runnable a) { return new CustomButton("Browse Titles", ButtonPictures.INFO.file(), Style.SECONDARY).setOnAction(a); }
    public static CustomButton surpriseMe(Runnable a) { return new CustomButton("Surprise Me", ButtonPictures.SURPRISE_ME.file(), Style.SECONDARY).setOnAction(a); }
    public static CustomButton watchOnYouTube(Runnable a) { return new CustomButton("Watch on YouTube", ButtonPictures.WATCH_ON_YOUTUBE.file(), Style.ACCENT).setOnAction(a); }
    public static CustomButton tryAgain(Runnable a) { return new CustomButton("Try again", ButtonPictures.TRY_AGAIN.file(), Style.SECONDARY).setOnAction(a); }
    public static CustomButton remindMe(Runnable a) { return new CustomButton("Remind me", ButtonPictures.REMIND_OFF.file(), Style.SECONDARY).setOnAction(a); }
    public static CustomButton downloadAll(Runnable a) { return new CustomButton("Download My List", ButtonPictures.DOWNLOAD.file(), Style.PRIMARY).setOnAction(a); }
    public static CustomButton deleteAll(Runnable a) { return new CustomButton("Delete all", ButtonPictures.DELETE.file(), Style.SECONDARY).setOnAction(a); }
    public static CustomButton repairTrailers(Runnable a) { return new CustomButton("Repair trailers", ButtonPictures.TRY_AGAIN.file(), Style.SECONDARY).setOnAction(a); }
    public static CustomButton browseMovies(Runnable a) { return new CustomButton("Browse movies", ButtonPictures.SEARCH.file(), Style.ACCENT).setOnAction(a); }
}
