package se233.arkanoid.model;

public class Upgrade {
    public void apply(GameModel game) {
        if (game == null) {
            throw new IllegalArgumentException("Game cannot be null.");
        }
    }
}
