package se233.arkanoid.view;

import javafx.stage.Stage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameStageTest {
    @Test
    void startConfiguresAndShowsTheGameWindow() {
        FxTestSupport.runAndWait(() -> {
            Stage stage = new Stage();
            new GameStage().start(stage);
            assertEquals("Arkanoid Roguelike", stage.getTitle());
            assertNotNull(stage.getScene());
            assertTrue(stage.isShowing());
            stage.close();
        });
    }

    @Test
    void startCreatesFixedSizeWindowAndCanCloseIt() {
        FxTestSupport.runAndWait(() -> {
            Stage stage = new Stage();
            new GameStage().start(stage);
            assertFalse(stage.isResizable());
            stage.close();
            assertFalse(stage.isShowing());
        });
    }
}
