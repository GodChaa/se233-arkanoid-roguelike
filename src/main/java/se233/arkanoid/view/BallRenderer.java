package se233.arkanoid.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import se233.arkanoid.model.Ball;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class BallRenderer {
    private static final long TRAIL_LIFETIME_NANOS = 180_000_000L;
    private static final long TRAIL_SAMPLE_INTERVAL_NANOS = 24_000_000L;
    private static final long BALL_FRAME_DURATION_NANOS = 90_000_000L;
    private static final int MAX_TRAIL_POINTS = 6;
    private static final double TRAIL_SPRITE_SIZE = 12;
    private static final double BALL_SPRITE_SIZE = 16;

    private final Canvas canvas;
    private final Image[] ballFrames;
    private final Map<Ball, Deque<TrailPoint>> trails =
            new IdentityHashMap<>();
    private boolean piercingBallActive;
    private Ball mainBall;

    BallRenderer(Canvas canvas, SpriteSheet effectsSheet) {
        this.canvas = canvas;
        this.ballFrames = extractBallFrames(effectsSheet);
    }

    void draw(GraphicsContext graphics, Ball ball) {
        mainBall = ball;
        if (ball == null) return;

        drawBallWithTrail(
                graphics,
                ball,
                piercingBallActive ? Color.CYAN : Color.GOLD
        );
    }

    void renderExtraBalls(List<Ball> balls) {
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        Set<Ball> activeBalls = Collections.newSetFromMap(
                new IdentityHashMap<>()
        );
        if (mainBall != null) activeBalls.add(mainBall);
        activeBalls.addAll(balls);
        trails.keySet().retainAll(activeBalls);

        for (Ball ball : balls) {
            drawBallWithTrail(
                    graphics,
                    ball,
                    piercingBallActive ? Color.CYAN : Color.LIGHTSKYBLUE
            );
        }
    }

    void setPiercingBallActive(boolean active) {
        piercingBallActive = active;
    }

    private void drawBallWithTrail(
            GraphicsContext graphics,
            Ball ball,
            Color ballColor
    ) {
        long now = System.nanoTime();
        Deque<TrailPoint> trail = trails.computeIfAbsent(
                ball,
                ignored -> new ArrayDeque<>()
        );
        addTrailPoint(trail, ball.getX(), ball.getY(), now);
        drawTrail(graphics, trail, now);

        if (ballFrames.length > 0) {
            Image ballFrame = ballFrames[getBallFrameIndex(now)];
            graphics.drawImage(
                    ballFrame,
                    ball.getX() - BALL_SPRITE_SIZE / 2,
                    ball.getY() - BALL_SPRITE_SIZE / 2,
                    BALL_SPRITE_SIZE,
                    BALL_SPRITE_SIZE
            );
        } else {
            graphics.setFill(ballColor);
            graphics.fillOval(
                    ball.getX() - GameView.BALL_RADIUS,
                    ball.getY() - GameView.BALL_RADIUS,
                    GameView.BALL_RADIUS * 2,
                    GameView.BALL_RADIUS * 2
            );
        }

        if (piercingBallActive) {
            graphics.setStroke(Color.WHITE);
            graphics.setLineWidth(2);
            graphics.strokeOval(
                    ball.getX() - GameView.BALL_RADIUS - 3,
                    ball.getY() - GameView.BALL_RADIUS - 3,
                    GameView.BALL_RADIUS * 2 + 6,
                    GameView.BALL_RADIUS * 2 + 6
            );
        }
    }

    private static void addTrailPoint(
            Deque<TrailPoint> trail,
            double x,
            double y,
            long now
    ) {
        TrailPoint last = trail.peekLast();
        boolean intervalPassed = last == null
                || now - last.timeNanos() >= TRAIL_SAMPLE_INTERVAL_NANOS;
        boolean movedEnough = last == null
                || Math.hypot(x - last.x(), y - last.y()) >= 3;

        if (intervalPassed && movedEnough) {
            if (last != null && Math.hypot(x - last.x(), y - last.y()) > 80) {
                trail.clear();
            }
            trail.addLast(new TrailPoint(x, y, now));
        }

        while (!trail.isEmpty()
                && (now - trail.peekFirst().timeNanos() > TRAIL_LIFETIME_NANOS
                || trail.size() > MAX_TRAIL_POINTS)) {
            trail.removeFirst();
        }
    }

    private void drawTrail(
            GraphicsContext graphics,
            Deque<TrailPoint> trail,
            long now
    ) {
        if (ballFrames.length == 0 || trail.isEmpty()) return;

        int index = 0;
        int count = trail.size();
        int currentFrame = getBallFrameIndex(now);
        graphics.save();
        try {
            for (TrailPoint point : trail) {
                double age = Math.max(
                        0,
                        Math.min(
                                1,
                                (double) (now - point.timeNanos())
                                        / TRAIL_LIFETIME_NANOS
                        )
                );
                double strength = (double) (index + 1) / count;
                graphics.setGlobalAlpha(0.48 * strength * (1 - age));
                graphics.drawImage(
                        ballFrames[(currentFrame + index) % ballFrames.length],
                        point.x() - TRAIL_SPRITE_SIZE / 2,
                        point.y() - TRAIL_SPRITE_SIZE / 2,
                        TRAIL_SPRITE_SIZE,
                        TRAIL_SPRITE_SIZE
                );
                index++;
            }
        } finally {
            graphics.restore();
        }
    }

    private int getBallFrameIndex(long now) {
        if (ballFrames.length == 0) return 0;
        long elapsedFrames = now / BALL_FRAME_DURATION_NANOS;
        return (int) ((elapsedFrames + 1) % ballFrames.length);
    }

    private static Image[] extractBallFrames(SpriteSheet effectsSheet) {
        if (effectsSheet == null
                || effectsSheet.getImage().getWidth() < 128
                || effectsSheet.getImage().getHeight() < 64) {
            return new Image[0];
        }
        Image[] frames = new Image[8];
        for (int frame = 0; frame < frames.length; frame++) {
            frames[frame] = effectsSheet.getSprite(
                    frame * 16, 48, 16, 16
            );
        }
        return frames;
    }

    private record TrailPoint(double x, double y, long timeNanos) { }
}
