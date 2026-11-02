package se233.arkanoid.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaddleTest {
    @Test
    void movesLeftAndRightBasedOnSpeedAndElapsedTime() {
        Paddle paddle = new Paddle(200, 80, 100, 500);

        paddle.moveLeft(1.0);
        assertEquals(100.0, paddle.getX(), 0.0001);

        paddle.moveRight(0.5);
        assertEquals(150.0, paddle.getX(), 0.0001);
    }

    @Test
    void cannotMoveOutsideTheLeftOrRightFieldBoundaries() {
        Paddle paddle = new Paddle(10, 80, 100, 500);

        paddle.moveLeft(1.0);
        assertEquals(0.0, paddle.getX(), 0.0001);

        paddle.setX(450);
        paddle.moveRight(1.0);
        assertEquals(420.0, paddle.getX(), 0.0001);
    }

    @Test
    void widerPaddleRemainsInsideTheField() {
        Paddle paddle = new Paddle(400, 80, 100, 500);

        paddle.increaseWidth(40);

        assertEquals(120.0, paddle.getWidth(), 0.0001);
        assertEquals(380.0, paddle.getX(), 0.0001);
    }

    @Test
    void constructorRejectsInvalidDimensionsAndSpeeds() {
        assertThrows(IllegalArgumentException.class, () -> new Paddle(0, 0, 100, 500));
        assertThrows(IllegalArgumentException.class, () -> new Paddle(0, 100, Double.NaN, 500));
        assertThrows(IllegalArgumentException.class, () -> new Paddle(0, 200, 100, 100));
    }

    @Test
    void gettersAndSetXClampPaddleToField() {
        Paddle paddle = new Paddle(25, 80, 120, 500);
        assertEquals(25, paddle.getX());
        assertEquals(80, paddle.getWidth());
        assertEquals(120, paddle.getSpeed());
        paddle.setX(1000);
        assertEquals(420, paddle.getX());
        assertThrows(IllegalArgumentException.class, () -> paddle.setX(Double.NaN));
    }

    @Test
    void moveAndWidthMethodsRejectInvalidInputsAndRespectLimits() {
        Paddle paddle = new Paddle(100, 80, 100, 500);
        assertThrows(IllegalArgumentException.class, () -> paddle.moveLeft(-1));
        assertThrows(IllegalArgumentException.class, () -> paddle.moveRight(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> paddle.increaseWidth(-1));
        paddle.increaseWidth(1000);
        assertEquals(500, paddle.getWidth());
        assertEquals(0, paddle.getX());
    }

    @Test
    void increaseSpeedAccumulatesValidBonusAndRejectsInvalidBonus() {
        Paddle paddle = new Paddle(0, 80, 100, 500);
        paddle.increaseSpeed(50);
        assertEquals(150, paddle.getSpeed());
        assertThrows(IllegalArgumentException.class, () -> paddle.increaseSpeed(-1));
        assertThrows(IllegalArgumentException.class, () -> paddle.increaseSpeed(Double.POSITIVE_INFINITY));
    }
}
