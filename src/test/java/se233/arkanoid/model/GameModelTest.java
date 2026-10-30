package se233.arkanoid.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void pausesForAnUpgradeAfterAStageIsCleared() {
        GameModel game = new GameModel();
        game.startNewGame();

        game.completeStage();

        assertEquals(GameState.UPGRADE, game.getState());
    }
}
