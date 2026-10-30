package se233.arkanoid.model;

public class Brick {
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
}
