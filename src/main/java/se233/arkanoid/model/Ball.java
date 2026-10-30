package se233.arkanoid.model;

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
