package se233.arkanoid.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BallTest {
    @Test
    void movesAccordingToItsVelocityAndElapsedTime() {
        Ball ball = new Ball(10, 20, 3, -4);

        ball.move(2.0);

        assertEquals(16.0, ball.getX(), 0.0001);
        assertEquals(12.0, ball.getY(), 0.0001);
    }

    @Test
    void reversesHorizontalVelocityWhenItBouncesOffASideWall() {
        Ball ball = new Ball(10, 20, 3, -4);

        ball.bounceHorizontal();

        assertEquals(-3.0, ball.getVelocityX(), 0.0001);
        assertEquals(-4.0, ball.getVelocityY(), 0.0001);
    }

    @Test
    void reversesVerticalVelocityWhenItBouncesOffATopOrPaddle() {
        Ball ball = new Ball(10, 20, 3, -4);

        ball.bounceVertical();

        assertEquals(3.0, ball.getVelocityX(), 0.0001);
        assertEquals(4.0, ball.getVelocityY(), 0.0001);
    }

    @Test
    void paddleCenterReturnsTheBallStraightUpWithoutChangingItsSpeed() {
        Ball ball = new Ball(150, 600, 80, 200);
        Paddle paddle = new Paddle(100, 100, 420, 640);
        double speed = Math.hypot(ball.getVelocityX(), ball.getVelocityY());

        ball.bounceFromPaddle(paddle);

        assertEquals(0, ball.getVelocityX(), 0.0001);
        assertTrue(ball.getVelocityY() < 0);
        assertEquals(speed, Math.hypot(ball.getVelocityX(), ball.getVelocityY()), 0.0001);
    }

    @Test
    void paddleEdgesAimTheBallTowardTheSideThatWasHit() {
        Paddle paddle = new Paddle(100, 100, 420, 640);
        Ball leftHit = new Ball(100, 600, 0, 200);
        Ball rightHit = new Ball(200, 600, 0, 200);

        leftHit.bounceFromPaddle(paddle);
        rightHit.bounceFromPaddle(paddle);

        assertTrue(leftHit.getVelocityX() < 0);
        assertTrue(leftHit.getVelocityY() < 0);
        assertTrue(rightHit.getVelocityX() > 0);
        assertTrue(rightHit.getVelocityY() < 0);
        assertEquals(Math.tan(Math.toRadians(60)),
                Math.abs(rightHit.getVelocityX() / rightHit.getVelocityY()), 0.0001);
    }

    @Test
    void constructorAndGettersPreserveInitialState() {
        Ball ball = new Ball(12, 34, -5, 6);
        assertEquals(12, ball.getX());
        assertEquals(34, ball.getY());
        assertEquals(-5, ball.getVelocityX());
        assertEquals(6, ball.getVelocityY());
    }

    @Test
    void moveRejectsInvalidElapsedTime() {
        Ball ball = new Ball(0, 0, 1, 1);
        assertThrows(IllegalArgumentException.class, () -> ball.move(-1));
        assertThrows(IllegalArgumentException.class, () -> ball.move(Double.NaN));
    }

    @Test
    void setPositionUpdatesCoordinatesAndRejectsNonFiniteValues() {
        Ball ball = new Ball(0, 0, 1, 1);
        ball.setPosition(20, 30);
        assertEquals(20, ball.getX());
        assertEquals(30, ball.getY());
        assertThrows(IllegalArgumentException.class, () -> ball.setPosition(Double.NaN, 0));
        assertThrows(IllegalArgumentException.class, () -> ball.setPosition(0, Double.POSITIVE_INFINITY));
    }

    @Test
    void multiplySpeedChangesBothComponentsAndRejectsInvalidFactors() {
        Ball ball = new Ball(0, 0, 3, -4);
        ball.multiplySpeed(2);
        assertEquals(6, ball.getVelocityX());
        assertEquals(-8, ball.getVelocityY());
        assertThrows(IllegalArgumentException.class, () -> ball.multiplySpeed(0));
        assertThrows(IllegalArgumentException.class, () -> ball.multiplySpeed(Double.NaN));
    }

    @Test
    void bounceFromPaddleRejectsNullAndKeepsAStationaryBallStationary() {
        Ball ball = new Ball(0, 0, 0, 0);
        assertThrows(NullPointerException.class, () -> ball.bounceFromPaddle(null));
        ball.bounceFromPaddle(new Paddle(0, 100, 100, 200));
        assertEquals(0, ball.getVelocityX());
        assertEquals(0, ball.getVelocityY());
    }
}
