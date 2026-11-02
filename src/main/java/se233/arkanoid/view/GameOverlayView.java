package se233.arkanoid.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import se233.arkanoid.model.Upgrade;

import java.util.List;
import java.util.function.Consumer;

final class GameOverlayView {
    private final StackPane playArea;
    private final Label message = new Label();
    private StackPane activeOverlay;

    GameOverlayView(StackPane playArea) {
        this.playArea = playArea;
    }

    public void showMenu(Runnable startAction) {
        showOverlay(
                "ARKANOID ROGUELIKE",
                "Break every brick. Choose upgrades between stages.",
                List.of(button("Start Game", startAction))
        );
    }
    public void showUpgradeChoices(
            List<Upgrade> choices,
            Consumer<Upgrade> onSelect
    ) {
        List<Button> buttons = choices.stream()
                .map(upgrade -> {
                    Button button = new Button(
                            upgrade.getName()
                                    + "\n"
                                    + upgrade.getDescription()
                    );
                    button.setMaxWidth(Double.MAX_VALUE);
                    button.setOnAction(
                            event -> onSelect.accept(upgrade)
                    );
                    return button;
                })
                .toList();

        showOverlay(
                "STAGE CLEARED",
                "Choose one upgrade to continue.",
                buttons
        );
    }
    public void showEnd(
            String title,
            String detail,
            Runnable restartAction
    ) {
        showOverlay(
                title,
                detail,
                List.of(button("Play Again", restartAction))
        );
    }
    public void showError(
            String detail,
            Runnable restartAction
    ) {
        showOverlay(
                "GAME ERROR",
                detail,
                List.of(button("Restart", restartAction))
        );
    }
private Button button(String text, Runnable action) {
        Button button = new Button(text);
        button.setOnAction(event -> action.run());
        return button;
    }
    public void hideOverlay() {
        if (activeOverlay != null) {
            playArea.getChildren().remove(activeOverlay);
            activeOverlay = null;
        }
    }
private void showOverlay(
            String title,
            String detail,
            List<Button> buttons
    ) {
        hideOverlay();

        VBox popup = new VBox(14);
        popup.setAlignment(Pos.CENTER);
        popup.setPadding(new Insets(28));
        popup.setPrefSize(400, buttons.size() >= 3 ? 330 : 230);
        popup.setMinSize(360, 210);
        popup.setMaxSize(400, buttons.size() >= 3 ? 330 : 230);
        popup.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #17243a, #0e1729);"
                        + "-fx-background-radius: 18;"
                        + "-fx-border-color: #38bdf8;"
                        + "-fx-border-width: 1.5;"
                        + "-fx-border-radius: 18;"
                        + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.55), 22, 0.2, 0, 8);"
        );

        Label heading = new Label(title);
        heading.setTextFill(Color.WHITE);
        heading.setStyle(
                "-fx-font-size: 25px;"
                        + "-fx-font-weight: bold;"
        );

        message.setText(detail);
        message.setTextFill(Color.LIGHTGRAY);
        message.setWrapText(true);
        message.setAlignment(Pos.CENTER);
        message.setStyle("-fx-font-size: 14px;");

        for (Button button : buttons) {
            button.setStyle(
                    "-fx-background-color: #38bdf8;"
                            + "-fx-text-fill: #071426;"
                            + "-fx-font-size: 14px;"
                            + "-fx-font-weight: bold;"
                            + "-fx-background-radius: 9;"
                            + "-fx-padding: 9 22 9 22;"
            );
        }

        popup.getChildren().addAll(heading, message);
        popup.getChildren().addAll(buttons);

        Region scrim = new Region();
        scrim.setStyle("-fx-background-color: rgba(3, 8, 21, 0.58);");

        activeOverlay = new StackPane(scrim, popup);
        activeOverlay.setPrefSize(GameView.WIDTH, GameView.HEIGHT);
        activeOverlay.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        StackPane.setAlignment(popup, Pos.CENTER);

        playArea.getChildren().add(activeOverlay);
    }
}
