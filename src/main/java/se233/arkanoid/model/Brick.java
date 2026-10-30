package se233.arkanoid.model;

public class Brick {
    private final double x;
    private final double y;
    private final double width;
    private final double height;
    private final int scoreValue;
    private final BrickType type;
    private int hitPoints;

    public Brick(int hitPoints, int scoreValue) {
        this(0, 0, 64, 24, hitPoints, scoreValue,
                hitPoints > 1 ? BrickType.STRONG : BrickType.NORMAL);
    }

    public Brick(double x, double y, double width, double height, int hitPoints, int scoreValue) {
        this(x, y, width, height, hitPoints, scoreValue,
                hitPoints > 1 ? BrickType.STRONG : BrickType.NORMAL);
    }

    public Brick(double x, double y, double width, double height, BrickType type) {
        this(x, y, width, height, requireType(type).getHitPoints(), type.getScoreValue(), type);
    }

    private Brick(double x, double y, double width, double height, int hitPoints,
                  int scoreValue, BrickType type) {
        if (hitPoints <= 0) {
            throw new IllegalArgumentException("A brick must have at least one hit point.");
        }
        if (scoreValue < 0) {
            throw new IllegalArgumentException("Score value cannot be negative.");
        }
        if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(width)
                || !Double.isFinite(height) || width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Brick bounds must be finite and have positive dimensions.");
        }
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.hitPoints = hitPoints;
        this.scoreValue = scoreValue;
        this.type = requireType(type);
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
    public BrickType getType() { return type; }

    private static BrickType requireType(BrickType type) {
        if (type == null) throw new IllegalArgumentException("Brick type cannot be null.");
        return type;
    }
}
