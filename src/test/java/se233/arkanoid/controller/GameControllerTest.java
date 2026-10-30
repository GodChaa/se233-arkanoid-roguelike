package se233.arkanoid.controller;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import se233.arkanoid.model.Ball;
import se233.arkanoid.model.Brick;
import se233.arkanoid.model.BrickType;
import se233.arkanoid.model.Boss;
import se233.arkanoid.model.GameModel;
import se233.arkanoid.model.GameState;
import se233.arkanoid.model.Paddle;
import se233.arkanoid.model.Stage;
import se233.arkanoid.model.Upgrade;
import se233.arkanoid.model.UpgradeManager;
import se233.arkanoid.view.GameDisplay;
import se233.arkanoid.view.GameView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(OrderAnnotation.class)
class GameControllerTest {
    @Test
    @Order(1)
    void constructorShowsTheStartMenuAndInitialFrame() {
        Harness harness = new Harness(new Random(1));

        assertNotNull(harness.display.startAction);
        assertEquals(GameState.MENU, harness.controller.getGame().getState());
        assertEquals(1, harness.display.renderCount);
    }

    @Test
    @Order(2)
    void startMenuActionStartsANewRun() {
        Harness harness = new Harness(new Random(1));

        harness.display.startAction.run();

        assertEquals(GameState.PLAYING, harness.controller.getGame().getState());
        assertEquals(3, harness.controller.getGame().getLives());
        assertEquals(26, harness.display.stage.getBricks().size());
    }

    @Test
    @Order(3)
    void startNewGameInitializesBallPaddleAndFirstStage() {
        Harness harness = new Harness(new Random(1));

        harness.controller.startNewGame();

        assertNotNull(harness.display.ball);
        assertNotNull(harness.display.paddle);
        assertEquals(320, harness.display.ball.getX(), 0.0001);
        assertEquals(610, harness.display.ball.getY(), 0.0001);
        assertEquals(1, harness.display.stageNumber);
    }

    @Test
    void startBossTestGameStartsAtStageFiveWithFullHealthBoss() {
        Harness harness = new Harness(new Random(1));

        harness.controller.startBossTestGame();

        assertEquals(GameState.PLAYING, harness.controller.getGame().getState());
        assertEquals(5, harness.display.stageNumber);
        assertNotNull(harness.display.boss);
        assertEquals(Boss.DEFAULT_HEALTH, harness.display.boss.getHealth());
        assertNotNull(harness.display.ball);
        assertNotNull(harness.display.paddle);
    }

    @Test
    @Order(4)
    void startNewGameResetsLivesScoreAndTemporaryPower() {
        Harness harness = new Harness(new Random(1));
        harness.controller.startNewGame();
        harness.controller.getGame().addScore(60);
        harness.controller.getGame().activatePiercingBall();
        harness.controller.getGame().loseLife();

        harness.controller.startNewGame();

        assertEquals(3, harness.controller.getGame().getLives());
        assertEquals(0, harness.controller.getGame().getScore());
        assertFalse(harness.controller.getGame().isPiercingBallActive());
    }

    @Test
    @Order(5)
    void setLeftPressedMovesThePaddleLeft() {
        Harness harness = new Harness(new Random(1));
        harness.controller.startNewGame();
        double startX = harness.display.paddle.getX();

        harness.controller.setLeftPressed(true);
        harness.controller.update(0.05);

        assertEquals(startX - 21, harness.display.paddle.getX(), 0.0001);
        assertTrue(harness.logger.actions.stream().anyMatch(message -> message.contains("left key pressed")));
    }

    @Test
    @Order(6)
    void releasingLeftKeyStopsLeftMovement() {
        Harness harness = new Harness(new Random(1));
        harness.controller.startNewGame();
        harness.controller.setLeftPressed(true);
        harness.controller.update(0.05);
        harness.controller.setLeftPressed(false);
        double stoppedX = harness.display.paddle.getX();

        harness.controller.update(0.05);

        assertEquals(stoppedX, harness.display.paddle.getX(), 0.0001);
    }

    @Test
    @Order(7)
    void setRightPressedMovesThePaddleRight() {
        Harness harness = new Harness(new Random(1));
        harness.controller.startNewGame();
        double startX = harness.display.paddle.getX();

        harness.controller.setRightPressed(true);
        harness.controller.update(0.05);

        assertEquals(startX + 21, harness.display.paddle.getX(), 0.0001);
        assertTrue(harness.logger.actions.stream().anyMatch(message -> message.contains("right key pressed")));
    }

    @Test
    @Order(8)
    void releasingRightKeyStopsRightMovement() {
        Harness harness = new Harness(new Random(1));
        harness.controller.startNewGame();
        harness.controller.setRightPressed(true);
        harness.controller.update(0.05);
        harness.controller.setRightPressed(false);
        double stoppedX = harness.display.paddle.getX();

        harness.controller.update(0.05);

        assertEquals(stoppedX, harness.display.paddle.getX(), 0.0001);
    }

    @Test
    @Order(9)
    void updateDoesNothingBeforeTheRunStarts() {
        Harness harness = new Harness(new Random(1));
        int initialRenders = harness.display.renderCount;

        harness.controller.update(0.05);

        assertEquals(GameState.MENU, harness.controller.getGame().getState());
        assertEquals(initialRenders, harness.display.renderCount);
    }

    @Test
    @Order(10)
    void updateMovesBallAndRendersTheChangedFrame() {
        Harness harness = new Harness(new Random(1));
        harness.controller.startNewGame();
        double startY = harness.display.ball.getY();
        int initialRenders = harness.display.renderCount;

        harness.controller.update(0.02);

        assertEquals(startY - 5, harness.display.ball.getY(), 0.0001);
        assertEquals(initialRenders + 1, harness.display.renderCount);
    }

    @Test
    @Order(11)
    void updateCatchesInvalidSimulationInputAndShowsAnError() {
        Harness harness = new Harness(new Random(1));
        harness.controller.startNewGame();

        harness.controller.update(Double.NaN);

        assertNotNull(harness.display.errorDetail);
        assertEquals(1, harness.logger.errors.size());
    }

    @Test
    @Order(12)
    void updateLosesALifeWhenTheBallPassesTheBottomBoundary() {
        Harness harness = new Harness(new Random(1));
        harness.controller.startNewGame();
        harness.display.ball.setPosition(320, 720);

        harness.controller.update(0.01);

        assertEquals(2, harness.controller.getGame().getLives());
        assertTrue(harness.logger.warnings.stream().anyMatch(message -> message.contains("lost a life")));
    }

    @Test
    @Order(13)
    void updateEntersGameOverAfterTheLastMissedBall() {
        Harness harness = new Harness(new Random(1));
        harness.controller.startNewGame();

        for (int life = 0; life < 3; life++) {
            harness.display.ball.setPosition(320, 720);
            harness.controller.update(0.01);
        }

        assertEquals(GameState.GAME_OVER, harness.controller.getGame().getState());
        assertEquals("GAME OVER", harness.display.endTitle);
    }

    @Test
    @Order(14)
    void getGameReturnsTheControllerModel() {
        Harness harness = new Harness(new Random(1));

        assertSame(harness.controller.getGame(), harness.controller.getGame());
        assertEquals(GameState.MENU, harness.controller.getGame().getState());
    }

    @Test
    @Order(15)
    void getGameExposesTheCurrentScore() {
        Harness harness = new Harness(new Random(1));
        harness.controller.startNewGame();
        harness.controller.getGame().addScore(45);

        assertEquals(45, harness.controller.getGame().getScore());
    }

    @Test
    @Order(16)
    void handleBrickCollisionDestroysBrickAwardsScoreAndBouncesNormally() {
        Harness harness = new Harness(new Random(1));
        harness.controller.startNewGame();
        Brick brick = harness.display.stage.getBricks().get(0);
        harness.display.ball.setPosition(brick.getX() + 35, brick.getY() + brick.getHeight() + 12);

        harness.controller.update(0.05);

        assertTrue(brick.isDestroyed());
        assertEquals(10, harness.controller.getGame().getScore());
        assertTrue(harness.logger.scores.contains(10));
        assertTrue(harness.display.ball.getVelocityY() > 0);
    }

    @Test
    @Order(17)
    void handleBrickCollisionDoesNotChangeScoreWhenThereIsNoCollision() {
        Harness harness = new Harness(new Random(1));
        harness.controller.startNewGame();
        harness.display.ball.setPosition(500, 300);

        harness.controller.update(0.05);

        assertEquals(0, harness.controller.getGame().getScore());
        assertFalse(harness.display.stage.getBricks().get(0).isDestroyed());
    }

    @Test
    @Order(18)
    void piercingBallBreaksBricksWithoutBouncing() {
        Harness harness = new Harness(new Random(1));
        harness.controller.startNewGame();
        harness.controller.getGame().activatePiercingBall();
        Brick brick = harness.display.stage.getBricks().get(0);
        harness.display.ball.setPosition(brick.getX() + 35, brick.getY() + brick.getHeight() + 12);

        harness.controller.update(0.05);

        assertTrue(brick.isDestroyed());
        assertTrue(harness.display.ball.getVelocityY() < 0);
        assertTrue(harness.controller.getGame().isPiercingBallActive());
    }

    @Test
    @Order(19)
    void piercingBallPowerEndsAndBouncesAtTheSideWall() {
        Harness harness = new Harness(new Random(1));
        harness.controller.startNewGame();
        harness.controller.getGame().activatePiercingBall();
        harness.display.ball.setPosition(GameView.WIDTH - 1, 300);

        harness.controller.update(0.05);

        assertFalse(harness.controller.getGame().isPiercingBallActive());
        assertTrue(harness.display.ball.getVelocityX() < 0);
    }

    @Test
    @Order(20)
    void piercingBallPowerEndsAtTheTopWallToo() {
        Harness harness = new Harness(new Random(1));
        harness.controller.startNewGame();
        harness.controller.getGame().activatePiercingBall();
        harness.display.ball.setPosition(320, 5);

        harness.controller.update(0.05);

        assertFalse(harness.controller.getGame().isPiercingBallActive());
        assertTrue(harness.display.ball.getVelocityY() > 0);
    }

    @Test
    @Order(21)
    void completeCurrentStageShowsThreeUpgradeChoicesAndPauses() {
        Harness harness = new Harness(new Random(1));
        harness.controller.startNewGame();
        clearCurrentStage(harness);

        harness.controller.update(0);

        assertEquals(GameState.UPGRADE, harness.controller.getGame().getState());
        assertEquals(3, harness.display.upgradeChoices.size());
        assertNotNull(harness.display.upgradeAction);
    }

    @Test
    @Order(22)
    void completeCurrentStageWinsAfterTheFinalStage() {
        Harness harness = new Harness(new Random(1));
        harness.controller.startNewGame();
        for (int stage = 0; stage < 4; stage++) {
            clearCurrentStage(harness);
            harness.controller.update(0);
            harness.display.selectUpgrade(0);
        }

        assertEquals(5, harness.display.stageNumber);
        assertNotNull(harness.display.boss);
        for (int hit = 0; hit < Boss.DEFAULT_HEALTH; hit++) {
            hitBoss(harness);
            if (hit < Boss.DEFAULT_HEALTH - 1) {
                for (int frame = 0; frame < 7; frame++) harness.controller.update(0.05);
            }
        }

        assertEquals(GameState.VICTORY, harness.controller.getGame().getState());
        assertEquals("YOU WIN", harness.display.endTitle);
    }

    @Test
    @Order(23)
    void selectUpgradeAppliesPaddleUpgradeAndAdvancesStage() {
        Upgrade widePaddle = upgrade("Long paddle", game -> game.increasePaddleWidth(25));
        Harness harness = new Harness(new UpgradeManager(List.of(widePaddle,
                upgrade("Choice B", game -> {}), upgrade("Choice C", game -> {})), new Random(1)));
        harness.controller.startNewGame();
        clearCurrentStage(harness);
        harness.controller.update(0);

        harness.display.choose(widePaddle);

        assertEquals(GameState.PLAYING, harness.controller.getGame().getState());
        assertEquals(2, harness.display.stageNumber);
        assertEquals(125, harness.display.paddle.getWidth(), 0.0001);
        assertTrue(harness.logger.infos.stream().anyMatch(message -> message.contains("Long paddle")));
    }

    @Test
    @Order(24)
    void selectUpgradeCanActivatePiercingPowerForTheNextStage() {
        Upgrade piercing = upgrade("Piercing ball", GameModel::activatePiercingBall);
        Harness harness = new Harness(new UpgradeManager(List.of(piercing,
                upgrade("Choice B", game -> {}), upgrade("Choice C", game -> {})), new Random(1)));
        harness.controller.startNewGame();
        clearCurrentStage(harness);
        harness.controller.update(0);

        harness.display.choose(piercing);

        assertTrue(harness.controller.getGame().isPiercingBallActive());
        assertEquals(2, harness.display.stageNumber);
        assertTrue(harness.display.ball.getVelocityY() < 0);
    }

    @Test
    @Order(25)
    void selectUpgradeRoutesFailuresThroughTheCentralExceptionHandler() {
        Upgrade brokenUpgrade = upgrade("Broken", game -> {
            throw new IllegalStateException("upgrade failed");
        });
        Harness harness = new Harness(new UpgradeManager(List.of(brokenUpgrade,
                upgrade("Choice B", game -> {}), upgrade("Choice C", game -> {})), new Random(1)));
        harness.controller.startNewGame();
        clearCurrentStage(harness);
        harness.controller.update(0);

        harness.display.choose(brokenUpgrade);

        assertNotNull(harness.display.errorDetail);
        assertEquals(1, harness.logger.errors.size());
    }

    @Test
    @Order(26)
    void createBallUsesTheExpectedLaunchPositionAndDirection() {
        Harness harness = new Harness(new Random(1));

        harness.controller.startNewGame();

        assertEquals(320, harness.display.ball.getX(), 0.0001);
        assertEquals(610, harness.display.ball.getY(), 0.0001);
        assertTrue(harness.display.ball.getVelocityY() < 0);
    }

    @Test
    @Order(27)
    void createBallRespawnsAfterThePlayerLosesALife() {
        Harness harness = new Harness(new Random(1));
        harness.controller.startNewGame();
        Ball missedBall = harness.display.ball;
        missedBall.setPosition(320, 720);

        harness.controller.update(0.01);

        assertNotNull(harness.display.ball);
        assertTrue(harness.display.ball != missedBall);
        assertEquals(610, harness.display.ball.getY(), 0.0001);
    }

    @Test
    @Order(28)
    void renderUpdatesTheScoreAndStageNumber() {
        Harness harness = new Harness(new Random(1));
        harness.controller.startNewGame();
        harness.controller.getGame().addScore(30);

        harness.controller.update(0);

        assertEquals(30, harness.display.score);
        assertEquals(1, harness.display.stageNumber);
    }

    @Test
    @Order(29)
    void renderUpdatesTheFrameWhenTheNextStageStarts() {
        Harness harness = new Harness(new Random(1));
        harness.controller.startNewGame();
        clearCurrentStage(harness);
        harness.controller.update(0);
        int expectedLives = harness.controller.getGame().getLives()
                + (harness.display.upgradeChoices.get(0).getName().equals("Extra life") ? 1 : 0);
        harness.display.selectUpgrade(0);

        assertEquals(2, harness.display.stageNumber);
        assertEquals(40, harness.display.stage.getBricks().size());
        assertEquals(expectedLives, harness.display.lives);
    }

    @Test
    @Order(30)
    void createUpgradesOffersThreeDistinctChoicesFromThePool() {
        Harness harness = new Harness(new Random(2));
        harness.controller.startNewGame();
        clearCurrentStage(harness);

        harness.controller.update(0);

        assertEquals(3, harness.display.upgradeChoices.size());
        assertEquals(3, harness.display.upgradeChoices.stream().map(Upgrade::getName).distinct().count());
    }

    @Test
    @Order(31)
    void createUpgradesIncludesTheSpecialAttackInItsRandomOptions() {
        List<String> offered = new ArrayList<>();
        for (int seed = 0; seed < 20; seed++) {
            Harness harness = new Harness(new Random(seed));
            harness.controller.startNewGame();
            clearCurrentStage(harness);
            harness.controller.update(0);
            harness.display.upgradeChoices.stream().map(Upgrade::getName).forEach(offered::add);
        }

        assertTrue(offered.contains("Piercing ball"));
    }

    @Test
    @Order(32)
    void createStagesBuildsFourProgressivelyHarderStagesBeforeTheBoss() {
        Harness harness = new Harness(new Random(1));
        harness.controller.startNewGame();
        assertEquals(26, harness.display.stage.getBricks().size());
        assertTrue(harness.display.stage.getBricks().stream().anyMatch(brick -> brick.getType() == BrickType.NORMAL));
        assertTrue(harness.display.stage.getBricks().stream().anyMatch(brick -> brick.getType() == BrickType.BONUS));

        for (int stage = 0; stage < 3; stage++) {
            clearCurrentStage(harness);
            harness.controller.update(0);
            harness.display.selectUpgrade(0);
        }

        assertEquals(4, harness.display.stageNumber);
        assertEquals(42, harness.display.stage.getBricks().size());
        assertTrue(harness.display.stage.getBricks().stream().anyMatch(brick -> brick.getType() == BrickType.STRONG));

        clearCurrentStage(harness);
        harness.controller.update(0);
        harness.display.selectUpgrade(0);

        assertEquals(5, harness.display.stageNumber);
        assertNotNull(harness.display.boss);
        assertEquals(Boss.DEFAULT_HEALTH, harness.display.boss.getHealth());
    }

    @Test
    @Order(33)
    void createStagesPlacesTheSecondStageAfterTheFirst() {
        Harness harness = new Harness(new Random(1));
        harness.controller.startNewGame();
        clearCurrentStage(harness);
        harness.controller.update(0);
        harness.display.selectUpgrade(0);

        assertEquals(2, harness.display.stageNumber);
        assertEquals(40, harness.display.stage.getBricks().size());
        assertEquals(2, harness.display.stage.getBricks().get(0).getHitPoints());
        assertTrue(harness.display.stage.getBricks().stream().anyMatch(brick -> brick.getType() == BrickType.STRONG));
        assertTrue(harness.display.stage.getBricks().stream().anyMatch(brick -> brick.getType() == BrickType.SPECIAL));
    }

    @Test
    @Order(34)
    void createStagePlacesBricksInsideThePlayfield() {
        Harness harness = new Harness(new Random(1));
        harness.controller.startNewGame();
        Brick first = harness.display.stage.getBricks().get(0);
        Brick last = harness.display.stage.getBricks().get(harness.display.stage.getBricks().size() - 1);

        assertTrue(first.getX() >= 0);
        assertTrue(last.getX() + last.getWidth() <= GameView.WIDTH);
        assertEquals(65, first.getY(), 0.0001);
    }

    @Test
    @Order(35)
    void createStageSpacesRowsEvenly() {
        Harness harness = new Harness(new Random(1));
        harness.controller.startNewGame();

        assertEquals(65, harness.display.stage.getBricks().get(0).getY(), 0.0001);
        assertTrue(harness.display.stage.getBricks().stream().anyMatch(brick -> brick.getY() == 97));
        assertTrue(harness.display.stage.getBricks().stream().anyMatch(brick -> brick.getY() == 129));
        assertTrue(harness.display.stage.getBricks().stream().anyMatch(brick -> brick.getY() == 161));
        assertTrue(harness.display.stage.getBricks().stream().anyMatch(brick -> brick.getY() == 193));
    }

    private static void clearCurrentStage(Harness harness) {
        for (Brick brick : harness.display.stage.getBricks()) {
            while (!brick.isDestroyed()) harness.controller.getGame().hitBrick(brick);
        }
    }

    private static void hitBoss(Harness harness) {
        Boss boss = harness.display.boss;
        if (harness.display.ball.getVelocityY() > 0) harness.display.ball.bounceVertical();
        harness.display.ball.setPosition(boss.getX() + boss.getWidth() / 2,
                boss.getY() + boss.getHeight() + 8);
        harness.controller.update(0.05);
    }

    private static Upgrade upgrade(String name, Consumer<GameModel> effect) {
        return new Upgrade(name, "Test upgrade") {
            @Override public void apply(GameModel game) { effect.accept(game); }
        };
    }

    private static final class Harness {
        private final RecordingDisplay display = new RecordingDisplay();
        private final RecordingLogger logger = new RecordingLogger();
        private final GameController controller;

        private Harness(Random random) {
            controller = new GameController(display, new GameModel(), logger, random);
        }

        private Harness(UpgradeManager upgrades) {
            controller = new GameController(display, new GameModel(), logger, upgrades);
        }
    }

    private static final class RecordingDisplay implements GameDisplay {
        private Runnable startAction;
        private Runnable restartAction;
        private Consumer<Upgrade> upgradeAction;
        private List<Upgrade> upgradeChoices = List.of();
        private Ball ball;
        private Paddle paddle;
        private Stage stage;
        private Boss boss;
        private int score;
        private int lives;
        private int stageNumber;
        private int renderCount;
        private String endTitle;
        private String errorDetail;

        @Override public void showMenu(Runnable action) { startAction = action; }

        @Override
        public void render(Ball ball, Paddle paddle, Stage stage, Boss boss,
                           int score, int lives, int stageNumber) {
            this.ball = ball;
            this.paddle = paddle;
            this.stage = stage;
            this.boss = boss;
            this.score = score;
            this.lives = lives;
            this.stageNumber = stageNumber;
            renderCount++;
        }

        @Override public void showUpgradeChoices(List<Upgrade> choices, Consumer<Upgrade> onSelect) {
            upgradeChoices = choices;
            upgradeAction = onSelect;
        }

        @Override public void showEnd(String title, String detail, Runnable restartAction) {
            endTitle = title;
            this.restartAction = restartAction;
        }

        @Override public void showError(String detail, Runnable restartAction) {
            errorDetail = detail;
            this.restartAction = restartAction;
        }

        @Override public void hideOverlay() { }

        private void choose(Upgrade upgrade) { upgradeAction.accept(upgrade); }
        private void selectUpgrade(int index) { choose(upgradeChoices.get(index)); }
    }

    private static final class RecordingLogger implements GameEventLogger {
        private final List<String> infos = new ArrayList<>();
        private final List<String> actions = new ArrayList<>();
        private final List<Integer> scores = new ArrayList<>();
        private final List<String> warnings = new ArrayList<>();
        private final List<String> errors = new ArrayList<>();

        @Override public void info(String message) { infos.add(message); }
        @Override public void action(String message) { actions.add(message); }
        @Override public void score(int score) { scores.add(score); }
        @Override public void warning(String message) { warnings.add(message); }
        @Override public void error(String message, Throwable cause) { errors.add(message); }
    }
}
