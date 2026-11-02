package se233.arkanoid.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {
    @Test
    void declaresAllExpectedStatesInOrder() {
        assertArrayEquals(new GameState[]{GameState.MENU, GameState.PLAYING,
                GameState.UPGRADE, GameState.GAME_OVER}, GameState.values());
    }

    @Test
    void valueOfFindsStatesAndRejectsUnknownNames() {
        assertSame(GameState.PLAYING, GameState.valueOf("PLAYING"));
        assertThrows(IllegalArgumentException.class, () -> GameState.valueOf("PAUSED"));
    }
}
