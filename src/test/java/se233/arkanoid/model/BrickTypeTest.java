package se233.arkanoid.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BrickTypeTest {
    @Test
    void definesDifferentDurabilityAndScoresForEachBrickType() {
        assertEquals(1, BrickType.NORMAL.getHitPoints());
        assertEquals(2, BrickType.STRONG.getHitPoints());
        assertEquals(1, BrickType.BONUS.getHitPoints());
        assertEquals(1, BrickType.SPECIAL.getHitPoints());
        assertEquals(10, BrickType.NORMAL.getScoreValue());
        assertEquals(20, BrickType.STRONG.getScoreValue());
        assertEquals(50, BrickType.BONUS.getScoreValue());
        assertEquals(30, BrickType.SPECIAL.getScoreValue());
    }

    @Test
    void createsBrickWithStatsFromItsType() {
        Brick brick = new Brick(10, 20, 70, 28, BrickType.STRONG);

        assertEquals(BrickType.STRONG, brick.getType());
        assertEquals(2, brick.getHitPoints());
        assertEquals(20, brick.getScoreValue());
        assertEquals(70, brick.getWidth());
    }
}
