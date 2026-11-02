package se233.arkanoid.model;

public final class BossProjectile {
    public static final double RADIUS = 9;
    public static final double SPEED = 300;

    private double x;
    private double y;
    private final double velocityX;
    private final double velocityY;

    public BossProjectile(double x, double y, double targetX, double targetY) {
        if (!Double.isFinite(x) || !Double.isFinite(y)
                || !Double.isFinite(targetX) || !Double.isFinite(targetY)) {
            throw new IllegalArgumentException("Projectile coordinates must be finite.");
        }
        this.x = x;
        this.y = y;
        double directionX = targetX - x;
        double directionY = targetY - y;
        double length = Math.hypot(directionX, directionY);
        if (length == 0) {
            throw new IllegalArgumentException("Projectile target must differ from its origin.");
        }
        velocityX = directionX / length * SPEED;
        velocityY = directionY / length * SPEED;
    }

    public void update(double elapsedSeconds) {
        if (!Double.isFinite(elapsedSeconds) || elapsedSeconds < 0) {
            throw new IllegalArgumentException("Elapsed time must be finite and non-negative.");
        }
        x += velocityX * elapsedSeconds;
        y += velocityY * elapsedSeconds;
    }

    public boolean intersects(Paddle paddle, double paddleY, double paddleHeight) {
        double nearestX = Math.max(paddle.getX(), Math.min(x, paddle.getX() + paddle.getWidth()));
        double nearestY = Math.max(paddleY, Math.min(y, paddleY + paddleHeight));
        double dx = x - nearestX;
        double dy = y - nearestY;
        return dx * dx + dy * dy <= RADIUS * RADIUS;
    }

    public boolean isOutside(double fieldWidth, double fieldHeight) {
        return x + RADIUS < 0 || x - RADIUS > fieldWidth
                || y - RADIUS > fieldHeight;
    }

    public double getX() { return x; }
    public double getY() { return y; }
}
