package se233.arkanoid.controller;

import se233.arkanoid.model.Ball;
import se233.arkanoid.model.Brick;
import se233.arkanoid.model.BrickType;
import se233.arkanoid.model.GameModel;
import se233.arkanoid.model.GameState;
import se233.arkanoid.model.GameException;
import se233.arkanoid.model.Paddle;
import se233.arkanoid.model.Stage;
import se233.arkanoid.model.StageManager;
import se233.arkanoid.model.Upgrade;
import se233.arkanoid.model.UpgradeManager;
import se233.arkanoid.view.GameView;
import se233.arkanoid.view.GameDisplay;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class GameController {
    private static final double FIELD_WIDTH = GameView.WIDTH;
    private static final double FIELD_HEIGHT = GameView.HEIGHT;
    private static final double BALL_RADIUS = GameView.BALL_RADIUS;

    private final GameDisplay view;
    private final GameModel game;
    private final GameEventLogger logger;
    private final UpgradeManager upgrades;
    private StageManager stages;
    private Paddle paddle;
    private Ball ball;
    private boolean leftPressed;
    private boolean rightPressed;

    public GameController(GameDisplay view) {
        this(view, new GameModel(), new GameLogger(), new Random());
    }

    GameController(GameDisplay view, GameModel game, GameEventLogger logger, Random random) {
        this(view, game, logger, new UpgradeManager(createUpgrades(), Objects.requireNonNull(random)));
    }

    GameController(GameDisplay view, GameModel game, GameEventLogger logger, UpgradeManager upgrades) {
        this.view = Objects.requireNonNull(view, "Display cannot be null.");
        this.game = Objects.requireNonNull(game, "Game model cannot be null.");
        this.logger = Objects.requireNonNull(logger, "Logger cannot be null.");
        this.upgrades = Objects.requireNonNull(upgrades, "Upgrade manager cannot be null.");
        view.showMenu(this::startNewGame);
        render();
    }

    public void startNewGame() {
        try {
            game.startNewGame();
            stages = createStageManager();
            paddle = new Paddle((FIELD_WIDTH - 100) / 2, 100, 420, FIELD_WIDTH);
            ball = createBall();
            leftPressed = false;
            rightPressed = false;
            view.hideOverlay();
            logger.info("New game started.");
            render();
        } catch (Exception exception) {
            handleException("Could not start a new game.", exception);
        }
    }

    public void setLeftPressed(boolean pressed) {
        if (leftPressed != pressed) logger.action("Paddle left key " + (pressed ? "pressed." : "released."));
        leftPressed = pressed;
    }

    public void setRightPressed(boolean pressed) {
        if (rightPressed != pressed) logger.action("Paddle right key " + (pressed ? "pressed." : "released."));
        rightPressed = pressed;
    }

    public void update(double elapsedSeconds) {
        if (game.getState() != GameState.PLAYING || ball == null || paddle == null) return;
        try {
            updateGame(elapsedSeconds);
        } catch (Exception exception) {
            handleException("The game stopped because an error occurred.", exception);
        }
    }

    private void updateGame(double elapsedSeconds) {
        double elapsed = Math.min(Math.max(elapsedSeconds, 0), 0.05);
        if (leftPressed) paddle.moveLeft(elapsed);
        if (rightPressed) paddle.moveRight(elapsed);

        ball.move(elapsed);
        if (ball.getX() < BALL_RADIUS && ball.getVelocityX() < 0) {
            ball.setPosition(BALL_RADIUS, ball.getY());
            ball.bounceHorizontal();
            deactivatePiercingAtBoundary("left wall");
        } else if (ball.getX() > FIELD_WIDTH - BALL_RADIUS && ball.getVelocityX() > 0) {
            ball.setPosition(FIELD_WIDTH - BALL_RADIUS, ball.getY());
            ball.bounceHorizontal();
            deactivatePiercingAtBoundary("right wall");
        }
        if (ball.getY() < BALL_RADIUS && ball.getVelocityY() < 0) {
            ball.setPosition(ball.getX(), BALL_RADIUS);
            ball.bounceVertical();
            deactivatePiercingAtBoundary("top wall");
        }

        if (ball.getVelocityY() > 0 && ball.getY() + BALL_RADIUS >= GameView.PADDLE_Y
                && ball.getY() - BALL_RADIUS <= GameView.PADDLE_Y + GameView.PADDLE_HEIGHT
                && ball.getX() >= paddle.getX() && ball.getX() <= paddle.getX() + paddle.getWidth()) {
            ball.setPosition(ball.getX(), GameView.PADDLE_Y - BALL_RADIUS);
            ball.bounceVertical();
            logger.action("Ball bounced off the paddle.");
        }

        if (ball.getY() - BALL_RADIUS > FIELD_HEIGHT) {
            if (game.isPiercingBallActive()) deactivatePiercingAtBoundary("bottom boundary");
            game.loseLife();
            logger.warning("Player lost a life. Remaining lives: " + game.getLives());
            if (game.getState() == GameState.PLAYING) ball = createBall();
            else view.showEnd("GAME OVER", "Final score: " + game.getScore(), this::startNewGame);
            render();
            return;
        }

        handleBrickCollision();
        if (game.getState() == GameState.PLAYING && stages.getCurrentStage().isCleared()) {
            completeCurrentStage();
        }
        render();
    }

    public GameModel getGame() { return game; }

    private void handleBrickCollision() {
        for (Brick brick : stages.getCurrentStage().getBricks()) {
            if (brick.isDestroyed()) continue;
            boolean overlaps = ball.getX() + BALL_RADIUS >= brick.getX()
                    && ball.getX() - BALL_RADIUS <= brick.getX() + brick.getWidth()
                    && ball.getY() + BALL_RADIUS >= brick.getY()
                    && ball.getY() - BALL_RADIUS <= brick.getY() + brick.getHeight();
            if (!overlaps) continue;

            boolean destroyed;
            if (game.isPiercingBallActive()) {
                destroyed = false;
                while (!brick.isDestroyed()) {
                    destroyed = game.hitBrick(brick) || destroyed;
                }
            } else {
                destroyed = game.hitBrick(brick);
            }
            if (destroyed) logger.score(game.getScore());
            logger.action(destroyed ? "Brick destroyed." : "Brick hit.");
            if (!game.isPiercingBallActive()) {
                if (ball.getVelocityY() > 0) {
                    ball.setPosition(ball.getX(), brick.getY() - BALL_RADIUS);
                } else {
                    ball.setPosition(ball.getX(), brick.getY() + brick.getHeight() + BALL_RADIUS);
                }
                ball.bounceVertical();
            }
            return;
        }
    }

    private void completeCurrentStage() {
        if (!stages.hasNextStage()) {
            game.win();
            logger.info("Player won with score " + game.getScore());
            view.showEnd("YOU WIN", "Final score: " + game.getScore(), this::startNewGame);
            return;
        }
        try {
            game.completeStage();
            logger.info("Stage " + stages.getCurrentStageNumber() + " cleared.");
            view.showUpgradeChoices(upgrades.getChoices(3), this::selectUpgrade);
        } catch (Exception exception) {
            handleException("Could not complete the stage.", exception);
        }
    }

    private void selectUpgrade(Upgrade upgrade) {
        try {
            int previousWidthBonus = game.getPaddleWidthBonus();
            double previousSpeedBonus = game.getPaddleSpeedBonus();
            game.selectUpgrade(upgrade);
            paddle.increaseWidth(game.getPaddleWidthBonus() - previousWidthBonus);
            paddle.increaseSpeed(game.getPaddleSpeedBonus() - previousSpeedBonus);
            ball = createBall();
            if (!stages.advanceToNextStage()) {
                throw new GameException("There is no next stage after this upgrade.");
            }
            logger.info("Upgrade selected: " + upgrade.getName());
            view.hideOverlay();
            render();
        } catch (Exception exception) {
            handleException("Could not apply the selected upgrade.", exception);
        }
    }

    private Ball createBall() {
        Ball next = new Ball(FIELD_WIDTH / 2, GameView.PADDLE_Y - 30, 190, -250);
        next.multiplySpeed(game.getBallSpeedMultiplier());
        return next;
    }

    private void render() {
        view.render(ball, paddle, stages == null ? null : stages.getCurrentStage(), game.getScore(),
                game.getLives(), stages == null ? 1 : stages.getCurrentStageNumber());
    }

    private StageManager createStageManager() throws GameException {
        try {
            return new StageManager(createStages());
        } catch (IllegalArgumentException exception) {
            throw new GameException("The stage configuration is invalid.", exception);
        }
    }

    private void deactivatePiercingAtBoundary(String boundary) {
        if (game.isPiercingBallActive()) {
            game.deactivatePiercingBall();
            logger.info("Piercing ball ended at the " + boundary + ".");
        }
    }

    private void handleException(String message, Exception exception) {
        logger.error(message, exception);
        view.showError(message, this::startNewGame);
    }

    private static List<Upgrade> createUpgrades() {
        List<Upgrade> pool = new ArrayList<>();
        pool.add(new Upgrade("Long paddle", "Increase paddle width by 25.") {
            @Override public void apply(GameModel game) { game.increasePaddleWidth(25); }
        });
        pool.add(new Upgrade("Quick paddle", "Increase paddle movement speed.") {
            @Override public void apply(GameModel game) { game.increasePaddleSpeed(80); }
        });
        pool.add(new Upgrade("Extra life", "Gain one additional life.") {
            @Override public void apply(GameModel game) { game.addLife(); }
        });
        pool.add(new Upgrade("Fast ball", "Increase ball speed by 10%.") {
            @Override public void apply(GameModel game) { game.multiplyBallSpeed(1.1); }
        });
        pool.add(new Upgrade("Piercing ball", "Breaks through bricks until it hits a wall.") {
            @Override public void apply(GameModel game) { game.activatePiercingBall(); }
        });
        return pool;
    }

    private static List<Stage> createStages() {
        List<Stage> result = new ArrayList<>();
        result.add(createStage(
                "...NN...",
                "..NNNN..",
                ".NNBBNN.",
                "NNBNNBNN",
                ".NNNNNN."));
        result.add(createStage(
                ".SSSSSS.",
                "NBNNNNBN",
                ".SSNXNS.",
                "NBNNNNBN",
                ".SSNNSS.",
                ".SSSSSS."));
        result.add(createStage(
                "...SS...",
                "..NBBN..",
                ".NSSSSN.",
                "NBSXSNBN",
                ".NSSSSN.",
                "..NBBN..",
                "...SS..."));
        return result;
    }

    private static Stage createStage(String... layout) {
        List<Brick> bricks = new ArrayList<>();
        int columns = 8;
        double brickWidth = 70;
        double gap = 6;
        double left = (FIELD_WIDTH - columns * (brickWidth + gap) + gap) / 2;
        for (int row = 0; row < layout.length; row++) {
            for (int column = 0; column < columns; column++) {
                BrickType type = switch (layout[row].charAt(column)) {
                    case 'N' -> BrickType.NORMAL;
                    case 'S' -> BrickType.STRONG;
                    case 'B' -> BrickType.BONUS;
                    case 'X' -> BrickType.SPECIAL;
                    case '.' -> null;
                    default -> throw new IllegalArgumentException("Unknown brick in stage layout.");
                };
                if (type != null) {
                    bricks.add(new Brick(left + column * (brickWidth + gap), 65 + row * 32,
                            brickWidth, 28, type));
                }
            }
        }
        return new Stage(bricks);
    }
}
