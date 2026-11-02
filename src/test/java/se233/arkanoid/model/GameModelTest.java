package se233.arkanoid.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameModelTest {
    @Test
    void startsWithThreeLivesZeroScoreAndPlayingState() {
        GameModel game = new GameModel();

        game.startNewGame();

        assertEquals(3, game.getLives());
        assertEquals(0, game.getScore());
        assertEquals(GameState.PLAYING, game.getState());
    }

    @Test
    void losesOneLifeWhenTheBallIsMissed() {
        GameModel game = new GameModel();
        game.startNewGame();

        game.loseLife();

        assertEquals(2, game.getLives());
        assertEquals(GameState.PLAYING, game.getState());
    }

    @Test
    void entersGameOverWhenTheLastLifeIsLost() {
        GameModel game = new GameModel();
        game.startNewGame();

        game.loseLife();
        game.loseLife();
        game.loseLife();

        assertEquals(0, game.getLives());
        assertEquals(GameState.GAME_OVER, game.getState());
    }

    @Test
    void addsPointsToTheScore() {
        GameModel game = new GameModel();
        game.startNewGame();

        game.addScore(100);
        game.addScore(50);

        assertEquals(150, game.getScore());
    }

    @Test
    void awardsBrickPointsOnlyWhenTheBrickIsDestroyed() {
        GameModel game = new GameModel();
        game.startNewGame();
        Brick brick = new Brick(2, 40);

        game.hitBrick(brick);
        assertEquals(0, game.getScore());

        game.hitBrick(brick);
        assertEquals(40, game.getScore());

        game.hitBrick(brick);
        assertEquals(40, game.getScore());
    }

    @Test
    void bonusBrickAwardsItsHigherScoreOnlyOnce() {
        GameModel game = new GameModel();
        game.startNewGame();
        Brick brick = new Brick(10, 20, 70, 28, BrickType.BONUS);

        assertTrue(game.hitBrick(brick));
        assertFalse(game.hitBrick(brick));

        assertEquals(50, game.getScore());
    }

    @Test
    void scoreUpgradeIncreasesPointsForDestroyedBricks() {
        GameModel game = new GameModel();
        game.startNewGame();
        game.multiplyBrickScore(1.5);

        game.hitBrick(new Brick(1, 40));

        assertEquals(60, game.getScore());
        assertEquals(1.5, game.getBrickScoreMultiplier());
    }

    @Test
    void newRunResetsBrickScoreMultiplier() {
        GameModel game = new GameModel();
        game.startNewGame();
        game.multiplyBrickScore(1.5);

        game.startNewGame();

        assertEquals(1.0, game.getBrickScoreMultiplier());
    }

    @Test
    void specialBrickGrantsOneLifeWhenItBreaks() {
        GameModel game = new GameModel();
        game.startNewGame();
        Brick brick = new Brick(10, 20, 70, 28, BrickType.SPECIAL);

        game.hitBrick(brick);
        game.hitBrick(brick);

        assertEquals(4, game.getLives());
        assertEquals(30, game.getScore());
    }

    @Test
    void pausesForAnUpgradeAfterAStageIsCleared() {
        GameModel game = new GameModel();
        game.startNewGame();

        game.completeStage();

        assertEquals(GameState.UPGRADE, game.getState());
    }

    @Test
    void activatesAndKeepsPiercingModeActive() {
        GameModel game = new GameModel();
        game.startNewGame();

        game.activatePiercingBall();
        game.activatePiercingBall();

        assertTrue(game.isPiercingBallActive());
        assertFalse(game.hasPiercingBallHitBrick());
    }

    @Test
    void tracksTheFirstBrickHitWhilePiercingIsActive() {
        GameModel game = new GameModel();
        game.startNewGame();
        game.activatePiercingBall();

        game.hitBrick(new Brick(1, 10));

        assertTrue(game.hasPiercingBallHitBrick());
    }

    @Test
    void deactivatesPiercingModeAndLeavesInactiveModeUnchanged() {
        GameModel game = new GameModel();
        game.startNewGame();

        game.deactivatePiercingBall();
        assertFalse(game.isPiercingBallActive());

        game.activatePiercingBall();
        game.deactivatePiercingBall();
        assertFalse(game.isPiercingBallActive());
        assertFalse(game.hasPiercingBallHitBrick());
    }

    @Test
    void resetsPiercingModeForANewRun() {
        GameModel game = new GameModel();
        game.startNewGame();
        game.activatePiercingBall();

        game.startNewGame();

        assertFalse(game.isPiercingBallActive());
    }

    @Test
    void rejectsNegativeScoresAndScoringOutsideAPlayingRun() {
        GameModel game = new GameModel();
        assertThrows(IllegalStateException.class, () -> game.addScore(1));
        game.startNewGame();
        assertThrows(IllegalArgumentException.class, () -> game.addScore(-1));
    }

    @Test
    void addsLivesDuringPlayingOrUpgradeAndRejectsOtherStates() {
        GameModel game = new GameModel();
        assertThrows(IllegalStateException.class, game::addLife);
        game.startNewGame();
        game.completeStage();
        game.addLife();
        assertEquals(4, game.getLives());
    }

    @Test
    void appliesPaddleWidthAndSpeedBonusesAndValidatesInputs() {
        GameModel game = new GameModel();
        game.increasePaddleWidth(25);
        game.increasePaddleSpeed(80);
        assertEquals(25, game.getPaddleWidthBonus());
        assertEquals(80, game.getPaddleSpeedBonus());
        assertThrows(IllegalArgumentException.class, () -> game.increasePaddleWidth(-1));
        assertThrows(IllegalArgumentException.class, () -> game.increasePaddleSpeed(Double.NaN));
    }

    @Test
    void appliesBallAndScoreMultipliersAndValidatesInputs() {
        GameModel game = new GameModel();
        game.multiplyBallSpeed(1.1);
        game.multiplyBrickScore(1.5);
        assertEquals(1.1, game.getBallSpeedMultiplier(), 0.0001);
        assertEquals(1.5, game.getBrickScoreMultiplier(), 0.0001);
        assertThrows(IllegalArgumentException.class, () -> game.multiplyBallSpeed(0));
        assertThrows(IllegalArgumentException.class, () -> game.multiplyBrickScore(Double.NaN));
    }

    @Test
    void extraBallCanBeAddedDuringARunAndIsResetForTheNextRun() {
        GameModel game = new GameModel();
        assertThrows(IllegalStateException.class, game::addExtraBall);
        game.startNewGame();
        game.addExtraBall();
        assertEquals(1, game.getExtraBallCount());
        game.startNewGame();
        assertEquals(0, game.getExtraBallCount());
    }

    @Test
    void selectUpgradeAppliesItAndRequiresUpgradeStateAndNonNullUpgrade() {
        GameModel game = new GameModel();
        game.startNewGame();
        assertThrows(IllegalStateException.class, () -> game.selectUpgrade(new Upgrade()));
        game.completeStage();
        assertThrows(IllegalArgumentException.class, () -> game.selectUpgrade(null));
        game.selectUpgrade(new Upgrade("extra", "life") {
            @Override public void apply(GameModel target) { target.addLife(); }
        });
        assertEquals(GameState.PLAYING, game.getState());
        assertEquals(4, game.getLives());
    }

    @Test
    void gettersExposeStateAndUpgradeValuesAfterUpdates() {
        GameModel game = new GameModel();
        game.startNewGame();
        game.increasePaddleWidth(10);
        game.increasePaddleSpeed(20);
        game.multiplyBallSpeed(1.2);
        game.multiplyBrickScore(1.3);
        game.addExtraBall();
        game.activatePiercingBall();
        game.hitBrick(new Brick(1, 5));
        assertEquals(3, game.getLives());
        assertEquals(7, game.getScore());
        assertEquals(GameState.PLAYING, game.getState());
        assertEquals(10, game.getPaddleWidthBonus());
        assertEquals(20, game.getPaddleSpeedBonus());
        assertEquals(1.2, game.getBallSpeedMultiplier(), 0.0001);
        assertEquals(1.3, game.getBrickScoreMultiplier(), 0.0001);
        assertEquals(1, game.getExtraBallCount());
        assertTrue(game.isPiercingBallActive());
        assertTrue(game.hasPiercingBallHitBrick());
    }
}
