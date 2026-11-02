package se233.arkanoid.view;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VausSpriteFramesTest {
    @Test
    void paddleUsesTopLifeFrameForThreeOrMoreLives() {
        assertEquals(new VausSpriteFrames(1, 6), VausSpriteFrames.paddleForLives(3));
        assertEquals(new VausSpriteFrames(1, 6), VausSpriteFrames.paddleForLives(4));
        assertEquals(new VausSpriteFrames(1, 6), VausSpriteFrames.paddleForLives(6));
    }

    @Test
    void paddleUsesMatchingFrameForTwoAndOneLife() {
        assertEquals(new VausSpriteFrames(2, 6), VausSpriteFrames.paddleForLives(2));
        assertEquals(new VausSpriteFrames(3, 6), VausSpriteFrames.paddleForLives(1));
    }

    @Test
    void quickExhaustUsesItsSpriteFrame() {
        assertEquals(7, VausSpriteFrames.quickExhaust().row());
        assertEquals(5, VausSpriteFrames.quickExhaust().column());
    }

    @Test
    void quickSmokeAlternatesBetweenRowsEightAndNine() {
        assertEquals(new VausSpriteFrames(8, 5), VausSpriteFrames.quickSmoke(0));
        assertEquals(new VausSpriteFrames(9, 5),
                VausSpriteFrames.quickSmoke(140_000_000L));
        assertEquals(new VausSpriteFrames(8, 5),
                VausSpriteFrames.quickSmoke(280_000_000L));
    }
}
