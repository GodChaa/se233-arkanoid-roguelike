package se233.arkanoid.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import se233.arkanoid.model.Ball;
import se233.arkanoid.model.Brick;
import se233.arkanoid.model.Paddle;
import se233.arkanoid.model.Stage;

import java.util.List;
import java.util.function.Consumer;

public class GameView implements GameDisplay {
    public static final double WIDTH = 640;
    public static final double HEIGHT = 700;
    public static final double PADDLE_Y = 640;
    public static final double PADDLE_HEIGHT = 16;
    public static final double BALL_RADIUS = 8;

    private final BorderPane root = new BorderPane();
    private final Canvas canvas = new Canvas(WIDTH, HEIGHT);
    private final StackPane playArea = new StackPane(canvas);
    private final Label status = new Label();
    private final Label message = new Label();

    public GameView() {
        root.setStyle("-fx-background-color: #111827;");
        status.setTextFill(Color.WHITE);
        status.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
        root.setTop(status);
        root.setCenter(playArea);
        drawBackground();
    }

    public BorderPane getRoot() { return root; }

    @Override
    public void render(Ball ball, Paddle paddle, Stage stage, int score, int lives, int stageNumber) {
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        graphics.setFill(Color.rgb(17, 24, 39));
        graphics.fillRect(0, 0, WIDTH, HEIGHT);
        if (stage != null) {
            for (Brick brick : stage.getBricks()) {
                if (brick.isDestroyed()) continue;
                graphics.setFill(brick.getHitPoints() > 1 ? Color.CORAL : Color.SKYBLUE);
                graphics.fillRoundRect(brick.getX(), brick.getY(), brick.getWidth() - 4,
                        brick.getHeight() - 4, 8, 8);
            }
        }
        if (paddle != null) {
            graphics.setFill(Color.WHITESMOKE);
            graphics.fillRoundRect(paddle.getX(), PADDLE_Y, paddle.getWidth(), PADDLE_HEIGHT, 8, 8);
        }
        if (ball != null) {
            graphics.setFill(Color.GOLD);
            graphics.fillOval(ball.getX() - BALL_RADIUS, ball.getY() - BALL_RADIUS,
                    BALL_RADIUS * 2, BALL_RADIUS * 2);
        }
        status.setText("Stage " + stageNumber + "     Score " + score + "     Lives " + lives);
    }

    @Override
    public void showMenu(Runnable startAction) {
        showOverlay("ARKANOID ROGUELIKE", "Break every brick. Choose upgrades between stages.",
                List.of(button("Start Game", startAction)));
    }

    @Override
    public void showUpgradeChoices(List<se233.arkanoid.model.Upgrade> choices,
                                   Consumer<se233.arkanoid.model.Upgrade> onSelect) {
        List<Button> buttons = choices.stream().map(upgrade -> {
            Button button = new Button(upgrade.getName() + "\n" + upgrade.getDescription());
            button.setMaxWidth(Double.MAX_VALUE);
            button.setOnAction(event -> onSelect.accept(upgrade));
            return button;
        }).toList();
        showOverlay("STAGE CLEARED", "Choose one upgrade to continue.", buttons);
    }

    @Override
    public void showEnd(String title, String detail, Runnable restartAction) {
        showOverlay(title, detail, List.of(button("Play Again", restartAction)));
    }

    @Override
    public void showError(String detail, Runnable restartAction) {
        showOverlay("GAME ERROR", detail, List.of(button("Restart", restartAction)));
    }

    @Override
    public void hideOverlay() {
        playArea.getChildren().removeIf(node -> node instanceof VBox);
    }

    private Button button(String text, Runnable action) {
        Button button = new Button(text);
        button.setOnAction(event -> action.run());
        return button;
    }

    private void showOverlay(String title, String detail, List<Button> buttons) {
        VBox overlay = new VBox(14);
        overlay.setAlignment(Pos.CENTER);
        overlay.setPadding(new Insets(24));
        overlay.setMaxWidth(380);
        overlay.setStyle("-fx-background-color: rgba(15, 23, 42, 0.96); -fx-background-radius: 12;");
        Label heading = new Label(title);
        heading.setTextFill(Color.WHITE);
        heading.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");
        message.setText(detail);
        message.setTextFill(Color.LIGHTGRAY);
        message.setWrapText(true);
        overlay.getChildren().addAll(heading, message);
        overlay.getChildren().addAll(buttons);
        playArea.getChildren().removeIf(node -> node instanceof VBox);
        playArea.getChildren().add(overlay);
    }

    private void drawBackground() {
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        graphics.setFill(Color.rgb(17, 24, 39));
        graphics.fillRect(0, 0, WIDTH, HEIGHT);
    }
}
