package se233.arkanoid.view;

record VausSpriteFrames(int row, int column) {
    static VausSpriteFrames paddleForLives(int lives) {
        int row = lives >= 3 ? 1 : lives == 2 ? 2 : 3;
        return new VausSpriteFrames(row, 6);
    }

    static VausSpriteFrames quickExhaust() {
        return new VausSpriteFrames(7, 5);
    }

    static VausSpriteFrames quickSmoke(long nowNanos) {
        int row = 8 + (int) ((nowNanos / 140_000_000L) % 2);
        return new VausSpriteFrames(row, 5);
    }
}
