package se233.arkanoid.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StageManagerTest {
    @Test
    void stageIsClearedOnlyAfterAllBricksAreDestroyed() {
        Brick first = new Brick(1, 10);
        Brick second = new Brick(2, 20);
        Stage stage = new Stage(List.of(first, second));

        assertFalse(stage.isCleared());
        first.hit();
        assertFalse(stage.isCleared());
        second.hit();
        assertFalse(stage.isCleared());
        second.hit();
        assertTrue(stage.isCleared());
        second.hit();
        assertTrue(stage.isCleared());
    }

    @Test
    void advancesThroughMultipleStagesInOrder() {
        Stage first = new Stage(List.of());
        Stage second = new Stage(List.of());
        StageManager manager = new StageManager(List.of(first, second));

        assertEquals(first, manager.getCurrentStage());
        assertTrue(manager.advanceToNextStage());
        assertEquals(second, manager.getCurrentStage());
        assertFalse(manager.advanceToNextStage());
    }
}
