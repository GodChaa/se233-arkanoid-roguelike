package se233.arkanoid.view;

import se233.arkanoid.model.Ball;
import se233.arkanoid.model.Boss;
import se233.arkanoid.model.Paddle;
import se233.arkanoid.model.Stage;
import se233.arkanoid.model.Upgrade;

import java.util.List;
import java.util.function.Consumer;

public interface GameDisplay {
    void showMenu(Runnable startAction);
    void render(Ball ball, Paddle paddle, Stage stage, Boss boss, int score, int lives, int stageNumber);
    default void renderExtraBalls(List<Ball> balls) { }
    default void setPiercingBallActive(boolean active) { }
    default void resetPaddleSprite() { }
    default void advanceLongPaddleSprite() { }
    default void activateSpeedPaddleSprite() { }
    default void showGameOverAnimation(Runnable onFinished) { onFinished.run(); }
    void showUpgradeChoices(List<Upgrade> choices, Consumer<Upgrade> onSelect);
    void showEnd(String title, String detail, Runnable restartAction);
    void showError(String detail, Runnable restartAction);
    void hideOverlay();
}
