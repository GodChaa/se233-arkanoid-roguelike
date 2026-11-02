package se233.arkanoid.view;

import javafx.scene.canvas.Canvas;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpriteSheetTest {
    @Test
    void constructorLoadsKnownSpriteResources() {
        SpriteSheet sheet = new SpriteSheet("/se233/arkanoid/assets/vaus.png");
        assertNotNull(sheet.getImage());
        assertEquals(208, sheet.getImage().getWidth());
    }

    @Test
    void constructorRejectsMissingResource() {
        assertThrows(IllegalArgumentException.class, () -> new SpriteSheet("/missing.png"));
        assertThrows(IllegalArgumentException.class, () -> new SpriteSheet(""));
    }

    @Test
    void getSpriteReturnsRequestedCropAndRejectsOutOfBounds() {
        SpriteSheet sheet = new SpriteSheet("/se233/arkanoid/assets/vaus.png");
        assertEquals(32, sheet.getSprite(16, 0, 32, 8).getWidth());
        assertThrows(IllegalArgumentException.class, () -> sheet.getSprite(200, 0, 16, 8));
    }

    @Test
    void getSpriteRejectsNegativeAndEmptyDimensions() {
        SpriteSheet sheet = new SpriteSheet("/se233/arkanoid/assets/vaus.png");
        assertThrows(IllegalArgumentException.class, () -> sheet.getSprite(-1, 0, 8, 8));
        assertThrows(IllegalArgumentException.class, () -> sheet.getSprite(0, 0, 0, 8));
    }

    @Test
    void drawRendersValidCropToCanvas() {
        FxTestSupport.runAndWait(() -> {
            SpriteSheet sheet = new SpriteSheet("/se233/arkanoid/assets/vaus.png");
            var graphics = new Canvas(100, 100).getGraphicsContext2D();
            assertDoesNotThrow(() -> sheet.draw(graphics, 16, 0, 32, 8, 10, 10, 64, 16));
        });
    }

    @Test
    void drawRejectsInvalidCropBeforeDrawing() {
        FxTestSupport.runAndWait(() -> {
            SpriteSheet sheet = new SpriteSheet("/se233/arkanoid/assets/vaus.png");
            var graphics = new Canvas(100, 100).getGraphicsContext2D();
            assertThrows(IllegalArgumentException.class,
                    () -> sheet.draw(graphics, -1, 0, 16, 8, 0, 0, 16, 8));
        });
    }

    @Test
    void getImageReturnsTheLoadedImageInstance() {
        SpriteSheet sheet = new SpriteSheet("/se233/arkanoid/assets/blocks.png");
        assertSame(sheet.getImage(), sheet.getImage());
        assertTrue(sheet.getImage().getHeight() > 0);
    }
}
