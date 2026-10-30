package se233.arkanoid.model;

public class GameModel {
    public static final int DEFAULT_LIVES = 3;

    private int lives;
    private int score;
    private GameState state = GameState.MENU;
    private int paddleWidthBonus;
    private double paddleSpeedBonus;
    private double ballSpeedMultiplier = 1.0;
    private boolean piercingBallActive;

    public void startNewGame() {
        lives = DEFAULT_LIVES;
        score = 0;
        paddleWidthBonus = 0;
        paddleSpeedBonus = 0;
        ballSpeedMultiplier = 1.0;
        piercingBallActive = false;
        state = GameState.PLAYING;
    }

    public void addScore(int points) {
        requireState(GameState.PLAYING, "Score can only change while playing.");
        if (points < 0) {
            throw new IllegalArgumentException("Points cannot be negative.");
        }
        score = Math.addExact(score, points);
    }

    public boolean hitBrick(Brick brick) {
        requireState(GameState.PLAYING, "Bricks can only be hit while playing.");
        if (brick == null) {
            throw new IllegalArgumentException("Brick cannot be null.");
        }
        boolean destroyed = brick.hit();
        if (destroyed) {
            addScore(brick.getScoreValue());
        }
        return destroyed;
    }

    public void loseLife() {
        if (state != GameState.PLAYING || lives == 0) {
            return;
        }
        lives--;
        if (lives == 0) {
            state = GameState.GAME_OVER;
        }
    }

    public void addLife() {
        if (state != GameState.PLAYING && state != GameState.UPGRADE) {
            throw new IllegalStateException("A life can only be added during a run.");
        }
        lives++;
    }

    public void increasePaddleWidth(int amount) {
        if (amount < 0) throw new IllegalArgumentException("Width increase cannot be negative.");
        paddleWidthBonus = Math.addExact(paddleWidthBonus, amount);
    }

    public void increasePaddleSpeed(double amount) {
        if (!Double.isFinite(amount) || amount < 0) {
            throw new IllegalArgumentException("Speed increase must be non-negative and finite.");
        }
        paddleSpeedBonus += amount;
    }

    public void multiplyBallSpeed(double multiplier) {
        if (!Double.isFinite(multiplier) || multiplier <= 0) {
            throw new IllegalArgumentException("Ball speed multiplier must be positive and finite.");
        }
        ballSpeedMultiplier *= multiplier;
    }

    public void activatePiercingBall() {
        if (state != GameState.PLAYING && state != GameState.UPGRADE) {
            throw new IllegalStateException("Piercing mode can only be activated during a run.");
        }
        piercingBallActive = true;
    }

    public void deactivatePiercingBall() {
        piercingBallActive = false;
    }

    public void completeStage() {
        requireState(GameState.PLAYING, "A stage can only be completed while playing.");
        state = GameState.UPGRADE;
    }

    public void selectUpgrade(Upgrade upgrade) {
        requireState(GameState.UPGRADE, "An upgrade can only be selected after a stage.");
        if (upgrade == null) {
            throw new IllegalArgumentException("Upgrade cannot be null.");
        }
        upgrade.apply(this);
        state = GameState.PLAYING;
    }

    public void win() {
        requireState(GameState.PLAYING, "A run can only be won while playing.");
        state = GameState.VICTORY;
    }

    public int getLives() {
        return lives;
    }

    public int getScore() {
        return score;
    }

    public GameState getState() {
        return state;
    }

    public int getPaddleWidthBonus() { return paddleWidthBonus; }
    public double getPaddleSpeedBonus() { return paddleSpeedBonus; }
    public double getBallSpeedMultiplier() { return ballSpeedMultiplier; }
    public boolean isPiercingBallActive() { return piercingBallActive; }

    private void requireState(GameState expected, String message) {
        if (state != expected) {
            throw new IllegalStateException(message);
        }
    }
}
