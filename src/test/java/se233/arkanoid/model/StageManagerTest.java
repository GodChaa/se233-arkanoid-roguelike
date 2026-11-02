package se233.arkanoid.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    void constructorRejectsNullEmptyAndNullContainingLists() {
        assertThrows(NullPointerException.class, () -> new StageManager(null));
        assertThrows(IllegalArgumentException.class, () -> new StageManager(List.of()));
        assertThrows(IllegalArgumentException.class,
                () -> new StageManager(java.util.Arrays.asList(new Stage(List.of()), null)));
    }

    @Test
    void stageNumberAndCountTrackAppendedStages() {
        StageManager manager = new StageManager(List.of(new Stage(List.of())));
        assertEquals(1, manager.getCurrentStageNumber());
        assertEquals(1, manager.getStageCount());
        manager.appendStage(new Stage(List.of()));
        manager.advanceToNextStage();
        assertEquals(2, manager.getCurrentStageNumber());
        assertEquals(2, manager.getStageCount());
    }

    @Test
    void hasNextStageAndAppendStageHandleBothStates() {
        StageManager manager = new StageManager(List.of(new Stage(List.of())));
        assertFalse(manager.hasNextStage());
        manager.appendStage(new Stage(List.of()));
        assertTrue(manager.hasNextStage());
        assertThrows(NullPointerException.class, () -> manager.appendStage(null));
    }

    @Test
    void getCurrentStageReturnsTheStageAtTheActiveIndex() {
        Stage first = new Stage(List.of());
        Stage second = new Stage(List.of(new Brick(1, 1)));
        StageManager manager = new StageManager(List.of(first, second));
        assertEquals(first, manager.getCurrentStage());
        manager.advanceToNextStage();
        assertEquals(second, manager.getCurrentStage());
    }
}
