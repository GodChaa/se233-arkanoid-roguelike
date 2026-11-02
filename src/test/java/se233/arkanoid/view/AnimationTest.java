package se233.arkanoid.view;

import javafx.scene.canvas.Canvas;
import se233.arkanoid.model.Brick;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnimationTest {
    @Test
    void isFinishedChangesOnlyAfterAnimationDuration() {
        Animation animation = new Animation(100, false);
        assertFalse(animation.isFinished(100 + 239_999_999L));
        assertTrue(animation.isFinished(100 + 240_000_000L));
    }

    @Test
    void isFinishedHandlesDifferentStartTimes() {
        Animation animation = new Animation(10_000, true);
        assertFalse(animation.isFinished(10_000));
        assertTrue(animation.isFinished(300_000_000));
    }

    @Test
    void drawHandlesHitAndDestroyEffects() {
        FxTestSupport.runAndWait(() -> {
            var graphics = new Canvas(200, 100).getGraphicsContext2D();
            Animation hit = new Animation(0, false);
            Animation destroyed = new Animation(0, true);
            assertDoesNotThrow(() -> hit.draw(graphics, new Brick(1, 10), 50_000_000));
            assertDoesNotThrow(() -> destroyed.draw(graphics, new Brick(1, 10), 50_000_000));
        });
    }

    @Test
    void drawHandlesTimesBeforeStartAndAfterFinish() {
        FxTestSupport.runAndWait(() -> {
            var graphics = new Canvas(200, 100).getGraphicsContext2D();
            Animation animation = new Animation(100, true);
            assertDoesNotThrow(() -> animation.draw(graphics, new Brick(1, 10), 0));
            assertDoesNotThrow(() -> animation.draw(graphics, new Brick(1, 10), 500_000_000));
        });
    }
}
