package se233.arkanoid.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    void constructorsProvideDefaultBoundsAndStrongBrickType() {
        Brick positioned = new Brick(5, 6, 70, 28, 2, 20);
        Brick unpositioned = new Brick(2, 20);
        assertEquals(5, positioned.getX());
        assertEquals(6, positioned.getY());
        assertEquals(70, positioned.getWidth());
        assertEquals(28, positioned.getHeight());
        assertEquals(BrickType.STRONG, unpositioned.getType());
    }

    @Test
    void constructorsRejectInvalidStatsBoundsAndTypes() {
        assertThrows(IllegalArgumentException.class, () -> new Brick(0, 10));
        assertThrows(IllegalArgumentException.class, () -> new Brick(1, -1));
        assertThrows(IllegalArgumentException.class, () -> new Brick(0, 0, 0, 10, 1, 10));
        assertThrows(IllegalArgumentException.class,
                () -> new Brick(0, 0, 10, 10, (BrickType) null));
    }

    @Test
    void accessorsExposeBoundsTypeAndRemainingDurability() {
        Brick brick = new Brick(3, 4, 50, 20, BrickType.BONUS);
        assertEquals(3, brick.getX());
        assertEquals(4, brick.getY());
        assertEquals(50, brick.getWidth());
        assertEquals(20, brick.getHeight());
        assertEquals(BrickType.BONUS, brick.getType());
        assertEquals(1, brick.getHitPoints());
    }
}
