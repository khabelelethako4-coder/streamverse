package com.streamverse.ui;

import com.streamverse.audio.SoundManager;
import com.streamverse.audio.SoundManager.Sfx;
import com.streamverse.model.Category;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.List;
import java.util.function.Consumer;

/**
 * Drop-down "Genres" panel (2 columns). Built from plain panes/labels - no Button, no MenuButton.
 * HomeView positions it under the nav link and closes it when the user clicks elsewhere.
 */
public class GenreMenu extends StackPane {
    private final FadeTransition fade = new FadeTransition(Duration.millis(160), this);

    public GenreMenu(List<Category> categories, Consumer<String> onPick) {
        GridPane grid = new GridPane();
        grid.setHgap(6);
        grid.setVgap(4);
        grid.setPadding(new Insets(14));
        grid.setStyle("-fx-background-color: rgba(18,18,18,0.98); -fx-background-radius: 10; "
                + "-fx-border-color: rgba(255,255,255,0.14); -fx-border-radius: 10;");
        grid.setEffect(new DropShadow(26, Color.rgb(0, 0, 0, 0.85)));

        int rows = (int) Math.ceil(categories.size() / 2.0);
        for (int i = 0; i < categories.size(); i++) {
            Category c = categories.get(i);
            grid.add(item(c, onPick), i / rows, i % rows);   // fill column by column
        }
        getChildren().add(grid);
        setVisible(false);
        setOpacity(0);
        setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
    }

    private HBox item(Category c, Consumer<String> onPick) {
        Label name = new Label(c.name());
        name.setFont(Theme.font(FontWeight.SEMI_BOLD, 14));
        name.setTextFill(Theme.TEXT_DIM);
        Label count = new Label(String.valueOf(c.movies().size()));
        count.setFont(Theme.font(FontWeight.NORMAL, 12));
        count.setTextFill(Theme.MUTED);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox row = new HBox(10, name, spacer, count);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(9, 14, 9, 14));
        row.setMinWidth(250);
        row.setPrefWidth(250);
        row.setCursor(Cursor.HAND);
        row.setStyle("-fx-background-color: transparent; -fx-background-radius: 6;");
        row.setOnMouseEntered(e -> {
            row.setStyle("-fx-background-color: rgba(229,9,20,0.85); -fx-background-radius: 6;");
            name.setTextFill(Color.WHITE);
            count.setTextFill(Color.WHITE);
            SoundManager.get().play(Sfx.HOVER);
        });
        row.setOnMouseExited(e -> {
            row.setStyle("-fx-background-color: transparent; -fx-background-radius: 6;");
            name.setTextFill(Theme.TEXT_DIM);
            count.setTextFill(Theme.MUTED);
        });
        row.setOnMouseClicked(e -> { SoundManager.get().play(Sfx.NAV); onPick.accept(c.name()); });
        return row;
    }

    public void open() {
        if (isVisible() && getOpacity() > 0.5) return;
        setVisible(true);
        fade.stop();
        fade.setToValue(1);
        fade.setOnFinished(null);
        fade.play();
    }

    public void close() {
        if (!isVisible()) return;
        fade.stop();
        fade.setToValue(0);
        fade.setOnFinished(e -> setVisible(false));
        fade.play();
    }

    public void toggle() {
        if (isVisible() && fade.getToValue() != 0) close(); else open();
    }
}
