package com.streamverse.ui.controls;

import com.streamverse.audio.SoundManager;
import com.streamverse.audio.SoundManager.Sfx;
import com.streamverse.ui.Theme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.FontWeight;

/** A small pill-shaped choice (genre filter, sort order). Custom control built from a label - not a Button. */
public class Chip extends StackPane {
    private final Label label;
    private boolean selected, hover;

    public Chip(String text, Runnable onClick) {
        label = new Label(text);
        label.setFont(Theme.font(FontWeight.SEMI_BOLD, 13));
        label.setPadding(new Insets(7, 16, 7, 16));
        getChildren().add(label);
        setAlignment(Pos.CENTER);
        setCursor(Cursor.HAND);
        setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
        setOnMouseEntered(e -> { hover = true; style(); SoundManager.get().play(Sfx.HOVER); });
        setOnMouseExited(e -> { hover = false; style(); });
        setOnMouseClicked(e -> { SoundManager.get().play(Sfx.NAV); onClick.run(); });
        style();
    }

    public void setSelected(boolean value) { selected = value; style(); }

    public String getText() { return label.getText(); }

    private void style() {
        String bg = selected ? "#E50914" : hover ? "rgba(255,255,255,0.28)" : "rgba(255,255,255,0.12)";
        setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 30; -fx-border-radius: 30; -fx-border-color: "
                + (selected ? "#E50914" : "rgba(255,255,255,0.25)") + ";");
        label.setTextFill(javafx.scene.paint.Color.WHITE);
    }
}
