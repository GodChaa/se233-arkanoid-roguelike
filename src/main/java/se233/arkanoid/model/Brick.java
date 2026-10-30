package se233.arkanoid.model;

public class Brick {
    private double x;
    private double y;
    private double width = 64;
    private double height = 24;
    private final int scoreValue;
    private int hitPoints;

    public Brick(int hitPoints, int scoreValue) {
        if (hitPoints <= 0) {
            throw new IllegalArgumentException("A brick must have at least one hit point.");
        }
        if (scoreValue < 0) {
            throw new IllegalArgumentException("Score value cannot be negative.");
        }
        this.hitPoints = hitPoints;
        this.scoreValue = scoreValue;
    }

    public Brick(double x, double y, double width, double height, int hitPoints, int scoreValue) {
        this(hitPoints, scoreValue);
        if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(width)
                || !Double.isFinite(height) || width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Brick bounds must be finite and have positive dimensions.");
        }
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean hit() {
        if (isDestroyed()) {
            return false;
        }
        hitPoints--;
        return isDestroyed();
    }

    public int getHitPoints() {
        return hitPoints;
    }

    public int getScoreValue() {
        return scoreValue;
    }

    public boolean isDestroyed() {
        return hitPoints == 0;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
}
