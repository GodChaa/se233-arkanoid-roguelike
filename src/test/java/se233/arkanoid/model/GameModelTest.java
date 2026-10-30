package se233.arkanoid.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
    }

    @Test
    void resetsPiercingModeForANewRun() {
        GameModel game = new GameModel();
        game.startNewGame();
        game.activatePiercingBall();

        game.startNewGame();

        assertFalse(game.isPiercingBallActive());
    }
}
