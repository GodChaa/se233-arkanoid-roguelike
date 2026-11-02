package se233.arkanoid.view;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.Test;
import se233.arkanoid.model.Ball;
import se233.arkanoid.model.Boss;
import se233.arkanoid.model.BossProjectile;
import se233.arkanoid.model.Brick;
import se233.arkanoid.model.Paddle;
import se233.arkanoid.model.Stage;
import se233.arkanoid.model.Upgrade;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class GameViewTest {
    @Test
    void constructorCreatesRootAndGetRootReturnsIt() {
        FxTestSupport.runAndWait(() -> {
            GameView view = new GameView();
            assertNotNull(view.getRoot());
            assertSame(view.getRoot(), view.getRoot());
        });
    }

    @Test
    void renderHandlesEmptyAndPopulatedStages() {
        FxTestSupport.runAndWait(() -> {
            GameView view = new GameView();
            assertDoesNotThrow(() -> view.render(null, null, null, null, 0, 3, 1));
            Stage stage = new Stage(List.of(new Brick(10, 10, 50, 20, 1, 10)));
            assertDoesNotThrow(() -> view.render(new Ball(100, 200, 5, -5),
                    new Paddle(200, 100, 420, GameView.WIDTH), stage, null, 25, 2, 2));
        });
    }

    @Test
    void renderDrawsBossAndUpdatesHudForBossStage() {
        FxTestSupport.runAndWait(() -> {
            GameView view = new GameView();
            Boss boss = new Boss(GameView.WIDTH);
            assertDoesNotThrow(() -> view.render(new Ball(320, 600, 20, -100),
                    new Paddle(270, 100, 420, GameView.WIDTH), new Stage(List.of()),
                    boss, 1250, 1, 5));
        });
    }

    @Test
    void renderBossProjectilesHandlesNoShotsAndOneShot() {
        FxTestSupport.runAndWait(() -> {
            GameView view = new GameView();
            view.render(null, null, null, null, 0, 3, 1);
            assertDoesNotThrow(() -> view.renderBossProjectiles(List.of()));
            view.renderBossProjectiles(List.of(new BossProjectile(200, 100, 200, 640)));
            assertDoesNotThrow(() -> view.render(null, null, null, null, 0, 3, 5));
        });
    }

    @Test
    void renderBossProjectilesHandlesMultipleShots() {
        FxTestSupport.runAndWait(() -> {
            GameView view = new GameView();
            List<BossProjectile> shots = List.of(
                    new BossProjectile(100, 100, 100, 640),
                    new BossProjectile(300, 100, 350, 640));
            view.renderBossProjectiles(shots);
            assertDoesNotThrow(() -> view.render(null, null, null, new Boss(GameView.WIDTH),
                    0, 3, 5));
        });
    }

    @Test
    void renderExtraBallsHandlesEmptyAndPopulatedLists() {
        FxTestSupport.runAndWait(() -> {
            GameView view = new GameView();
            view.render(null, null, null, null, 0, 3, 1);
            assertDoesNotThrow(() -> view.renderExtraBalls(List.of()));
            assertDoesNotThrow(() -> view.renderExtraBalls(List.of(new Ball(100, 100, 1, 1))));
        });
    }

    @Test
    void setPiercingBallActiveRendersBothModes() {
        FxTestSupport.runAndWait(() -> {
            GameView view = new GameView();
            view.setPiercingBallActive(true);
            view.render(new Ball(100, 100, 1, 1), null, null, null, 0, 3, 1);
            view.setPiercingBallActive(false);
            assertDoesNotThrow(() -> view.render(new Ball(100, 100, 1, 1), null,
                    null, null, 0, 3, 1));
        });
    }

    @Test
    void paddleSpriteResetAndLongPaddleAdvanceRenderRepeatedly() {
        FxTestSupport.runAndWait(() -> {
            GameView view = new GameView();
            Paddle paddle = new Paddle(200, 100, 420, GameView.WIDTH);
            view.resetPaddleSprite();
            view.render(null, paddle, null, null, 0, 3, 1);
            view.advanceLongPaddleSprite();
            assertDoesNotThrow(() -> view.render(null, paddle, null, null, 0, 3, 1));
        });
    }

    @Test
    void speedSpriteAndPaddleResetCanBeCombined() {
        FxTestSupport.runAndWait(() -> {
            GameView view = new GameView();
            Paddle paddle = new Paddle(200, 100, 420, GameView.WIDTH);
            view.activateSpeedPaddleSprite();
            view.render(null, paddle, null, null, 0, 3, 1);
            view.resetPaddleSprite();
            assertDoesNotThrow(() -> view.render(null, paddle, null, null, 0, 3, 1));
        });
    }

    @Test
    void showGameOverAnimationRunsCompletionCallback() throws Exception {
        CountDownLatch completed = new CountDownLatch(1);
        FxTestSupport.runAndWait(() -> new GameView().showGameOverAnimation(completed::countDown));
        assertTrue(completed.await(3, TimeUnit.SECONDS));
    }

    @Test
    void showGameOverAnimationCanReplaceAnInProgressAnimation() throws Exception {
        CountDownLatch completed = new CountDownLatch(1);
        FxTestSupport.runAndWait(() -> {
            GameView view = new GameView();
            view.showGameOverAnimation(() -> { });
            view.showGameOverAnimation(completed::countDown);
        });
        assertTrue(completed.await(3, TimeUnit.SECONDS));
    }

    @Test
    void showMenuAddsStartButtonAndInvokesItsAction() {
        FxTestSupport.runAndWait(() -> {
            GameView view = new GameView();
            AtomicInteger starts = new AtomicInteger();
            view.showMenu(starts::incrementAndGet);
            findButton(view.getRoot(), "Start Game").fire();
            assertEquals(1, starts.get());
        });
    }

    @Test
    void showMenuReplacesThePreviousOverlay() {
        FxTestSupport.runAndWait(() -> {
            GameView view = new GameView();
            view.showMenu(() -> { });
            int afterFirstMenu = playArea(view).getChildren().size();
            view.showMenu(() -> { });
            assertEquals(afterFirstMenu, playArea(view).getChildren().size());
        });
    }

    @Test
    void showUpgradeChoicesInvokesTheSelectedUpgrade() {
        FxTestSupport.runAndWait(() -> {
            GameView view = new GameView();
            AtomicInteger selected = new AtomicInteger();
            Upgrade upgrade = new Upgrade("Extra ball", "Add one ball.");
            view.showUpgradeChoices(List.of(upgrade), chosen -> selected.incrementAndGet());
            findButton(view.getRoot(), "Extra ball").fire();
            assertEquals(1, selected.get());
        });
    }

    @Test
    void showUpgradeChoicesSupportsSeveralChoices() {
        FxTestSupport.runAndWait(() -> {
            GameView view = new GameView();
            AtomicInteger selected = new AtomicInteger();
            view.showUpgradeChoices(List.of(new Upgrade("Long paddle", "Longer."),
                    new Upgrade("Fast ball", "Faster.")), chosen -> selected.incrementAndGet());
            findButton(view.getRoot(), "Fast ball").fire();
            assertEquals(1, selected.get());
        });
    }

    @Test
    void showEndAndShowErrorWireTheirRestartActions() {
        FxTestSupport.runAndWait(() -> {
            GameView view = new GameView();
            AtomicInteger restarts = new AtomicInteger();
            view.showEnd("GAME OVER", "Try again", restarts::incrementAndGet);
            findButton(view.getRoot(), "Play Again").fire();
            view.showError("Bad state", restarts::incrementAndGet);
            findButton(view.getRoot(), "Restart").fire();
            assertEquals(2, restarts.get());
        });
    }

    @Test
    void showEndAndShowErrorReplaceEarlierOverlays() {
        FxTestSupport.runAndWait(() -> {
            GameView view = new GameView();
            view.showEnd("STAGE CLEARED", "Continue playing", () -> { });
            int childCount = playArea(view).getChildren().size();
            view.showError("Error", () -> { });
            assertEquals(childCount, playArea(view).getChildren().size());
        });
    }

    @Test
    void hideOverlayRemovesOverlayAndCanBeCalledMoreThanOnce() {
        FxTestSupport.runAndWait(() -> {
            GameView view = new GameView();
            view.showMenu(() -> { });
            view.hideOverlay();
            assertEquals(1, playArea(view).getChildren().size());
            assertDoesNotThrow(view::hideOverlay);
        });
    }

    private static StackPane playArea(GameView view) {
        return (StackPane) view.getRoot().getCenter();
    }

    private static Button findButton(Node node, String prefix) {
        Button found = findButtonOrNull(node, prefix);
        if (found == null) throw new AssertionError("Button not found: " + prefix);
        return found;
    }

    private static Button findButtonOrNull(Node node, String prefix) {
        if (node instanceof Button button && button.getText().startsWith(prefix)) {
            return button;
        }
        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                Button found = findButtonOrNull(child, prefix);
                if (found != null) return found;
            }
        }
        return null;
    }
}
