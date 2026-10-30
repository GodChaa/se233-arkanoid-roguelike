package se233.arkanoid.model;

public class GameModel {
    public static final int DEFAULT_LIVES = 3;

    private int lives;
    private int score;
    private GameState state = GameState.MENU;

    public void startNewGame() {
        lives = DEFAULT_LIVES;
        score = 0;
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

    private void requireState(GameState expected, String message) {
        if (state != expected) {
            throw new IllegalStateException(message);
        }
    }
}
