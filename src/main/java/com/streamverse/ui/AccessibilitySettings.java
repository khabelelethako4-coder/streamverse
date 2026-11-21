package com.streamverse.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.AccessibleRole;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

import java.util.prefs.Preferences;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

/** In-app accessibility controls for text sizing and background lighting. */
final class AccessibilitySettings extends StackPane {
    private static final double MIN_FONT_SCALE = 0.8;
    private static final double MAX_FONT_SCALE = 1.8;
    private static final Preferences PREFERENCES = Preferences.userNodeForPackage(AccessibilitySettings.class);

    private final Slider fontScale = new Slider(MIN_FONT_SCALE, MAX_FONT_SCALE, bounded(
            PREFERENCES.getDouble("fontScale", 1.0), MIN_FONT_SCALE, MAX_FONT_SCALE));
    private final Slider brightness = new Slider(-0.5, 0.3, bounded(
            PREFERENCES.getDouble("backdropBrightness", 0.0), -0.5, 0.3));
    private final Slider contrast = new Slider(-0.5, 0.5, bounded(
            PREFERENCES.getDouble("backdropContrast", 0.0), -0.5, 0.5));
    private final Label fontValue = new Label();
    private final Label brightnessValue = new Label();
    private final Label contrastValue = new Label();

    AccessibilitySettings(Consumer<Double> onFontScale, DoubleConsumer onBrightness, DoubleConsumer onContrast) {
        setAlignment(Pos.CENTER);
        setStyle("-fx-background-color: rgba(0,0,0,0.68);");
        setAccessibleText("Accessibility settings");
        setFocusTraversable(true);
        setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                setVisible(false);
                setManaged(false);
            }
        });

        Label title = new Label("Accessibility settings");
        title.setFont(Theme.font(FontWeight.EXTRA_BOLD, 24));
        title.setTextFill(Color.WHITE);

        Label fontLabel = new Label("Text size");
        fontLabel.setTextFill(Color.WHITE);
        fontScale.setBlockIncrement(0.1);
        fontScale.setMajorTickUnit(0.2);
        fontScale.setMinorTickCount(1);
        fontScale.setShowTickMarks(true);
        fontScale.setAccessibleText("Text size");
        fontScale.setAccessibleHelp("Adjusts text size throughout the app.");
        fontScale.valueProperty().addListener((o, a, value) -> {
            double scaled = Math.round(value.doubleValue() * 10) / 10.0;
            if (fontScale.getValue() != scaled) fontScale.setValue(scaled);
            fontValue.setText(Math.round(scaled * 100) + "%");
            PREFERENCES.putDouble("fontScale", scaled);
            onFontScale.accept(scaled);
        });
        fontValue.setText(Math.round(fontScale.getValue() * 100) + "%");
        fontValue.setTextFill(Theme.TEXT_DIM);

        Label brightnessLabel = new Label("Background brightness");
        brightnessLabel.setTextFill(Color.WHITE);
        brightness.setBlockIncrement(0.1);
        brightness.setMajorTickUnit(0.2);
        brightness.setMinorTickCount(1);
        brightness.setShowTickMarks(true);
        brightness.setAccessibleText("Background brightness");
        brightness.setAccessibleHelp("Adjusts the background image lighting without dimming the text.");
        brightness.valueProperty().addListener((o, a, value) -> {
            double adjusted = Math.round(value.doubleValue() * 10) / 10.0;
            if (brightness.getValue() != adjusted) brightness.setValue(adjusted);
            brightnessValue.setText(String.format("%+.1f", adjusted));
            PREFERENCES.putDouble("backdropBrightness", adjusted);
            onBrightness.accept(adjusted);
        });
        brightnessValue.setText(String.format("%+.1f", brightness.getValue()));

        Label contrastLabel = new Label("Background contrast");
        contrastLabel.setTextFill(Color.WHITE);
        contrast.setBlockIncrement(0.1);
        contrast.setMajorTickUnit(0.2);
        contrast.setMinorTickCount(1);
        contrast.setShowTickMarks(true);
        contrast.setAccessibleText("Background contrast");
        contrast.setAccessibleHelp("Adjusts contrast of the background image without changing text contrast.");
        contrast.valueProperty().addListener((o, a, value) -> {
            double adjusted = Math.round(value.doubleValue() * 10) / 10.0;
            if (contrast.getValue() != adjusted) contrast.setValue(adjusted);
            contrastValue.setText(String.format("%+.1f", adjusted));
            PREFERENCES.putDouble("backdropContrast", adjusted);
            onContrast.accept(adjusted);
        });
        contrastValue.setText(String.format("%+.1f", contrast.getValue()));

        Label note = new Label("Voice search sends up to 10 seconds of microphone audio to OpenAI for transcription; API usage charges may apply.");
        note.setWrapText(true);
        note.setTextFill(Theme.TEXT_DIM);
        note.setAccessibleHelp("Voice search requires internet and an OpenAI API key. Audio is sent to OpenAI and charges may apply.");

        com.streamverse.ui.controls.CustomButton close = new com.streamverse.ui.controls.CustomButton(
                "Done", com.streamverse.ui.controls.CustomButton.Style.SECONDARY);
        close.setAccessibleRole(AccessibleRole.BUTTON);
        close.setAccessibleText("Close accessibility settings");
        close.setOnAction(() -> {
            setVisible(false);
            setManaged(false);
        });

        VBox panel = new VBox(12,
                title, fontLabel, fontScale, fontValue,
                brightnessLabel, brightness, brightnessValue,
                contrastLabel, contrast, contrastValue, note, close);
        panel.setAlignment(Pos.CENTER_LEFT);
        panel.setPadding(new Insets(26));
        panel.setMaxWidth(460);
        panel.setStyle("-fx-background-color: #202020; -fx-background-radius: 12; "
                + "-fx-border-color: rgba(255,255,255,0.35); -fx-border-radius: 12;");
        getChildren().add(panel);
        setVisible(false);
        setManaged(false);

        onFontScale.accept(fontScale.getValue());
        onBrightness.accept(brightness.getValue());
        onContrast.accept(contrast.getValue());
    }

    void toggle() {
        boolean open = !isVisible();
        setVisible(open);
        setManaged(open);
        if (open) requestFocus();
    }

    private static double bounded(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
