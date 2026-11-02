package se233.arkanoid.model;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UpgradeManagerTest {
    @Test
    void offersTheRequestedNumberOfUpgradesFromTheAvailablePool() {
        Upgrade first = new Upgrade();
        Upgrade second = new Upgrade();
        Upgrade third = new Upgrade();
        List<Upgrade> pool = List.of(first, second, third);
        UpgradeManager manager = new UpgradeManager(pool, new Random(7));

        List<Upgrade> choices = manager.getChoices(3);

        assertEquals(3, choices.size());
        assertTrue(pool.containsAll(choices));
    }

    @Test
    void appliesTheSelectedUpgradeToTheGame() {
        GameModel game = new GameModel();
        game.startNewGame();
        Upgrade upgrade = new Upgrade() {
            @Override
            public void apply(GameModel target) {
                target.addScore(25);
            }
        };
        UpgradeManager manager = new UpgradeManager(List.of(upgrade), new Random(1));

        manager.apply(upgrade, game);

        assertEquals(25, game.getScore());
    }

    @Test
    void removingAnUpgradeHidesItUntilTheNextRunReset() {
        Upgrade longPaddle = new Upgrade("Long paddle", "Increase paddle size.");
        Upgrade second = new Upgrade("Second", "Second choice.");
        Upgrade third = new Upgrade("Third", "Third choice.");
        Upgrade fourth = new Upgrade("Fourth", "Fourth choice.");
        UpgradeManager manager = new UpgradeManager(
                List.of(longPaddle, second, third, fourth), new Random(1));

        manager.remove(longPaddle);

        assertEquals(3, manager.getChoices(3).size());
        assertFalse(manager.contains(longPaddle));

        manager.reset();

        assertTrue(manager.contains(longPaddle));
    }

    @Test
    void constructorRejectsNullEmptyAndNullContainingPools() {
        assertThrows(NullPointerException.class, () -> new UpgradeManager(null, new Random(1)));
        assertThrows(IllegalArgumentException.class,
                () -> new UpgradeManager(List.of(), new Random(1)));
        assertThrows(IllegalArgumentException.class,
                () -> new UpgradeManager(java.util.Arrays.asList(new Upgrade(), null), new Random(1)));
    }

    @Test
    void getChoicesSupportsZeroAndRejectsCountsOutsidePoolSize() {
        UpgradeManager manager = new UpgradeManager(List.of(new Upgrade(), new Upgrade()), new Random(1));
        assertTrue(manager.getChoices(0).isEmpty());
        assertThrows(IllegalArgumentException.class, () -> manager.getChoices(-1));
        assertThrows(IllegalArgumentException.class, () -> manager.getChoices(3));
    }

    @Test
    void applyRejectsUnavailableUpgradesAndNullInputs() {
        Upgrade upgrade = new Upgrade();
        UpgradeManager manager = new UpgradeManager(List.of(upgrade), new Random(1));
        GameModel game = new GameModel();
        game.startNewGame();
        assertThrows(NullPointerException.class, () -> manager.apply(null, game));
        assertThrows(NullPointerException.class, () -> manager.apply(upgrade, null));
        assertThrows(IllegalArgumentException.class, () -> manager.apply(new Upgrade(), game));
    }

    @Test
    void containsAndRemoveTrackItemsAcrossRemovalAndReset() {
        Upgrade first = new Upgrade("first", "first");
        Upgrade second = new Upgrade("second", "second");
        UpgradeManager manager = new UpgradeManager(List.of(first, second), new Random(1));
        assertTrue(manager.contains(first));
        manager.remove(first);
        assertFalse(manager.contains(first));
        manager.reset();
        assertTrue(manager.contains(first));
        assertTrue(manager.contains(second));
    }
}
