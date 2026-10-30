package se233.arkanoid.model;

public class Upgrade {
    private final String name;
    private final String description;

    public Upgrade() {
        this("Upgrade", "A gameplay upgrade.");
    }

    public Upgrade(String name, String description) {
        if (name == null || name.isBlank() || description == null || description.isBlank()) {
            throw new IllegalArgumentException("Upgrade name and description are required.");
        }
        this.name = name;
        this.description = description;
    }

    public void apply(GameModel game) {
        if (game == null) {
            throw new IllegalArgumentException("Game cannot be null.");
        }
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
}
