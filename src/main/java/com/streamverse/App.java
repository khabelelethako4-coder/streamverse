package com.streamverse;

import com.streamverse.data.MovieRepository;
import com.streamverse.data.SampleMovieRepository;
import com.streamverse.player.LocalPlayerServer;
import com.streamverse.ui.MainView;
import com.streamverse.ui.IntroOverlay;
import com.streamverse.ui.PlayerOverlay;
import com.streamverse.ui.Theme;
import com.streamverse.ui.Toast;
import com.streamverse.ui.controls.ButtonPictures;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Application entry. Composition root: creates the catalogue, the home screen, the player overlay and
 * the intro splash, and stacks them in one StackPane (home at the bottom, intro on top).
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        MovieRepository repository = new SampleMovieRepository();

        PlayerOverlay player = new PlayerOverlay(getHostServices(), repository::nextInGenre);
        ButtonPictures.logAll();                      // prints every button picture and its URL to the console
        MainView home = new MainView(repository, player::open, getHostServices());
        Toast toast = new Toast();
        IntroOverlay intro = new IntroOverlay();

        StackPane root = new StackPane(home, player, toast, intro);
        Scene scene = new Scene(root, 1280, 760, Theme.BG);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        stage.setTitle("StreamVerse \u2013 Movie Trailers");
        stage.setMinWidth(1180);
        stage.setMinHeight(680);
        stage.setScene(scene);
        stage.show();
        Platform.runLater(root::requestFocus);       // don't start with the search box focused
        intro.play();
    }

    @Override
    public void stop() {
        LocalPlayerServer.get().stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
