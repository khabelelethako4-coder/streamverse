package com.streamverse;

import com.streamverse.data.MovieRepository;
import com.streamverse.data.SampleMovieRepository;
import com.streamverse.player.LocalPlayerServer;
import com.streamverse.ui.MainView;
import com.streamverse.ui.IntroOverlay;
import com.streamverse.ui.PlayerOverlay;
import com.streamverse.ui.Theme;
import com.streamverse.ui.Toast;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Application entry. Composition root: creates the catalogue, the home screen, the player overlay and
 * the intro splash, and stacks them in one StackPane (home at the bottom, intro on top).
 */
public class App extends Application {
    private static final String STYLESHEET_RESOURCE = "/css/style.css";
    private static final int INITIAL_WIDTH = 1280;
    private static final int INITIAL_HEIGHT = 760;
    private static final int MIN_WIDTH = 1180;
    private static final int MIN_HEIGHT = 680;
    private MainView mainView;

    @Override
    public void start(Stage stage) {
        MovieRepository repository = new SampleMovieRepository();

        PlayerOverlay player = new PlayerOverlay(getHostServices(), repository::nextInGenre);
        mainView = new MainView(repository, player::open, getHostServices());
        Toast toast = new Toast();
        IntroOverlay intro = new IntroOverlay();

        StackPane root = new StackPane(mainView, player, toast, intro);
        Scene scene = new Scene(root, INITIAL_WIDTH, INITIAL_HEIGHT, Theme.BG);
        URL stylesheet = getClass().getResource(STYLESHEET_RESOURCE);
        if (stylesheet == null) {
            throw new IllegalStateException("Missing application resource: " + STYLESHEET_RESOURCE);
        }
        scene.getStylesheets().add(stylesheet.toExternalForm());

        stage.setTitle("StreamVerse \u2013 Movie Trailers");
        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);
        stage.setScene(scene);
        root.applyCss();
        mainView.attachFontScaleTo(root);
        stage.show();
        Platform.runLater(root::requestFocus);       // don't start with the search box focused
        intro.play();
    }

    @Override
    public void stop() {
        if (mainView != null) mainView.dispose();
        LocalPlayerServer.get().stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
