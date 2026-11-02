package se233.arkanoid.view;

import org.junit.jupiter.api.Test;
import se233.arkanoid.model.Ball;
import se233.arkanoid.model.Boss;
import se233.arkanoid.model.BossProjectile;
import se233.arkanoid.model.Paddle;
import se233.arkanoid.model.Stage;
import se233.arkanoid.model.Upgrade;

import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class GameDisplayTest {
    @Test
    void defaultOptionalRenderAndSpriteMethodsAreSafeForEmptyAndPopulatedData() {
        GameDisplay display = new EmptyDisplay();
        assertDoesNotThrow(() -> display.renderExtraBalls(List.of()));
        assertDoesNotThrow(() -> display.renderExtraBalls(List.of(new Ball(1, 2, 3, 4))));
        assertDoesNotThrow(() -> display.renderBossProjectiles(List.of()));
        assertDoesNotThrow(() -> display.renderBossProjectiles(
                List.of(new BossProjectile(0, 0, 0, 1))));
        assertDoesNotThrow(() -> display.setPiercingBallActive(true));
        assertDoesNotThrow(display::resetPaddleSprite);
        assertDoesNotThrow(display::advanceLongPaddleSprite);
        assertDoesNotThrow(display::activateSpeedPaddleSprite);
    }

    @Test
    void defaultGameOverAnimationRunsCompletionCallbackOnce() {
        GameDisplay display = new EmptyDisplay();
        int[] calls = {0};
        display.showGameOverAnimation(() -> calls[0]++);
        assertEquals(1, calls[0]);
    }

    @Test
    void defaultGameOverAnimationAlsoSupportsAnotherCallback() {
        GameDisplay display = new EmptyDisplay();
        StringBuilder result = new StringBuilder();
        display.showGameOverAnimation(() -> result.append("finished"));
        assertEquals("finished", result.toString());
    }

    private static final class EmptyDisplay implements GameDisplay {
        @Override public void showMenu(Runnable startAction) { }
        @Override public void render(Ball ball, Paddle paddle, Stage stage, Boss boss,
                                     int score, int lives, int stageNumber) { }
        @Override public void showUpgradeChoices(List<Upgrade> choices, Consumer<Upgrade> onSelect) { }
        @Override public void showEnd(String title, String detail, Runnable restartAction) { }
        @Override public void showError(String detail, Runnable restartAction) { }
        @Override public void hideOverlay() { }
    }
}
