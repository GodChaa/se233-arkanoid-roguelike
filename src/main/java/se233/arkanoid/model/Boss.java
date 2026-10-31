package se233.arkanoid.model;

public final class Boss {
    public static final int DEFAULT_HEALTH = 10;
    public static final double HIT_COOLDOWN_SECONDS = 0.35;
    public static final double WIDTH = 160;
    public static final double HEIGHT = 160;
    public static final double Y = 105;
    private static final double INITIAL_SPEED = 110;

    private final double width = WIDTH;
    private final double height = HEIGHT;
    private int health = DEFAULT_HEALTH;
    private int hitsTaken;
    private double x;
    private double velocityX = INITIAL_SPEED;
    private double hitCooldown;

    public Boss(double fieldWidth) {
        if (!Double.isFinite(fieldWidth) || fieldWidth < WIDTH) {
            throw new IllegalArgumentException("Field width must be finite and wide enough for the boss.");
        }
        x = (fieldWidth - width) / 2;
    }

    public void update(double elapsedSeconds, double fieldWidth) {
        if (!Double.isFinite(elapsedSeconds) || elapsedSeconds < 0) {
            throw new IllegalArgumentException("Elapsed time must be finite and non-negative.");
        }
        if (!Double.isFinite(fieldWidth) || fieldWidth < width) {
            throw new IllegalArgumentException("Field width must be finite and wide enough for the boss.");
        }
        hitCooldown = Math.max(0, hitCooldown - elapsedSeconds);
        x += velocityX * elapsedSeconds;
        if (x < 0) {
            x = 0;
            velocityX = Math.abs(velocityX);
        } else if (x + width > fieldWidth) {
            x = fieldWidth - width;
            velocityX = -Math.abs(velocityX);
        }
    }

    public boolean hit() {
        if (isDefeated() || hitCooldown > 0) return false;
        health--;
        hitsTaken++;
        hitCooldown = HIT_COOLDOWN_SECONDS;
        if (hitsTaken % 3 == 0) velocityX = -velocityX * 1.3;
        return true;
    }

    public boolean intersects(double ballX, double ballY, double ballRadius) {
        double nearestX = Math.max(x, Math.min(ballX, x + width));
        double nearestY = Math.max(Y, Math.min(ballY, Y + height));
        double distanceX = ballX - nearestX;
        double distanceY = ballY - nearestY;
        return distanceX * distanceX + distanceY * distanceY <= ballRadius * ballRadius;
    }

    public int getHealth() { return health; }
    public int getMaxHealth() { return DEFAULT_HEALTH; }
    public int getHitsTaken() { return hitsTaken; }
    public boolean isDefeated() { return health == 0; }
    public double getX() { return x; }
    public double getY() { return Y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public double getVelocityX() { return velocityX; }
}
