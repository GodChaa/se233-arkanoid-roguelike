package se233.arkanoid.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
