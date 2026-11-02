package se233.arkanoid.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StageTest {
    @Test
    void constructorCopiesBricksAndRejectsNullInputs() {
        Brick brick = new Brick(1, 10);
        Stage stage = new Stage(List.of(brick));
        assertEquals(List.of(brick), stage.getBricks());
        assertThrows(NullPointerException.class, () -> new Stage(null));
    }

    @Test
    void constructorRejectsNullBrickEntries() {
        assertThrows(IllegalArgumentException.class,
                () -> new Stage(java.util.Arrays.asList(new Brick(1, 10), null)));
    }

    @Test
    void getBricksReturnsAnImmutableList() {
        Stage stage = new Stage(List.of(new Brick(1, 10)));
        assertEquals(1, stage.getBricks().size());
        assertThrows(UnsupportedOperationException.class,
                () -> stage.getBricks().clear());
    }

    @Test
    void isClearedIsTrueForEmptyOrDestroyedStages() {
        Stage empty = new Stage(List.of());
        Brick brick = new Brick(1, 10);
        Stage destroyed = new Stage(List.of(brick));
        brick.hit();
        assertTrue(empty.isCleared());
        assertTrue(destroyed.isCleared());
    }

    @Test
    void isClearedIsFalseWhileAnyBrickRemains() {
        Stage stage = new Stage(List.of(new Brick(2, 10)));
        assertFalse(stage.isCleared());
    }
}
