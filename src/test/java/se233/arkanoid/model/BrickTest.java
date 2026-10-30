package se233.arkanoid.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BrickTest {
    @Test
    void takesDamageUntilItsHitPointsReachZero() {
        Brick brick = new Brick(2, 100);

        assertFalse(brick.hit());
        assertEquals(1, brick.getHitPoints());
        assertFalse(brick.isDestroyed());

        assertTrue(brick.hit());
        assertEquals(0, brick.getHitPoints());
        assertTrue(brick.isDestroyed());
    }

    @Test
    void destroyedBrickStaysDestroyedAndCannotBeDestroyedTwice() {
        Brick brick = new Brick(1, 50);

        assertTrue(brick.hit());
        assertFalse(brick.hit());
        assertTrue(brick.isDestroyed());
    }

    @Test
    void exposesItsScoreValueForTheGameModelToAwardOnDestruction() {
        Brick brick = new Brick(1, 75);

        assertEquals(75, brick.getScoreValue());
    }
}
