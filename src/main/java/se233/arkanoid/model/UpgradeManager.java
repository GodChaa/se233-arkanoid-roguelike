package se233.arkanoid.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class UpgradeManager {
    private final List<Upgrade> upgradePool;
    private final List<Upgrade> availableUpgrades;
    private final Random random;

    public UpgradeManager(List<Upgrade> availableUpgrades, Random random) {
        Objects.requireNonNull(availableUpgrades, "Upgrade pool cannot be null.");
        if (availableUpgrades.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Upgrade pool cannot contain null entries.");
        }
        if (availableUpgrades.isEmpty()) {
            throw new IllegalArgumentException("At least one upgrade is required.");
        }
        this.upgradePool = List.copyOf(availableUpgrades);
        this.availableUpgrades = new ArrayList<>(this.upgradePool);
        this.random = Objects.requireNonNull(random, "Random source cannot be null.");
    }

    public List<Upgrade> getChoices(int count) {
        if (count < 0 || count > availableUpgrades.size()) {
            throw new IllegalArgumentException("Choice count must be between zero and pool size.");
        }
        List<Upgrade> shuffled = new ArrayList<>(availableUpgrades);
        Collections.shuffle(shuffled, random);
        return List.copyOf(shuffled.subList(0, count));
    }

    public void apply(Upgrade selectedUpgrade, GameModel game) {
        Objects.requireNonNull(selectedUpgrade, "Selected upgrade cannot be null.");
        Objects.requireNonNull(game, "Game cannot be null.");
        if (!availableUpgrades.contains(selectedUpgrade)) {
            throw new IllegalArgumentException("Selected upgrade is not in the available pool.");
        }
        selectedUpgrade.apply(game);
    }

    public boolean contains(Upgrade upgrade) {
        return availableUpgrades.contains(upgrade);
    }

    public void remove(Upgrade upgrade) {
        availableUpgrades.remove(upgrade);
    }

    public void reset() {
        availableUpgrades.clear();
        availableUpgrades.addAll(upgradePool);
    }
}
