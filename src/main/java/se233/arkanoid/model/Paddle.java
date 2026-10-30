package se233.arkanoid.model;

public class Paddle {
    private double x;
    private double width;
    private final double speed;
    private final double fieldWidth;

    public Paddle(double x, double width, double speed, double fieldWidth) {
        if (!Double.isFinite(width) || width <= 0) {
            throw new IllegalArgumentException("Paddle width must be positive and finite.");
        }
        if (!Double.isFinite(speed) || speed < 0) {
            throw new IllegalArgumentException("Paddle speed must be non-negative and finite.");
        }
        if (!Double.isFinite(fieldWidth) || fieldWidth < width) {
            throw new IllegalArgumentException("Field width must be at least the paddle width.");
        }
        this.width = width;
        this.speed = speed;
        this.fieldWidth = fieldWidth;
        setX(x);
    }

    public void moveLeft(double elapsedSeconds) {
        validateElapsedTime(elapsedSeconds);
        setX(x - speed * elapsedSeconds);
    }

    public void moveRight(double elapsedSeconds) {
        validateElapsedTime(elapsedSeconds);
        setX(x + speed * elapsedSeconds);
    }

    public void increaseWidth(double amount) {
        if (!Double.isFinite(amount) || amount < 0) {
            throw new IllegalArgumentException("Width increase must be non-negative and finite.");
        }
        width = Math.min(fieldWidth, width + amount);
        setX(x);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        if (!Double.isFinite(x)) {
            throw new IllegalArgumentException("Paddle position must be finite.");
        }
        this.x = Math.max(0, Math.min(x, fieldWidth - width));
    }

    public double getWidth() {
        return width;
    }

    public double getSpeed() {
        return speed;
    }

    private static void validateElapsedTime(double elapsedSeconds) {
        if (!Double.isFinite(elapsedSeconds) || elapsedSeconds < 0) {
            throw new IllegalArgumentException("Elapsed time must be finite and non-negative.");
        }
    }
}
