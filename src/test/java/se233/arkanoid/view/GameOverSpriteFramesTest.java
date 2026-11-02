package se233.arkanoid.view;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GameOverSpriteFramesTest {
    @Test
    void frameGridStartsAtBottomLeftAndUsesThirtyTwoByEightCells() {
        assertEquals(new GameOverSpriteFrames(0, 76, 32, 8),
                GameOverSpriteFrames.frame(0));
        assertEquals(new GameOverSpriteFrames(32, 76, 32, 8),
                GameOverSpriteFrames.frame(1));
        assertEquals(new GameOverSpriteFrames(0, 84, 32, 8),
                GameOverSpriteFrames.frame(4));
    }

    @Test
    void frameGridEndsAtBottomRightOfThreeByFourSheet() {
        assertEquals(new GameOverSpriteFrames(96, 92, 32, 8),
                GameOverSpriteFrames.frame(11));
    }

    @Test
    void rejectsIndicesOutsideTheTwelveGameOverFrames() {
        assertThrows(IllegalArgumentException.class,
                () -> GameOverSpriteFrames.frame(-1));
        assertThrows(IllegalArgumentException.class,
                () -> GameOverSpriteFrames.frame(12));
    }
}
