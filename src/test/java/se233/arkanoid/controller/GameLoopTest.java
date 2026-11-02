package se233.arkanoid.controller;

import org.junit.jupiter.api.Test;
import se233.arkanoid.view.FxTestSupport;
import se233.arkanoid.model.Ball;
import se233.arkanoid.model.Boss;
import se233.arkanoid.model.BossProjectile;
import se233.arkanoid.model.GameModel;
import se233.arkanoid.model.Paddle;
import se233.arkanoid.model.Stage;
import se233.arkanoid.model.Upgrade;
import se233.arkanoid.view.GameDisplay;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class GameLoopTest {
    @Test
    void firstFrameOnlyInitializesTheClock() {
        CountingController controller = new CountingController();
        GameLoop loop = new GameLoop(controller);

        loop.handle(1_000_000_000L);

        assertTrue(controller.elapsed.isEmpty());
    }

    @Test
    void followingFramesSendElapsedSecondsToController() {
        CountingController controller = new CountingController();
        GameLoop loop = new GameLoop(controller);
        loop.handle(1_000_000_000L);
        loop.handle(1_500_000_000L);

        assertEquals(1, controller.elapsed.size());
        assertEquals(0.5, controller.elapsed.get(0), 0.000001);
    }

    @Test
    void startResetsTheFrameClockBeforeTheNextFrame() {
        CountingController controller = new CountingController();
        GameLoop loop = new GameLoop(controller);
        loop.handle(1_000_000_000L);
        FxTestSupport.runAndWait(() -> {
            loop.start();
            loop.stop();
        });
        loop.handle(20L);
        assertTrue(controller.elapsed.isEmpty());
        loop.handle(500_000_020L);
        assertEquals(0.5, controller.elapsed.get(0), 0.000001);
    }

    private static final class CountingController extends GameController {
        private final List<Double> elapsed = new ArrayList<>();

        private CountingController() {
            super(new EmptyDisplay(), new GameModel(), new EmptyLogger(), new Random(1));
        }

        @Override
        public void update(double elapsedSeconds) {
            elapsed.add(elapsedSeconds);
        }
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

    private static final class EmptyLogger implements GameEventLogger {
        @Override public void info(String message) { }
        @Override public void action(String message) { }
        @Override public void score(int score) { }
        @Override public void warning(String message) { }
        @Override public void error(String message, Throwable cause) { }
    }
}
