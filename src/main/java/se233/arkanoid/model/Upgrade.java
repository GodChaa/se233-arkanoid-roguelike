package se233.arkanoid.model;

/** An effect offered between stages. Concrete upgrades override {@link #apply(GameModel)}. */
public class Upgrade {
    public void apply(GameModel game) {
        if (game == null) {
            throw new IllegalArgumentException("Game cannot be null.");
        }
    }
}
