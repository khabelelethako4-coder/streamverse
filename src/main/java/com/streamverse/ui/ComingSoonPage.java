package com.streamverse.ui;

import com.streamverse.audio.SoundManager;
import com.streamverse.library.UserLibrary;
import com.streamverse.model.Movie;
import com.streamverse.model.Upcoming;
import com.streamverse.net.TrailerResolver;
import com.streamverse.ui.controls.Buttons;
import com.streamverse.ui.controls.CoverImage;
import com.streamverse.ui.controls.CustomButton;
import com.streamverse.ui.controls.Icon;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Coming Soon: movies that are not out yet, grouped by month, each with a live countdown, a "Remind me" bell and a trailer.
 * Trailers are found on YouTube when you press the button (TrailerResolver.findTrailer) and play inside the app; if none
 * can be found, the YouTube search opens in the browser instead.
 */
public class ComingSoonPage extends ScrollPage {
    private static final DateTimeFormatter LONG = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter MONTH = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);

    private final Consumer<Movie> onPlay;
    private final HostServices hostServices;
    private final List<Card> cards = new ArrayList<>();
    private boolean thumbsRequested;

    public ComingSoonPage(List<Upcoming> upcoming, Consumer<Movie> onPlay, HostServices hostServices) {
        this.onPlay = onPlay;
        this.hostServices = hostServices;

        content.getChildren().add(heading("Coming Soon"));
        content.getChildren().add(subtitle("Planned release dates \u2013 studios sometimes move them. Press the bell to get a reminder "
                + "when the opening is close."));

        Upcoming next = upcoming.stream().filter(u -> u.daysToGo() >= 0).findFirst().orElse(null);
        if (next != null) content.getChildren().add(nextUp(next));

        String lastMonth = "";
        for (Upcoming u : upcoming) {
            String month = u.releaseDate().format(MONTH);
            if (!month.equals(lastMonth)) {
                Label m = new Label(month);
                m.setFont(Theme.font(FontWeight.BOLD, 22));
                m.setTextFill(Color.WHITE);
                VBox.setMargin(m, new Insets(18, 0, 0, 0));
                content.getChildren().add(m);
                lastMonth = month;
            }
            Card c = new Card(u);
            cards.add(c);
            content.getChildren().add(c);
        }
    }

    /** Banner for the nearest release. */
    private HBox nextUp(Upcoming u) {
        Icon cal = new Icon(Icon.CALENDAR, 34, Theme.ACCENT);
        Label tag = new Label("NEXT UP");
        tag.setFont(Theme.font(FontWeight.BOLD, 12));
        tag.setTextFill(Theme.ACCENT);
        Label title = new Label(u.title());
        title.setFont(Theme.font(FontWeight.EXTRA_BOLD, 26));
        title.setTextFill(Color.WHITE);
        Label when = new Label(u.countdown() + "  \u2022  " + u.releaseDate().format(LONG));
        when.setFont(Theme.font(FontWeight.SEMI_BOLD, 15));
        when.setTextFill(Theme.TEXT_DIM);
        HBox box = new HBox(18, cal, new VBox(2, tag, title, when));
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(18, 26, 18, 26));
        box.setStyle("-fx-background-color: rgba(0,0,0,0.55); -fx-background-radius: 12; "
                + "-fx-border-color: rgba(229,9,20,0.7); -fx-border-radius: 12;");
        VBox.setMargin(box, new Insets(8, 0, 0, 0));
        return box;
    }

    /** Looks up the trailers (and so the pictures) of the soonest films, one after another, the first time the page opens. */
    @Override
    public void onShow() {
        scrollToTop();
        if (thumbsRequested) return;
        thumbsRequested = true;
        for (int i = 0; i < Math.min(6, cards.size()); i++) cards.get(i).findPicture();
    }

    // ================================================================== one film

    private final class Card extends HBox {
        private final Upcoming u;
        private final CoverImage picture = new CoverImage();
        private final CustomButton remind;
        private String trailerId;

        Card(Upcoming u) {
            super(22);
            this.u = u;
            setAlignment(Pos.CENTER_LEFT);
            setPadding(new Insets(14));
            setStyle("-fx-background-color: rgba(0,0,0,0.45); -fx-background-radius: 14; -fx-border-color: rgba(255,255,255,0.10); -fx-border-radius: 14;");

            // ---- date block
            Label mon = new Label(u.releaseDate().format(DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH)).toUpperCase(Locale.ROOT));
            mon.setFont(Theme.font(FontWeight.BOLD, 14));
            mon.setTextFill(Theme.ACCENT);
            Label day = new Label(String.valueOf(u.releaseDate().getDayOfMonth()));
            day.setFont(Theme.font(FontWeight.EXTRA_BOLD, 40));
            day.setTextFill(Color.WHITE);
            VBox date = new VBox(0, mon, day);
            date.setAlignment(Pos.CENTER);
            date.setMinWidth(64);

            // ---- picture (gradient + title until the trailer's picture is found)
            StackPane thumb = new StackPane();
            thumb.setMinSize(272, 153); thumb.setPrefSize(272, 153); thumb.setMaxSize(272, 153);
            Rectangle clip = new Rectangle(272, 153);
            clip.setArcWidth(16); clip.setArcHeight(16);
            thumb.setClip(clip);
            Region bg = new Region();
            bg.setBackground(new javafx.scene.layout.Background(new javafx.scene.layout.BackgroundFill(
                    Theme.placeholder(u.title()), javafx.scene.layout.CornerRadii.EMPTY, Insets.EMPTY)));
            Label ghost = new Label(u.title());
            ghost.setFont(Theme.font(FontWeight.BOLD, 16));
            ghost.setTextFill(Color.rgb(255, 255, 255, 0.6));
            ghost.setWrapText(true);
            ghost.setMaxWidth(220);
            ghost.setAlignment(Pos.CENTER);
            ghost.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            thumb.getChildren().addAll(bg, ghost, picture);

            // ---- text
            Label title = new Label(u.title());
            title.setFont(Theme.font(FontWeight.EXTRA_BOLD, 24));
            title.setTextFill(Color.WHITE);
            title.setWrapText(true);
            Label meta = new Label(u.genre() + "  \u2022  " + u.releaseDate().format(LONG));
            meta.setFont(Theme.font(FontWeight.SEMI_BOLD, 13));
            meta.setTextFill(Theme.MUTED);
            Label desc = new Label(u.description());
            desc.setFont(Theme.font(FontWeight.NORMAL, 15));
            desc.setTextFill(Theme.TEXT_DIM);
            desc.setWrapText(true);
            Label countdown = new Label(u.countdown());
            countdown.setFont(Theme.font(FontWeight.BOLD, 13));
            countdown.setTextFill(Color.WHITE);
            countdown.setPadding(new Insets(4, 12, 4, 12));
            countdown.setStyle("-fx-background-color: " + (u.daysToGo() <= 14 ? "#E50914" : "rgba(255,255,255,0.18)") + "; -fx-background-radius: 20;");

            remind = Buttons.remindMe(this::toggleReminder);
            CustomButton watch = Buttons.watchTrailer(this::watchTrailer);
            HBox buttons = new HBox(12, watch, remind);
            buttons.setPadding(new Insets(6, 0, 0, 0));

            VBox info = new VBox(6, new HBox(12, countdown), title, meta, desc, buttons);
            HBox.setHgrow(info, Priority.ALWAYS);
            getChildren().addAll(date, thumb, info);
            refreshReminder();
        }

        private void toggleReminder() {
            boolean now = UserLibrary.get().toggleReminder(u.slug());
            Toast.show(now ? "We'll remind you before \u201C" + u.title() + "\u201D opens (" + u.releaseDate().format(LONG) + ")"
                           : "Reminder removed for \u201C" + u.title() + "\u201D");
            refreshReminder();
        }

        private void refreshReminder() {
            boolean on = UserLibrary.get().hasReminder(u.slug());
            remind.setText(on ? "Reminder set" : "Remind me");
            remind.setIconPath(on ? Icon.BELL : Icon.BELL_OUTLINE);
        }

        /** Finds the trailer's id (cached after the first time) and shows its picture. */
        void findPicture() {
            TrailerResolver.get().findTrailer(u.slug(), u.title(), u.releaseDate().getYear()).thenAccept(found ->
                found.ifPresent(id -> Platform.runLater(() -> {
                    trailerId = id;
                    picture.setImage(new Image(Movie.IMAGE_HOST + "/vi/" + id + "/hqdefault.jpg", 0, 0, true, true, true), true);
                })));
        }

        private void watchTrailer() {
            if (trailerId != null) { play(trailerId); return; }
            Toast.show("Looking for the trailer of \u201C" + u.title() + "\u201D\u2026");
            TrailerResolver.get().findTrailer(u.slug(), u.title(), u.releaseDate().getYear()).thenAccept(found ->
                Platform.runLater(() -> {
                    if (found.isPresent()) { trailerId = found.get(); play(trailerId); }
                    else {
                        hostServices.showDocument(u.youtubeSearchUrl());
                        Toast.show("No embeddable trailer found \u2013 opened the YouTube search in your browser");
                    }
                }));
        }

        private void play(String id) {
            // a temporary Movie so the normal player can show it
            onPlay.accept(new Movie(u.title(), u.releaseDate().getYear(), "Soon", u.genre(), u.description(), id));
        }
    }
}
