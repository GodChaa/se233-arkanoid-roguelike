package se233.arkanoid.view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import se233.arkanoid.model.Paddle;

import java.util.Random;

/** Draws the paddle and its visual upgrade effects. */
final class PaddleRenderer {
    private static final int VAUS_SHEET_X_OFFSET = 16;
    private final Image vausSheet;
    private final Random spriteRandom = new Random();
    private int selectedPaddleRow;
    private int longPaddleSteps;
    private boolean speedPaddleUpgrade;

    PaddleRenderer(Image vausSheet) {
        this.vausSheet = vausSheet;
    }

    void draw(GraphicsContext graphics, Paddle paddle, int lives) {
        if (paddle == null) return;

        if (GameCanvasRenderer.isUsable(vausSheet)) {
            VausSpriteFrames frame = lives >= 1
                    ? VausSpriteFrames.paddleForLives(lives)
                    : new VausSpriteFrames(selectedPaddleRow + 1, 2 + longPaddleSteps);
            graphics.drawImage(vausSheet,
                    sourceX(frame), sourceY(frame), 32, 8,
                    paddle.getX(), GameView.PADDLE_Y,
                    paddle.getWidth(), GameView.PADDLE_HEIGHT);
            if (speedPaddleUpgrade) drawExhaust(graphics, paddle);
        } else {
            graphics.setFill(Color.WHITESMOKE);
            graphics.fillRoundRect(paddle.getX(), GameView.PADDLE_Y,
                    paddle.getWidth(), GameView.PADDLE_HEIGHT, 8, 8);
        }
    }

    void reset() {
        selectedPaddleRow = spriteRandom.nextInt(6);
        longPaddleSteps = 0;
        speedPaddleUpgrade = false;
    }

    void advanceLongPaddleSprite() {
        longPaddleSteps = Math.min(1, longPaddleSteps + 1);
    }

    void activateSpeedPaddleSprite() {
        speedPaddleUpgrade = true;
    }

    private void drawExhaust(GraphicsContext graphics, Paddle paddle) {
        double effectWidth = Math.min(32, paddle.getWidth() / 2);
        double effectX = paddle.getX() + (paddle.getWidth() - effectWidth) / 2;
        VausSpriteFrames smoke = VausSpriteFrames.quickSmoke(System.nanoTime());
        VausSpriteFrames exhaust = VausSpriteFrames.quickExhaust();
        graphics.drawImage(vausSheet, sourceX(smoke), sourceY(smoke), 32, 8,
                effectX, GameView.PADDLE_Y + GameView.PADDLE_HEIGHT + 8,
                effectWidth, 8);
        graphics.drawImage(vausSheet, sourceX(exhaust), sourceY(exhaust), 32, 8,
                effectX, GameView.PADDLE_Y + GameView.PADDLE_HEIGHT - 2,
                effectWidth, 8);
    }

    private static int sourceX(VausSpriteFrames frame) {
        return VAUS_SHEET_X_OFFSET + (frame.column() - 1) * 32;
    }

    private static int sourceY(VausSpriteFrames frame) {
        return (frame.row() - 1) * 8;
    }
}
