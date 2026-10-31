package se233.arkanoid.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import se233.arkanoid.model.Ball;
import se233.arkanoid.model.Boss;
import se233.arkanoid.model.Brick;
import se233.arkanoid.model.Paddle;
import se233.arkanoid.model.Stage;
import se233.arkanoid.model.Upgrade;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class GameView implements GameDisplay {
    public static final double WIDTH = 640;
    public static final double HEIGHT = 700;
    public static final double PADDLE_Y = 640;
    public static final double PADDLE_HEIGHT = 16;
    public static final double BALL_RADIUS = 8;

    private final BorderPane root = new BorderPane();
    private final Canvas canvas = new Canvas(WIDTH, HEIGHT);
    private final StackPane playArea = new StackPane(canvas);
    private final Label message = new Label();

    private final Label stageValue = new Label("01");
    private final Label scoreValue = new Label("00000");
    private final Label livesValue = new Label("03");
    private StackPane activeOverlay;

    private final Image fieldSheet =
            loadImage("/se233/arkanoid/assets/fields.png");
    private final Image vausSheet =
            loadImage("/se233/arkanoid/assets/vaus.png");
    private final Image blockSheet =
            loadImage("/se233/arkanoid/assets/blocks.png");
    private final Image bossImage =
            loadImage("/se233/arkanoid/assets/boss.png");

    private final Map<Brick, Integer> previousHitPoints =
            new IdentityHashMap<>();
    private final Map<Brick, Animation> brickAnimations =
            new IdentityHashMap<>();

    private Stage animatedStage;

    public GameView() {
        root.setStyle("-fx-background-color: #111827;");
        root.setTop(createHud());
        root.setCenter(playArea);

        drawBackground();
    }

    public BorderPane getRoot() {
        return root;
    }

    private HBox createHud() {
        Label title = new Label("ARKANOID");
        title.setStyle(
                "-fx-font-size: 16px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: #f8fafc;"
        );

        Label subtitle = new Label("ROGUELIKE");
        subtitle.setStyle(
                "-fx-font-size: 9px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: #38bdf8;"
                        + "-fx-letter-spacing: 2px;"
        );

        VBox brand = new VBox(2, title, subtitle);
        brand.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox hud = new HBox(
                10,
                brand,
                spacer,
                createStatCard("STAGE", stageValue, "#38bdf8"),
                createStatCard("SCORE", scoreValue, "#facc15"),
                createStatCard("LIVES", livesValue, "#fb7185")
        );

        hud.setAlignment(Pos.CENTER_LEFT);
        hud.setPadding(new Insets(6, 16, 6, 16));
        hud.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #111c31, #0b1322);"
                        + "-fx-border-color: transparent transparent #26364f transparent;"
                        + "-fx-border-width: 0 0 1 0;"
        );

        return hud;
    }

    private VBox createStatCard(
            String caption,
            Label value,
            String accentColor
    ) {
        Label captionLabel = new Label(caption);
        captionLabel.setStyle(
                "-fx-font-size: 9px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: #94a3b8;"
                        + "-fx-letter-spacing: 1px;"
        );

        value.setStyle(
                "-fx-font-size: 18px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: " + accentColor + ";"
        );

        VBox card = new VBox(1, captionLabel, value);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setMinWidth(76);
        card.setPadding(new Insets(4, 10, 4, 10));
        card.setStyle(
                "-fx-background-color: #17243a;"
                        + "-fx-background-radius: 8;"
                        + "-fx-border-color: #2b3b55;"
                        + "-fx-border-radius: 8;"
        );

        return card;
    }

    @Override
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
        drawField(graphics);

        if (stage != null) {
            long now = System.nanoTime();

            if (stage != animatedStage) {
                animatedStage = stage;
                previousHitPoints.clear();
                brickAnimations.clear();
            }

            List<Brick> bricks = stage.getBricks();

            // ตรวจจับอิฐที่ถูกชนจากค่า hit points ที่ลดลง
            for (Brick brick : bricks) {
                int currentHitPoints = brick.getHitPoints();
                Integer oldHitPoints =
                        previousHitPoints.put(brick, currentHitPoints);

                if (oldHitPoints != null
                        && currentHitPoints < oldHitPoints) {
                    brickAnimations.put(
                            brick,
                            new Animation(now, brick.isDestroyed())
                    );
                }
            }

            // วาดอิฐและเอฟเฟกต์ โดยใช้ลำดับเดิมของแต่ละก้อน
            for (int brickIndex = 0;
                 brickIndex < bricks.size();
                 brickIndex++) {
                Brick brick = bricks.get(brickIndex);
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

                drawBrick(graphics, brick, brickIndex);

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

        drawBoss(graphics, boss);
        drawPaddle(graphics, paddle);
        drawBall(graphics, ball);

        stageValue.setText(String.format("%02d", stageNumber));
        scoreValue.setText(String.format("%05d", score));
        livesValue.setText(String.format("%02d", lives));
    }

    private void drawBoss(GraphicsContext graphics, Boss boss) {
        if (boss == null || boss.isDefeated()) {
            return;
        }

        if (isUsable(bossImage)) {
            graphics.drawImage(
                    bossImage,
                    boss.getX(),
                    boss.getY(),
                    boss.getWidth(),
                    boss.getHeight()
            );
        } else {
            graphics.setFill(Color.MEDIUMPURPLE);
            graphics.fillRoundRect(
                    boss.getX(),
                    boss.getY(),
                    boss.getWidth(),
                    boss.getHeight(),
                    18,
                    18
            );
            graphics.setFill(Color.WHITE);
            graphics.fillText(
                    "BOSS",
                    boss.getX() + boss.getWidth() / 2 - 16,
                    boss.getY() + boss.getHeight() / 2 + 5
            );
        }

        double segmentGap = 3;
        double segmentWidth =
                (boss.getWidth()
                        - segmentGap * (boss.getMaxHealth() - 1))
                        / boss.getMaxHealth();

        for (int segment = 0; segment < boss.getMaxHealth(); segment++) {
            graphics.setFill(
                    segment < boss.getHealth()
                            ? Color.LIGHTGREEN
                            : Color.DARKSLATEGRAY
            );
            graphics.fillRoundRect(
                    boss.getX() + segment * (segmentWidth + segmentGap),
                    boss.getY() - 14,
                    segmentWidth,
                    8,
                    4,
                    4
            );
        }
    }

    private void drawPaddle(GraphicsContext graphics, Paddle paddle) {
        if (paddle == null) {
            return;
        }

        if (isUsable(vausSheet)) {
            graphics.drawImage(
                    vausSheet,
                    32, 0, 32, 8,
                    paddle.getX(), PADDLE_Y,
                    paddle.getWidth(), PADDLE_HEIGHT
            );
        } else {
            graphics.setFill(Color.WHITESMOKE);
            graphics.fillRoundRect(
                    paddle.getX(),
                    PADDLE_Y,
                    paddle.getWidth(),
                    PADDLE_HEIGHT,
                    8,
                    8
            );
        }
    }

    private void drawBall(GraphicsContext graphics, Ball ball) {
        if (ball == null) {
            return;
        }

        graphics.setFill(Color.GOLD);
        graphics.fillOval(
                ball.getX() - BALL_RADIUS,
                ball.getY() - BALL_RADIUS,
                BALL_RADIUS * 2,
                BALL_RADIUS * 2
        );
    }

    @Override
    public void renderExtraBalls(List<Ball> balls) {
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        graphics.setFill(Color.LIGHTSKYBLUE);

        for (Ball extraBall : balls) {
            graphics.fillOval(
                    extraBall.getX() - BALL_RADIUS,
                    extraBall.getY() - BALL_RADIUS,
                    BALL_RADIUS * 2,
                    BALL_RADIUS * 2
            );
        }
    }

    @Override
    public void showMenu(Runnable startAction) {
        showOverlay(
                "ARKANOID ROGUELIKE",
                "Break every brick. Choose upgrades between stages.",
                List.of(button("Start Game", startAction))
        );
    }

    @Override
    public void showUpgradeChoices(
            List<Upgrade> choices,
            Consumer<Upgrade> onSelect
    ) {
        List<Button> buttons = choices.stream()
                .map(upgrade -> {
                    Button button = new Button(
                            upgrade.getName()
                                    + "\n"
                                    + upgrade.getDescription()
                    );
                    button.setMaxWidth(Double.MAX_VALUE);
                    button.setOnAction(
                            event -> onSelect.accept(upgrade)
                    );
                    return button;
                })
                .toList();

        showOverlay(
                "STAGE CLEARED",
                "Choose one upgrade to continue.",
                buttons
        );
    }

    @Override
    public void showEnd(
            String title,
            String detail,
            Runnable restartAction
    ) {
        showOverlay(
                title,
                detail,
                List.of(button("Play Again", restartAction))
        );
    }

    @Override
    public void showError(
            String detail,
            Runnable restartAction
    ) {
        showOverlay(
                "GAME ERROR",
                detail,
                List.of(button("Restart", restartAction))
        );
    }

    private Button button(String text, Runnable action) {
        Button button = new Button(text);
        button.setOnAction(event -> action.run());
        return button;
    }

    @Override
    public void hideOverlay() {
        if (activeOverlay != null) {
            playArea.getChildren().remove(activeOverlay);
            activeOverlay = null;
        }
    }

    private void showOverlay(
            String title,
            String detail,
            List<Button> buttons
    ) {
        hideOverlay();

        VBox popup = new VBox(14);
        popup.setAlignment(Pos.CENTER);
        popup.setPadding(new Insets(28));
        popup.setPrefSize(400, buttons.size() >= 3 ? 330 : 230);
        popup.setMinSize(360, 210);
        popup.setMaxSize(400, buttons.size() >= 3 ? 330 : 230);
        popup.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #17243a, #0e1729);"
                        + "-fx-background-radius: 18;"
                        + "-fx-border-color: #38bdf8;"
                        + "-fx-border-width: 1.5;"
                        + "-fx-border-radius: 18;"
                        + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.55), 22, 0.2, 0, 8);"
        );

        Label heading = new Label(title);
        heading.setTextFill(Color.WHITE);
        heading.setStyle(
                "-fx-font-size: 25px;"
                        + "-fx-font-weight: bold;"
        );

        message.setText(detail);
        message.setTextFill(Color.LIGHTGRAY);
        message.setWrapText(true);
        message.setAlignment(Pos.CENTER);
        message.setStyle("-fx-font-size: 14px;");

        for (Button button : buttons) {
            button.setStyle(
                    "-fx-background-color: #38bdf8;"
                            + "-fx-text-fill: #071426;"
                            + "-fx-font-size: 14px;"
                            + "-fx-font-weight: bold;"
                            + "-fx-background-radius: 9;"
                            + "-fx-padding: 9 22 9 22;"
            );
        }

        popup.getChildren().addAll(heading, message);
        popup.getChildren().addAll(buttons);

        Region scrim = new Region();
        scrim.setStyle("-fx-background-color: rgba(3, 8, 21, 0.58);");

        activeOverlay = new StackPane(scrim, popup);
        activeOverlay.setPrefSize(WIDTH, HEIGHT);
        activeOverlay.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        StackPane.setAlignment(popup, Pos.CENTER);

        playArea.getChildren().add(activeOverlay);
    }

    private void drawBackground() {
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        drawField(graphics);
    }

    private void drawField(GraphicsContext graphics) {
        if (isUsable(fieldSheet)) {
            graphics.drawImage(
                    fieldSheet,
                    8, 8, 208, 232,
                    0, 0, WIDTH, HEIGHT
            );
        } else {
            graphics.setFill(Color.rgb(17, 24, 39));
            graphics.fillRect(0, 0, WIDTH, HEIGHT);
        }
    }

    private void drawBrick(
            GraphicsContext graphics,
            Brick brick,
            int index
    ) {
        double brickWidth = brick.getWidth() - 4;
        double brickHeight = brick.getHeight() - 4;
        Color typeColor = Color.web(brick.getType().getColorHex());

        if (isUsable(blockSheet)) {
            int columns = Math.max(
                    1,
                    (int) blockSheet.getWidth() / 16
            );
            int sourceX = (index % columns) * 16;

            graphics.drawImage(
                    blockSheet,
                    sourceX, 0, 16, 8,
                    brick.getX(),
                    brick.getY(),
                    brickWidth,
                    brickHeight
            );
        } else {
            graphics.setFill(typeColor);
            graphics.fillRoundRect(
                    brick.getX(),
                    brick.getY(),
                    brickWidth,
                    brickHeight,
                    8,
                    8
            );
        }

        // แถบสีและตัวอักษรช่วยแยกชนิดของอิฐ
        graphics.setFill(typeColor);
        graphics.fillRect(
                brick.getX(),
                brick.getY(),
                brickWidth,
                4
        );

        graphics.setFill(Color.WHITE);
        graphics.fillText(
                brick.getType().getSymbol(),
                brick.getX() + brickWidth / 2 - 4,
                brick.getY() + brickHeight / 2 + 4
        );
    }

    private static Image loadImage(String path) {
        try (var stream =
                     GameView.class.getResourceAsStream(path)) {
            return stream == null ? null : new Image(stream);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static boolean isUsable(Image image) {
        return image != null
                && !image.isError()
                && image.getWidth() > 0
                && image.getHeight() > 0;
    }
}
