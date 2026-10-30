package se233.arkanoid.model;

import java.util.List;
import java.util.Objects;

/** A stage consists of the bricks that must be cleared to progress. */
public class Stage {
    private final List<Brick> bricks;

    public Stage(List<Brick> bricks) {
        Objects.requireNonNull(bricks, "Bricks cannot be null.");
        if (bricks.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("A stage cannot contain a null brick.");
        }
        this.bricks = List.copyOf(bricks);
    }

    public List<Brick> getBricks() {
        return bricks;
    }

    public boolean isCleared() {
        return bricks.stream().allMatch(Brick::isDestroyed);
    }
}
