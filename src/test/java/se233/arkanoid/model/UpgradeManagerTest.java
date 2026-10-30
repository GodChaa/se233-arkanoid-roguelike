package se233.arkanoid.model;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
}
