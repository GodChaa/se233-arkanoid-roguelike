package se233.arkanoid.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
