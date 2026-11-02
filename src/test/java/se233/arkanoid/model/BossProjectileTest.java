package se233.arkanoid.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BossProjectileTest {
    @Test
    void constructorCreatesProjectileWithRequestedCoordinates() {
        BossProjectile projectile = new BossProjectile(10, 20, 10, 120);
        assertEquals(10, projectile.getX());
        assertEquals(20, projectile.getY());
    }

    @Test
    void constructorRejectsNonFiniteCoordinatesAndCoincidentTarget() {
        assertThrows(IllegalArgumentException.class,
                () -> new BossProjectile(Double.NaN, 20, 10, 120));
        assertThrows(IllegalArgumentException.class,
                () -> new BossProjectile(10, 20, 10, 20));
    }

    @Test
    void updateMovesProjectileTowardItsTarget() {
        BossProjectile projectile = new BossProjectile(100, 0, 100, 100);
        projectile.update(0.1);
        assertEquals(100, projectile.getX(), 0.0001);
        assertTrue(projectile.getY() > 0);
    }

    @Test
    void updateRejectsNegativeAndNonFiniteElapsedTime() {
        BossProjectile projectile = new BossProjectile(0, 0, 0, 100);
        assertThrows(IllegalArgumentException.class, () -> projectile.update(-0.1));
        assertThrows(IllegalArgumentException.class,
                () -> projectile.update(Double.POSITIVE_INFINITY));
    }

    @Test
    void intersectsDetectsPaddleOverlapAndMiss() {
        Paddle paddle = new Paddle(100, 100, 300, 640);
        BossProjectile overlapping = new BossProjectile(150, 0, 150, 100);
        BossProjectile missing = new BossProjectile(500, 0, 500, 100);
        overlapping.update(0.16);
        missing.update(0.16);
        assertTrue(overlapping.intersects(paddle, 48, 16));
        assertFalse(missing.intersects(paddle, 48, 16));
    }

    @Test
    void intersectsHandlesBothPaddleEdgesAndVerticalSeparation() {
        Paddle paddle = new Paddle(100, 100, 300, 640);
        BossProjectile edge = new BossProjectile(92, 0, 92, 100);
        BossProjectile tooHigh = new BossProjectile(150, 0, 150, 100);
        edge.update(0.16);
        tooHigh.update(0.1);
        assertTrue(edge.intersects(paddle, 48, 16));
        assertFalse(tooHigh.intersects(paddle, 48, 16));
    }

    @Test
    void isOutsideDetectsBottomAndSideExit() {
        BossProjectile below = new BossProjectile(10, 0, 10, 100);
        below.update(1);
        BossProjectile outsideRight = new BossProjectile(0, 0, 100, 1);
        outsideRight.update(1);
        assertTrue(below.isOutside(200, 100));
        assertTrue(outsideRight.isOutside(200, 100));
    }

    @Test
    void isOutsideKeepsProjectileWithinField() {
        BossProjectile projectile = new BossProjectile(10, 10, 10, 100);
        projectile.update(0.1);
        assertFalse(projectile.isOutside(200, 200));
        BossProjectile left = new BossProjectile(-100, 10, -100, 100);
        assertTrue(left.isOutside(200, 200));
    }
}
