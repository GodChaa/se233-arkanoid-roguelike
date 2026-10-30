package se233.arkanoid.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
    void losesOneHealthPerAcceptedHitAndBrieflyRejectsRepeatedHits() {
        Boss boss = new Boss(640);

        assertTrue(boss.hit());
        assertEquals(9, boss.getHealth());
        assertFalse(boss.hit());
        assertEquals(9, boss.getHealth());

        boss.update(Boss.HIT_COOLDOWN_SECONDS, 640);
        assertTrue(boss.hit());
        assertEquals(8, boss.getHealth());
    }

    @Test
    void changesMovementPhaseAfterEveryThreeAcceptedHits() {
        Boss boss = new Boss(640);
        double initialSpeed = Math.abs(boss.getVelocityX());

        for (int hit = 0; hit < 3; hit++) {
            assertTrue(boss.hit());
            if (hit < 2) boss.update(Boss.HIT_COOLDOWN_SECONDS, 640);
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
}
