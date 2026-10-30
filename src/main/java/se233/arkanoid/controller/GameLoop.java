package se233.arkanoid.controller;

import javafx.animation.AnimationTimer;

public final class GameLoop extends AnimationTimer {
    private final GameController controller;
    private long previousFrameNanos;

    public GameLoop(GameController controller) {
        this.controller = controller;
    }

    @Override
    public void start() {
        previousFrameNanos = 0;
        super.start();
    }

    @Override
    public void handle(long now) {
        if (previousFrameNanos == 0) {
            previousFrameNanos = now;
            return;
        }
        double elapsedSeconds = (now - previousFrameNanos) / 1_000_000_000.0;
        previousFrameNanos = now;
        controller.update(elapsedSeconds);
    }
}
