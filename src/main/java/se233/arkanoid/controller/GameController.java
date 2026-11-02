package se233.arkanoid.controller;

import se233.arkanoid.model.Ball;
import se233.arkanoid.model.Boss;
import se233.arkanoid.model.BossProjectile;
import se233.arkanoid.model.Brick;
import se233.arkanoid.model.BrickType;
import se233.arkanoid.model.GameModel;
import se233.arkanoid.model.GameState;
import se233.arkanoid.model.GameException;
import se233.arkanoid.model.Paddle;
import se233.arkanoid.model.Stage;
import se233.arkanoid.model.Upgrade;
import se233.arkanoid.model.UpgradeManager;
import se233.arkanoid.view.GameView;
import se233.arkanoid.view.GameDisplay;

import java.util.ArrayList;
import java.util.Iterator;
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
    private final StageController stageController = new StageController();
    private Boss boss;
    private Paddle paddle;
    private Ball ball;
    private final List<Ball> extraBalls = new ArrayList<>();
    private final List<BossProjectile> bossProjectiles = new ArrayList<>();
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
            upgrades.reset();
            stageController.startNewRun();
            boss = null;
            extraBalls.clear();
            bossProjectiles.clear();
            paddle = new Paddle((FIELD_WIDTH - 100) / 2, 100, 420, FIELD_WIDTH);
            view.resetPaddleSprite();
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

    public void startUpgradeTestGame() {
        boolean longPaddle = true;
        boolean quickPaddle = true;
        boolean extraLife = true;
        int extraBallsToAdd = 0;
        boolean fastBall = false;
        boolean slowBall = false;
        boolean scoreBonus = false;
        boolean piercingBall = true;

        startNewGame();
        if (game.getState() != GameState.PLAYING || paddle == null
                || stageController.getCurrentStage() == null) {
            return;
        }

        if (longPaddle) {
            game.increasePaddleWidth(25);
            paddle.increaseWidth(25);
            view.advanceLongPaddleSprite();
        }
        if (quickPaddle) {
            game.increasePaddleSpeed(80);
            paddle.increaseSpeed(80);
            view.activateSpeedPaddleSprite();
        }
        if (extraLife) {
            game.addLife();
        }
        for (int index = 0; index < extraBallsToAdd; index++) {
            game.addExtraBall();
        }
        if (fastBall) {
            game.multiplyBallSpeed(1.1);
        }
        if (slowBall) {
            game.multiplyBallSpeed(0.9);
        }
        if (fastBall || slowBall) {
            ball = createBall();
        }
        if (scoreBonus) {
            game.multiplyBrickScore(1.5);
        }
        if (piercingBall) {
            game.activatePiercingBall();
        }
        for (int index = 0; index < game.getExtraBallCount(); index++) {
            extraBalls.add(createExtraBall(index));
        }

        logger.info("Upgrade test game started.");
        render();
    }

    public void startBossTestGame() {
        startNewGame();
        if (stageController.getCurrentStage() == null || game.getState() != GameState.PLAYING) return;
        if (!stageController.advanceToStage(5)) return;
        boss = new Boss(FIELD_WIDTH);
        logger.info("Boss test game started at stage " + stageController.getCurrentStageNumber()
                + ". Boss health: " + boss.getHealth());
        render();
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
        if (boss != null) {
            BossProjectile fired = boss.updateAndMaybeFire(
                    elapsed,
                    FIELD_WIDTH,
                    paddle.getX() + paddle.getWidth() / 2,
                    GameView.PADDLE_Y
            );
            if (fired != null) {
                bossProjectiles.add(fired);
                logger.action("Boss fired a projectile.");
            }
        }
        updateBossProjectiles(elapsed);
        if (game.getState() != GameState.PLAYING) {
            finishGameOver();
            return;
        }

        List<Ball> activeBalls = new ArrayList<>();
        activeBalls.add(ball);
        activeBalls.addAll(extraBalls);
        List<Ball> survivors = new ArrayList<>();
        for (Ball activeBall : activeBalls) {
            ball = activeBall;
            if (updateBall(activeBall, elapsed)) survivors.add(activeBall);
            if (game.getState() != GameState.PLAYING) break;
        }
        if (game.getState() != GameState.PLAYING) {
            render();
            return;
        }
        if (survivors.isEmpty()) {
            if (game.isPiercingBallActive()) deactivatePiercingAtBoundary("bottom boundary");
            game.loseLife();
            logger.warning("Player lost a life. Remaining lives: " + game.getLives());
            if (game.getState() == GameState.PLAYING) {
                ball = createBall();
                survivors.add(ball);
                for (int index = 0; index < game.getExtraBallCount(); index++) {
                    survivors.add(createExtraBall(index));
                }
            } else {
                finishGameOver();
                return;
            }
        }
        ball = survivors.get(0);
        extraBalls.clear();
        extraBalls.addAll(survivors.subList(1, survivors.size()));

        if (boss == null && stageController.getCurrentStage().isCleared()) {
            completeCurrentStage();
        }
        render();
    }

    private boolean updateBall(Ball activeBall, double elapsed) {
        activeBall.move(elapsed);
        boolean piercingHitBoundary = false;
        if (activeBall.getX() < BALL_RADIUS && activeBall.getVelocityX() < 0) {
            activeBall.setPosition(BALL_RADIUS, activeBall.getY());
            activeBall.bounceHorizontal();
            piercingHitBoundary = game.isPiercingBallActive();
        } else if (activeBall.getX() > FIELD_WIDTH - BALL_RADIUS && activeBall.getVelocityX() > 0) {
            activeBall.setPosition(FIELD_WIDTH - BALL_RADIUS, activeBall.getY());
            activeBall.bounceHorizontal();
            piercingHitBoundary = game.isPiercingBallActive();
        }
        if (activeBall.getY() < BALL_RADIUS && activeBall.getVelocityY() < 0) {
            activeBall.setPosition(activeBall.getX(), BALL_RADIUS);
            activeBall.bounceVertical();
            piercingHitBoundary = game.isPiercingBallActive();
        }

        if (activeBall.getVelocityY() > 0 && activeBall.getY() + BALL_RADIUS >= GameView.PADDLE_Y
                && activeBall.getY() - BALL_RADIUS <= GameView.PADDLE_Y + GameView.PADDLE_HEIGHT
                && activeBall.getX() >= paddle.getX() && activeBall.getX() <= paddle.getX() + paddle.getWidth()) {
            activeBall.setPosition(activeBall.getX(), GameView.PADDLE_Y - BALL_RADIUS);
            activeBall.bounceFromPaddle(paddle);
            logger.action("Ball bounced off the paddle.");
        }

        if (activeBall.getY() - BALL_RADIUS > FIELD_HEIGHT) {
            deactivatePiercingAtBoundary("bottom boundary");
            return false;
        }

        if (boss == null) {
            handleBrickCollision();
        } else {
            handleBossCollision();
        }
        if (piercingHitBoundary) {
            deactivatePiercingAtBoundary("playfield boundary");
        }
        return true;
    }

    public GameModel getGame() { return game; }

    private void updateBossProjectiles(double elapsedSeconds) {
        Iterator<BossProjectile> iterator = bossProjectiles.iterator();
        while (iterator.hasNext()) {
            BossProjectile projectile = iterator.next();
            projectile.update(elapsedSeconds);
            if (projectile.intersects(paddle, GameView.PADDLE_Y, GameView.PADDLE_HEIGHT)) {
                game.loseLife();
                logger.warning("Boss projectile hit the paddle. Remaining lives: " + game.getLives());
                iterator.remove();
                if (game.getState() != GameState.PLAYING) {
                    bossProjectiles.clear();
                    return;
                }
            } else if (projectile.isOutside(FIELD_WIDTH, FIELD_HEIGHT)) {
                iterator.remove();
            }
        }
    }

    private void finishGameOver() {
        bossProjectiles.clear();
        render();
        view.showGameOverAnimation(() -> view.showEnd(
                "GAME OVER", "Final score: " + game.getScore(), this::startNewGame));
    }

    private void handleBrickCollision() {
        for (Brick brick : stageController.getCurrentStage().getBricks()) {
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

    private void handleBossCollision() {
        if (!boss.intersects(ball.getX(), ball.getY(), BALL_RADIUS)) return;

        if (boss.hit()) {
            game.addScore(25);
            logger.score(game.getScore());
            logger.info("Boss hit. Health remaining: " + boss.getHealth());
            if (boss.isDefeated()) {
                game.addScore(200);
                logger.score(game.getScore());
                logger.info("Boss defeated at stage " + stageController.getCurrentStageNumber() + ".");
                boss = null;
                bossProjectiles.clear();
                completeCurrentStage();
                return;
            }
        }

        if (ball.getVelocityY() > 0) {
            ball.setPosition(ball.getX(), boss.getY() - BALL_RADIUS);
        } else {
            ball.setPosition(ball.getX(), boss.getY() + boss.getHeight() + BALL_RADIUS);
        }
        ball.bounceVertical();
    }

    private void completeCurrentStage() {
        try {
            if (!stageController.hasNextStage()) {
                stageController.appendNextStage();
            }
            game.completeStage();
            logger.info("Stage " + stageController.getCurrentStageNumber() + " cleared.");
            view.showUpgradeChoices(upgrades.getChoices(3), this::selectUpgrade);
        } catch (Exception exception) {
            handleException("Could not complete the stage.", exception);
        }
    }

    private void selectUpgrade(Upgrade upgrade) {
        try {
            if (!upgrades.contains(upgrade)) {
                throw new GameException("This upgrade is no longer available.");
            }
            int previousWidthBonus = game.getPaddleWidthBonus();
            double previousSpeedBonus = game.getPaddleSpeedBonus();
            game.selectUpgrade(upgrade);
            if (upgrade.getName().equals("Long paddle")) {
                view.advanceLongPaddleSprite();
                upgrades.remove(upgrade);
            } else if (upgrade.getName().equals("Quick paddle")) {
                view.activateSpeedPaddleSprite();
            }
            paddle.increaseWidth(game.getPaddleWidthBonus() - previousWidthBonus);
            paddle.increaseSpeed(game.getPaddleSpeedBonus() - previousSpeedBonus);
            ball = createBall();
            extraBalls.clear();
            for (int index = 0; index < game.getExtraBallCount(); index++) {
                extraBalls.add(createExtraBall(index));
            }
            if (!stageController.advanceToNextStage()) {
                throw new GameException("There is no next stage after this upgrade.");
            }
            if (stageController.isCurrentStageBoss()) {
                boss = new Boss(FIELD_WIDTH);
                logger.info("Boss stage " + stageController.getCurrentStageNumber()
                        + " started. Boss health: " + boss.getHealth());
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

    private Ball createExtraBall(int index) {
        double speed = 250 * game.getBallSpeedMultiplier();
        return new Ball(FIELD_WIDTH / 2 + (index + 1) * 24, GameView.PADDLE_Y - 30,
                (index % 2 == 0 ? -1 : 1) * 190 * game.getBallSpeedMultiplier(), -speed);
    }

    private void render() {
        view.setPiercingBallActive(game.isPiercingBallActive());
        view.renderBossProjectiles(List.copyOf(bossProjectiles));
        view.render(ball, paddle, stageController.getCurrentStage(), boss, game.getScore(),
                game.getLives(), stageController.getCurrentStageNumber());
        view.renderExtraBalls(List.copyOf(extraBalls));
    }

    private void deactivatePiercingAtBoundary(String boundary) {
        if (game.isPiercingBallActive() && game.hasPiercingBallHitBrick()) {
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
        pool.add(new Upgrade("Extra ball", "Add another ball to the run.") {
            @Override public void apply(GameModel game) { game.addExtraBall(); }
        });
        pool.add(new Upgrade("Fast ball", "Increase ball speed by 10%.") {
            @Override public void apply(GameModel game) { game.multiplyBallSpeed(1.1); }
        });
        pool.add(new Upgrade("Slow ball", "Reduce ball speed by 10%.") {
            @Override public void apply(GameModel game) { game.multiplyBallSpeed(0.9); }
        });
        pool.add(new Upgrade("Score bonus", "Gain 50% more points from destroyed bricks.") {
            @Override public void apply(GameModel game) { game.multiplyBrickScore(1.5); }
        });
        pool.add(new Upgrade("Piercing ball", "Breaks through bricks until it hits a wall.") {
            @Override public void apply(GameModel game) { game.activatePiercingBall(); }
        });
        return pool;
    }

}
