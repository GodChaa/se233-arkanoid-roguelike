package se233.arkanoid.model;

/** A brick with hit points and a score value awarded when it is destroyed. */
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

    /** @return true only on the hit that destroys this brick. */
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
