package se233.arkanoid.model;

import java.util.Objects;

public class Ball {
    private double x;
    private double y;
    private double velocityX;
    private double velocityY;

    public Ball(double x, double y, double velocityX, double velocityY) {
        this.x = x;
        this.y = y;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    public void move(double elapsedSeconds) {
        if (elapsedSeconds < 0 || !Double.isFinite(elapsedSeconds)) {
            throw new IllegalArgumentException("Elapsed time must be finite and non-negative.");
        }
        x += velocityX * elapsedSeconds;
        y += velocityY * elapsedSeconds;
    }

    public void bounceHorizontal() {
        velocityX = -velocityX;
    }

    public void bounceVertical() {
        velocityY = -velocityY;
    }

    public void bounceFromPaddle(Paddle paddle) {
        Objects.requireNonNull(paddle, "Paddle cannot be null.");
        double halfWidth = paddle.getWidth() / 2;
        double paddleCenter = paddle.getX() + halfWidth;
        double hitOffset = Math.max(-1, Math.min(1, (x - paddleCenter) / halfWidth));
        double speed = Math.hypot(velocityX, velocityY);
        if (speed == 0) return;

        double angle = Math.toRadians(hitOffset * 60);
        velocityX = speed * Math.sin(angle);
        velocityY = -speed * Math.cos(angle);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getVelocityX() {
        return velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public void setPosition(double x, double y) {
        if (!Double.isFinite(x) || !Double.isFinite(y)) {
            throw new IllegalArgumentException("Ball position must be finite.");
        }
        this.x = x;
        this.y = y;
    }

    public void multiplySpeed(double multiplier) {
        if (!Double.isFinite(multiplier) || multiplier <= 0) {
            throw new IllegalArgumentException("Speed multiplier must be positive and finite.");
        }
        velocityX *= multiplier;
        velocityY *= multiplier;
    }
}
