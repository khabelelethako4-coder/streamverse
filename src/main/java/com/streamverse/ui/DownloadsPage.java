package com.streamverse.ui;

import com.streamverse.downloads.DownloadService;
import com.streamverse.library.UserLibrary;
import com.streamverse.model.Movie;
import com.streamverse.ui.controls.Buttons;
import com.streamverse.ui.controls.Icon;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * The Downloads page. A download saves a movie's pictures + details on this computer (see DownloadService) so it stays
 * in your library without internet; the trailer itself always streams from YouTube.
 */
public class DownloadsPage extends ScrollPage {
    private final UserLibrary library = UserLibrary.get();
    private final DownloadService service = DownloadService.get();
    private final Map<String, Movie> byKey;
    private final MovieGrid grid;
    private final Label info = subtitle("");
    private final HBox actions = new HBox(12);
    private final VBox empty = new VBox(14);
    private final ListChangeListener<String> listListener = c -> refresh();
    private final ChangeListener<Number> versionListener = (o, a, b) -> refresh();

    public DownloadsPage(Map<String, Movie> byKey, Consumer<Movie> onPlay, Runnable onBrowse) {
        this.byKey = byKey;
        grid = new MovieGrid(onPlay);

        Label note = subtitle("Downloads keep each movie's picture and details on this computer, so your library is still "
                + "there offline. Trailers are never stored \u2013 they always stream from YouTube.");
        actions.getChildren().addAll(
                Buttons.downloadAll(this::downloadMyList),
                Buttons.deleteAll(this::deleteAll));

        Icon big = new Icon(Icon.DOWNLOAD, 72, Color.rgb(255, 255, 255, 0.55));
        Label none = new Label("No downloads yet");
        none.setFont(Theme.font(FontWeight.BOLD, 26));
        none.setTextFill(Color.WHITE);
        Label how = subtitle("Tap the download arrow on any movie, or press \u201CDownload My List\u201D.");
        how.setTextAlignment(TextAlignment.CENTER);
        empty.getChildren().addAll(big, none, how, Buttons.browseMovies(onBrowse));
        empty.setAlignment(Pos.CENTER);
        empty.setPadding(new javafx.geometry.Insets(60, 0, 60, 0));

        content.getChildren().addAll(heading("Downloads"), info, note, actions, grid, empty);
        library.downloads().addListener(listListener);
        library.favorites().addListener(listListener);
        service.versionProperty().addListener(versionListener);
        refresh();
    }

    private void downloadMyList() {
        List<Movie> list = new ArrayList<>();
        for (String k : library.favorites()) if (byKey.get(k) != null) list.add(byKey.get(k));
        if (list.isEmpty()) { Toast.show("Your list is empty \u2013 heart some movies first"); return; }
        int n = service.downloadAll(list);
        Toast.show(n == 0 ? "Everything in My List is already downloaded" : "Downloading " + n + " movie" + (n == 1 ? "" : "s") + "\u2026");
    }

    private void deleteAll() {
        List<Movie> list = downloaded();
        if (list.isEmpty()) { Toast.show("There is nothing to delete"); return; }
        service.removeAll(list);
        Toast.show("Deleted " + list.size() + " download" + (list.size() == 1 ? "" : "s"));
    }

    private List<Movie> downloaded() {
        List<Movie> list = new ArrayList<>();
        for (String k : service.activeKeys()) if (byKey.get(k) != null) list.add(byKey.get(k));      // still downloading first
        for (String k : library.downloads()) {
            Movie m = byKey.get(k);
            if (m != null && !list.contains(m)) list.add(m);
        }
        return list;
    }

    private void refresh() {
        List<Movie> movies = downloaded();
        boolean any = !movies.isEmpty();
        long done = library.downloads().size();
        info.setText(any
                ? done + (done == 1 ? " download" : " downloads") + "  \u2022  " + DownloadService.humanSize(service.totalBytes()) + " used on this computer"
                : "Nothing downloaded");
        grid.setMovies(movies);
        grid.setVisible(any); grid.setManaged(any);
        empty.setVisible(!any); empty.setManaged(!any);
    }

    @Override public void onShow() { refresh(); scrollToTop(); }

    public void dispose() {
        library.downloads().removeListener(listListener);
        library.favorites().removeListener(listListener);
        service.versionProperty().removeListener(versionListener);
    }
}
