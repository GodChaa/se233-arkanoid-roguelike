package se233.arkanoid.view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import se233.arkanoid.model.Brick;

public final class Animation {
    private static final long DURATION_NANOS = 240_000_000L;

    private static final double[] PARTICLE_DIRECTIONS = {
            -1.0, -0.35,
            0.2, 0.85,
            -0.6, 0.65,
            0.0, 1.0,
            1.0, -0.8,
            -0.25, 0.55,
            0.7, 0.1,
            -0.9, -0.15
    };

    private final long startedAtNanos;
    private final boolean destroyed;

    public Animation(long startedAtNanos, boolean destroyed) {
        this.startedAtNanos = startedAtNanos;
        this.destroyed = destroyed;
    }

    public boolean isFinished(long nowNanos) {
        return nowNanos - startedAtNanos >= DURATION_NANOS;
    }

    public void draw(
            GraphicsContext graphics,
            Brick brick,
            long nowNanos
    ) {
        double progress = Math.min(
                1.0,
                Math.max(
                        0.0,
                        (double) (nowNanos - startedAtNanos)
                                / DURATION_NANOS
                )
        );

        double alpha = 1.0 - progress;
        double x = brick.getX();
        double y = brick.getY();
        double width = brick.getWidth() - 4;
        double height = brick.getHeight() - 4;

        graphics.save();
        try {
            if (!destroyed) {
                graphics.setGlobalAlpha(alpha * 0.9);
                graphics.setFill(Color.WHITE);
                graphics.fillRoundRect(
                        x, y, width, height, 8, 8
                );
                return;
            }

            double centerX = x + width / 2.0;
            double centerY = y + height / 2.0;

            graphics.setGlobalAlpha(alpha);
            graphics.setStroke(Color.ORANGE);
            graphics.setLineWidth(2.5 * alpha + 0.5);

            double ringWidth = 8 + progress * 34;
            double ringHeight = 6 + progress * 23;

            graphics.strokeOval(
                    centerX - ringWidth / 2.0,
                    centerY - ringHeight / 2.0,
                    ringWidth,
                    ringHeight
            );

            for (int i = 0; i < PARTICLE_DIRECTIONS.length; i += 2) {
                double dx = PARTICLE_DIRECTIONS[i];
                double dy = PARTICLE_DIRECTIONS[i + 1];
                double distance = progress * (14 + (i % 4) * 3);
                double size = Math.max(1.5, 4.5 * alpha);

                graphics.setFill(
                        i % 4 == 0 ? Color.YELLOW : Color.WHITE
                );
                graphics.fillOval(
                        centerX + dx * distance - size / 2.0,
                        centerY + dy * distance - size / 2.0,
                        size,
                        size
                );
            }
        } finally {
            graphics.restore();
        }
    }
}