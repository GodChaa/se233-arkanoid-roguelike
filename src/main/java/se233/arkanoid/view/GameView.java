package se233.arkanoid.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import se233.arkanoid.model.Ball;
import se233.arkanoid.model.Boss;
import se233.arkanoid.model.BossProjectile;
import se233.arkanoid.model.Paddle;
import se233.arkanoid.model.Stage;
import se233.arkanoid.model.Upgrade;

import java.util.List;
import java.util.function.Consumer;

public class GameView {
    public static final double WIDTH = 640;
    public static final double HEIGHT = 700;
    public static final double PADDLE_Y = 640;
    public static final double PADDLE_HEIGHT = 16;
    public static final double BALL_RADIUS = 8;

    private final BorderPane root = new BorderPane();
    private final Canvas canvas = new Canvas(WIDTH, HEIGHT);
    private final StackPane playArea = new StackPane(canvas);
    private final GameHud hud = new GameHud();
    private final GameOverlayView overlays = new GameOverlayView(playArea);
    private final GameCanvasRenderer renderer = new GameCanvasRenderer(canvas);

    public GameView() {
        root.setStyle("-fx-background-color: #111827;");
        root.setTop(hud.getRoot());
        root.setCenter(playArea);
    }

    public BorderPane getRoot() { return root; }

    public void render(Ball ball, Paddle paddle, Stage stage, Boss boss,
                       int score, int lives, int stageNumber) {
        renderer.render(ball, paddle, stage, boss, score, lives, stageNumber);
        hud.update(stageNumber, score, lives);
    }

    public void renderBossProjectiles(List<BossProjectile> projectiles) {
        renderer.renderBossProjectiles(projectiles);
    }
    public void renderExtraBalls(List<Ball> balls) { renderer.renderExtraBalls(balls); }
    public void setPiercingBallActive(boolean active) { renderer.setPiercingBallActive(active); }
    public void resetPaddleSprite() { renderer.resetPaddleSprite(); }
    public void advanceLongPaddleSprite() { renderer.advanceLongPaddleSprite(); }
    public void activateSpeedPaddleSprite() { renderer.activateSpeedPaddleSprite(); }
    public void showGameOverAnimation(Runnable onFinished) { renderer.showGameOverAnimation(onFinished); }

    public void showMenu(Runnable startAction) { overlays.showMenu(startAction); }
    public void showUpgradeChoices(List<Upgrade> choices, Consumer<Upgrade> onSelect) {
        overlays.showUpgradeChoices(choices, onSelect);
    }
    public void showEnd(String title, String detail, Runnable restartAction) {
        overlays.showEnd(title, detail, restartAction);
    }
    public void showError(String detail, Runnable restartAction) {
        overlays.showError(detail, restartAction);
    }
    public void hideOverlay() { overlays.hideOverlay(); }
}
