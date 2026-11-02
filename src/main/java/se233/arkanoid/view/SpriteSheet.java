
package se233.arkanoid.view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.io.InputStream;

public class SpriteSheet {

    private final Image image;

    public SpriteSheet(String resourcePath) {
        InputStream stream =
                getClass().getResourceAsStream(resourcePath);

        if (stream == null) {
            throw new IllegalArgumentException(
                    "Cannot find sprite: " + resourcePath
            );
        }

        image = new Image(stream);

        if (image.isError()) {
            throw new IllegalArgumentException(
                    "Cannot load sprite: " + resourcePath,
                    image.getException()
            );
        }
    }

    public SpriteSheet(Image image) {
        if (image == null || image.isError()
                || image.getWidth() <= 0 || image.getHeight() <= 0) {
            throw new IllegalArgumentException(
                    "Sprite sheet image is missing or invalid."
            );
        }
        this.image = image;
    }

    public Image getSprite(int x, int y, int width, int height) {
        if (x < 0 || y < 0 || width <= 0 || height <= 0
                || x + width > image.getWidth()
                || y + height > image.getHeight()) {
            throw new IllegalArgumentException(
                    "Sprite coordinates are outside the image."
            );
        }

        return new WritableImage(
                image.getPixelReader(),
                x, y, width, height
        );
    }

    public void draw(
            GraphicsContext gc,
            int x, int y,
            int width, int height,
            double destX, double destY,
            double destWidth, double destHeight
    ) {
        Image sprite = getSprite(x, y, width, height);

        gc.drawImage(
                sprite,
                destX, destY,
                destWidth, destHeight
        );
    }

    public Image getImage() {
        return image;
    }
}
