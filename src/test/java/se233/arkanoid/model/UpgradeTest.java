package se233.arkanoid.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpgradeTest {
    @Test
    void defaultConstructorProvidesDefaultNameAndDescription() {
        Upgrade upgrade = new Upgrade();
        assertEquals("Upgrade", upgrade.getName());
        assertEquals("A gameplay upgrade.", upgrade.getDescription());
    }

    @Test
    void namedConstructorStoresNameAndDescription() {
        Upgrade upgrade = new Upgrade("Fast ball", "Increase ball speed.");
        assertEquals("Fast ball", upgrade.getName());
        assertEquals("Increase ball speed.", upgrade.getDescription());
    }

    @Test
    void namedConstructorRejectsMissingOrBlankValues() {
        assertThrows(IllegalArgumentException.class, () -> new Upgrade(null, "description"));
        assertThrows(IllegalArgumentException.class, () -> new Upgrade("name", " "));
    }

    @Test
    void applyRequiresANonNullGame() {
        Upgrade upgrade = new Upgrade();
        assertThrows(IllegalArgumentException.class, () -> upgrade.apply(null));
        GameModel game = new GameModel();
        game.startNewGame();
        assertDoesNotThrow(() -> upgrade.apply(game));
    }
}
