package se233.arkanoid.view;

import se233.arkanoid.model.Ball;
import se233.arkanoid.model.Paddle;
import se233.arkanoid.model.Stage;
import se233.arkanoid.model.Upgrade;

import java.util.List;
import java.util.function.Consumer;

/** Small display boundary so the game controller can be tested without starting JavaFX. */
public interface GameDisplay {
    void showMenu(Runnable startAction);
    void render(Ball ball, Paddle paddle, Stage stage, int score, int lives, int stageNumber);
    void showUpgradeChoices(List<Upgrade> choices, Consumer<Upgrade> onSelect);
    void showEnd(String title, String detail, Runnable restartAction);
    void showError(String detail, Runnable restartAction);
    void hideOverlay();
}
