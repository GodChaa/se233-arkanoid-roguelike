package se233.arkanoid.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BossTest {
    @Test
    void startsWithTenHealthSegments() {
        Boss boss = new Boss(640);

        assertEquals(10, boss.getHealth());
        assertEquals(10, boss.getMaxHealth());
        assertFalse(boss.isDefeated());
    }

    @Test
    void rejectsFieldsTooSmallOrNonFiniteForBoss() {
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> new Boss(159));
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> new Boss(Double.NaN));
    }

    @Test
    void losesOneHealthForEachBallHit() {
        Boss boss = new Boss(640);

        assertTrue(boss.hit());
        assertEquals(9, boss.getHealth());
        assertTrue(boss.hit());
        assertEquals(8, boss.getHealth());
    }

    @Test
    void changesMovementPhaseAfterEveryThreeAcceptedHits() {
        Boss boss = new Boss(640);
        double initialSpeed = Math.abs(boss.getVelocityX());

        for (int hit = 0; hit < 3; hit++) {
            assertTrue(boss.hit());
        }

        assertEquals(7, boss.getHealth());
        assertTrue(Math.abs(boss.getVelocityX()) > initialSpeed);
    }

    @Test
    void movesAndStaysWithinTheField() {
        Boss boss = new Boss(640);
        double startX = boss.getX();

        boss.update(1.0, 640);

        assertTrue(boss.getX() != startX);
        assertTrue(boss.getX() >= 0);
        assertTrue(boss.getX() + boss.getWidth() <= 640);
    }

    @Test
    void updateRejectsInvalidElapsedTimeAndFieldWidth() {
        Boss boss = new Boss(640);
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> boss.update(-0.1, 640));
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> boss.update(0.1, 100));
    }

    @Test
    void updateAndMaybeFireWaitsForCooldownThenProducesAShot() {
        Boss boss = new Boss(640);
        assertNull(boss.updateAndMaybeFire(0.5, 640, 320, 640));
        BossProjectile shot = boss.updateAndMaybeFire(0.45, 640, 320, 640);
        assertNotNull(shot);
        assertEquals(boss.getX() + boss.getWidth() / 2, shot.getX(), 0.0001);
    }

    @Test
    void updateAndMaybeFireResetsCooldownAfterEachShot() {
        Boss boss = new Boss(640);
        assertNotNull(boss.updateAndMaybeFire(0.9, 640, 320, 640));
        assertNull(boss.updateAndMaybeFire(0.5, 640, 320, 640));
        assertNotNull(boss.updateAndMaybeFire(1.1, 640, 320, 640));
    }

    @Test
    void intersectsDetectsBallInsideBossAndBallOutsideBoss() {
        Boss boss = new Boss(640);
        assertTrue(boss.intersects(boss.getX() + boss.getWidth() / 2,
                boss.getY() + boss.getHeight() / 2, 5));
        assertFalse(boss.intersects(0, 0, 5));
    }

    @Test
    void hitStopsApplyingDamageOnceBossIsDefeated() {
        Boss boss = new Boss(640);
        for (int hit = 0; hit < Boss.DEFAULT_HEALTH; hit++) boss.hit();
        assertFalse(boss.hit());
        assertTrue(boss.isDefeated());
    }
}
