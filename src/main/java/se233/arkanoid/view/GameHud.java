package se233.arkanoid.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

final class GameHud {
    private final Label stageValue = new Label("01");
    private final Label scoreValue = new Label("00000");
    private final Label livesValue = new Label("03");
    private final HBox root;

    GameHud() {
        Label title = new Label("ARKANOID");
        title.setStyle("-fx-font-size: 16px;-fx-font-weight: bold;-fx-text-fill: #f8fafc;");
        Label subtitle = new Label("ROGUELIKE");
        subtitle.setStyle("-fx-font-size: 9px;-fx-font-weight: bold;-fx-text-fill: #38bdf8;-fx-letter-spacing: 2px;");
        VBox brand = new VBox(2, title, subtitle);
        brand.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        root = new HBox(10, brand, spacer,
                createStatCard("STAGE", stageValue, "#38bdf8"),
                createStatCard("SCORE", scoreValue, "#facc15"),
                createStatCard("LIVES", livesValue, "#fb7185"));
        root.setAlignment(Pos.CENTER_LEFT);
        root.setPadding(new Insets(6, 16, 6, 16));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #111c31, #0b1322);-fx-border-color: transparent transparent #26364f transparent;-fx-border-width: 0 0 1 0;");
    }

    HBox getRoot() { return root; }

    void update(int stage, int score, int lives) {
        stageValue.setText(String.format("%02d", stage));
        scoreValue.setText(String.format("%05d", score));
        livesValue.setText(String.format("%02d", lives));
    }

    private VBox createStatCard(String caption, Label value, String accentColor) {
        Label captionLabel = new Label(caption);
        captionLabel.setStyle("-fx-font-size: 9px;-fx-font-weight: bold;-fx-text-fill: #94a3b8;-fx-letter-spacing: 1px;");
        value.setStyle("-fx-font-size: 18px;-fx-font-weight: bold;-fx-text-fill: " + accentColor + ";");
        VBox card = new VBox(1, captionLabel, value);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setMinWidth(76);
        card.setPadding(new Insets(4, 10, 4, 10));
        card.setStyle("-fx-background-color: #17243a;-fx-background-radius: 8;-fx-border-color: #2b3b55;-fx-border-radius: 8;");
        return card;
    }
}
