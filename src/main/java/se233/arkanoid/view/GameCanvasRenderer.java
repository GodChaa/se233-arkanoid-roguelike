package se233.arkanoid.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import se233.arkanoid.model.Ball;
import se233.arkanoid.model.Boss;
import se233.arkanoid.model.BossProjectile;
import se233.arkanoid.model.Brick;
import se233.arkanoid.model.Stage;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import se233.arkanoid.model.Paddle;

final class GameCanvasRenderer {
    private static final double WIDTH = GameView.WIDTH;
    private static final double HEIGHT = GameView.HEIGHT;
    private static final double PADDLE_Y = GameView.PADDLE_Y;
    private static final double PADDLE_HEIGHT = GameView.PADDLE_HEIGHT;
    private static final double BALL_RADIUS = GameView.BALL_RADIUS;

    private final Canvas canvas;
    private final Image fieldSheet = loadImage("/se233/arkanoid/assets/fields.png");
    private final Image vausSheet = loadVausSheet("/se233/arkanoid/assets/vaus.png");
    private final Image blockSheet = loadImage("/se233/arkanoid/assets/blocks.png");
    private final Image effectsSheet = loadEffectsSheet(
            "/se233/arkanoid/assets/Effects.png");
    private final SpriteSheet effectsSprites = new SpriteSheet(effectsSheet);
    private final PaddleRenderer paddleRenderer = new PaddleRenderer(vausSheet);
    private final BallRenderer ballRenderer;
    private final BossRenderer bossRenderer = new BossRenderer(
            loadImage("/se233/arkanoid/assets/boss.png"));
    private final Map<Brick, Integer> previousHitPoints = new IdentityHashMap<>();
    private final Map<Brick, Animation> brickAnimations = new IdentityHashMap<>();
    private Stage animatedStage;
    private int currentLives = 3;
    private int currentStageNumber = 1;
    private double lastPaddleX = (WIDTH - 100) / 2;
    private double lastPaddleWidth = 100;
    private Timeline gameOverAnimation;

    GameCanvasRenderer(Canvas canvas) {
        this.canvas = canvas;
        this.ballRenderer = new BallRenderer(canvas, effectsSprites);
        drawBackground();
    }

    public void render(
            Ball ball,
            Paddle paddle,
            Stage stage,
            Boss boss,
            int score,
            int lives,
            int stageNumber
    ) {
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        currentLives = lives;
        currentStageNumber = stageNumber;
        drawField(graphics, stageNumber);

        if (stage != null) {
            long now = System.nanoTime();

            if (stage != animatedStage) {
                animatedStage = stage;
                previousHitPoints.clear();
                brickAnimations.clear();
            }

            List<Brick> bricks = stage.getBricks();

            // ตรวจอิฐที่ถูกชนจาก hit points ที่ลด
            for (Brick brick : bricks) {
                int currentHitPoints = brick.getHitPoints();
                Integer oldHitPoints =
                        previousHitPoints.put(brick, currentHitPoints);

                if (oldHitPoints != null
                        && currentHitPoints < oldHitPoints) {
                    brickAnimations.put(
                            brick,
                            new Animation(now, brick.isDestroyed(), effectsSprites)
                    );
                }
            }

            // วาดอิฐ,เอฟเฟกต์ด้วย sprite ที่จับคู่ตามชนิดอิฐ
            for (Brick brick : bricks) {
                Animation animation = brickAnimations.get(brick);

                if (brick.isDestroyed()) {
                    if (animation != null
                            && !animation.isFinished(now)) {
                        animation.draw(graphics, brick, now);
                    } else {
                        brickAnimations.remove(brick);
                    }
                    continue;
                }

                drawBrick(graphics, brick);

                if (animation != null) {
                    if (animation.isFinished(now)) {
                        brickAnimations.remove(brick);
                    } else {
                        animation.draw(graphics, brick, now);
                    }
                }
            }
        } else {
            animatedStage = null;
            previousHitPoints.clear();
            brickAnimations.clear();
        }

        bossRenderer.drawBoss(graphics, boss);
        bossRenderer.drawProjectiles(graphics);
        paddleRenderer.draw(graphics, paddle, currentLives);
        ballRenderer.draw(graphics, ball);

    }
    public void renderBossProjectiles(List<BossProjectile> projectiles) {
        bossRenderer.setProjectiles(projectiles);
    }
    public void renderExtraBalls(List<Ball> balls) {
        ballRenderer.renderExtraBalls(balls);
    }
    public void setPiercingBallActive(boolean active) {
        ballRenderer.setPiercingBallActive(active);
    }
    public void resetPaddleSprite() {
        paddleRenderer.reset();
    }
    public void advanceLongPaddleSprite() {
        paddleRenderer.advanceLongPaddleSprite();
    }
    public void activateSpeedPaddleSprite() {
        paddleRenderer.activateSpeedPaddleSprite();
    }
    public void showGameOverAnimation(Runnable onFinished) {
        if (!isUsable(vausSheet)) {
            onFinished.run();
            return;
        }
        if (gameOverAnimation != null) {
            gameOverAnimation.stop();
        }

        gameOverAnimation = new Timeline();
        for (int frame = 0; frame < 12; frame++) {
            final int currentFrame = frame;
            gameOverAnimation.getKeyFrames().add(new KeyFrame(
                    Duration.millis(frame * 75),
                    event -> drawGameOverFrame(currentFrame)
            ));
        }
        gameOverAnimation.setOnFinished(event -> onFinished.run());
        gameOverAnimation.playFromStart();
    }
private void drawGameOverFrame(int frame) {
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        double clearX = lastPaddleX - 20;
        double clearY = PADDLE_Y - 16;
        double clearWidth = lastPaddleWidth + 40;
        double clearHeight = PADDLE_HEIGHT + 48;
        drawFieldPatch(graphics, clearX, clearY, clearWidth, clearHeight);
        int column = frame % 4;
        int row = frame / 4;
        graphics.drawImage(
                vausSheet,
                column * 32, 72 + row * 8, 32, 8,
                lastPaddleX, PADDLE_Y,
                lastPaddleWidth, PADDLE_HEIGHT
        );
    }
private void drawFieldPatch(
            GraphicsContext graphics,
            double destX,
            double destY,
            double destWidth,
            double destHeight
    ) {
        if (!isUsable(fieldSheet)) {
            graphics.setFill(Color.rgb(17, 24, 39));
            graphics.fillRect(destX, destY, destWidth, destHeight);
            return;
        }
        int backgroundIndex = Math.floorMod(currentStageNumber - 1, 9);
        int column = backgroundIndex < 5 ? backgroundIndex : backgroundIndex - 5;
        int row = backgroundIndex < 5 ? 0 : 1;
        graphics.drawImage(
                fieldSheet,
                8 + column * 232 + destX * 208 / WIDTH,
                8 + row * 256 + destY * 232 / HEIGHT,
                destWidth * 208 / WIDTH,
                destHeight * 232 / HEIGHT,
                destX, destY, destWidth, destHeight
        );
    }
private void drawBackground() {
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        drawField(graphics, 1);
    }
private void drawField(GraphicsContext graphics, int stageNumber) {
        if (isUsable(fieldSheet)) {
            int backgroundIndex = Math.floorMod(stageNumber - 1, 9);
            int column = backgroundIndex < 5 ? backgroundIndex : backgroundIndex - 5;
            int row = backgroundIndex < 5 ? 0 : 1;
            graphics.drawImage(
                    fieldSheet,
                    8 + column * 232,
                    8 + row * 256,
                    208,
                    232,
                    0, 0, WIDTH, HEIGHT
            );
        } else {
            graphics.setFill(Color.rgb(17, 24, 39));
            graphics.fillRect(0, 0, WIDTH, HEIGHT);
        }
    }
private void drawBrick(
            GraphicsContext graphics,
            Brick brick
    ) {
        double brickWidth = brick.getWidth() - 4;
        double brickHeight = brick.getHeight() - 4;

        if (isUsable(blockSheet)) {
            int sourceX;
            int sourceY;
            switch (brick.getType()) {
                case NORMAL -> {
                    sourceX = 16;
                    sourceY = 8;
                }
                case STRONG -> {
                    sourceX = 0;
                    sourceY = brick.getHitPoints() == 1 ? 24 : 16;
                }
                case BONUS -> {
                    sourceX = 48;
                    sourceY = 8;
                }
                case SPECIAL -> {
                    sourceX = 32;
                    sourceY = 8;
                }
                default -> throw new IllegalStateException(
                        "No block sprite is mapped for " + brick.getType()
                );
            }

            graphics.drawImage(
                    blockSheet,
                    sourceX, sourceY, 16, 8,
                    brick.getX(),
                    brick.getY(),
                    brickWidth,
                    brickHeight
            );
        } else {
            graphics.setFill(Color.web(brick.getType().getColorHex()));
            graphics.fillRoundRect(
                    brick.getX(),
                    brick.getY(),
                    brickWidth,
                    brickHeight,
                    8,
                    8
            );
        }

    }
private static Image loadVausSheet(String path) {
        Image source = loadImage(path);
        if (!isUsable(source)) {
            return source;
        }

        WritableImage keyedImage = new WritableImage(
                (int) source.getWidth(),
                (int) source.getHeight()
        );
        var reader = source.getPixelReader();
        var writer = keyedImage.getPixelWriter();
        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                Color pixel = reader.getColor(x, y);
                boolean greenScreen = pixel.getRed() < 0.05
                        && pixel.getBlue() < 0.05
                        && pixel.getGreen() > 0.2;
                boolean printedText = y >= 48 && y < 72
                        && pixel.getRed() > 0.8
                        && pixel.getGreen() > 0.8
                        && pixel.getBlue() < 0.1;
                writer.setColor(x, y,
                        greenScreen || printedText ? Color.TRANSPARENT : pixel);
            }
        }
        return keyedImage;
    }

private static Image loadEffectsSheet(String path) {
        Image source = loadImage(path);
        if (!isUsable(source)) {
            return source;
        }

        WritableImage keyedImage = new WritableImage(
                (int) source.getWidth(),
                (int) source.getHeight()
        );
        var reader = source.getPixelReader();
        var writer = keyedImage.getPixelWriter();

        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                Color pixel = reader.getColor(x, y);
                boolean oliveBackground = isColorKey(
                        pixel, 171, 160, 0
                ) || isColorKey(pixel, 91, 85, 0);
                boolean purpleBackground = isColorKey(
                        pixel, 146, 39, 143
                );
                writer.setColor(
                        x,
                        y,
                        oliveBackground || purpleBackground
                                ? Color.TRANSPARENT
                                : pixel
                );
            }
        }

        return keyedImage;
    }

private static boolean isColorKey(
        Color color,
        int red,
        int green,
        int blue
) {
        double tolerance = 2.0 / 255.0;
        return Math.abs(color.getRed() - red / 255.0) <= tolerance
                && Math.abs(color.getGreen() - green / 255.0) <= tolerance
                && Math.abs(color.getBlue() - blue / 255.0) <= tolerance;
    }

private static Image loadImage(String path) {
        try (var stream =
                     GameView.class.getResourceAsStream(path)) {
            return stream == null ? null : new Image(stream);
        } catch (Exception ignored) {
            return null;
        }
    }
static boolean isUsable(Image image) {
        return image != null
                && !image.isError()
                && image.getWidth() > 0
                && image.getHeight() > 0;
    }
}
