package se233.arkanoid.view;

record GameOverSpriteFrames(int x, int y, int width, int height) {
    static final int COLUMNS = 4;
    static final int ROWS = 3;
    static final int FRAME_WIDTH = 32;
    static final int FRAME_HEIGHT = 8;
    static final int SHEET_X = 0;
    static final int SHEET_Y = 76;

    static GameOverSpriteFrames frame(int index) {
        if (index < 0 || index >= COLUMNS * ROWS) {
            throw new IllegalArgumentException("Game-over frame index is out of range.");
        }
        int column = index % COLUMNS;
        int row = index / COLUMNS;
        return new GameOverSpriteFrames(
                SHEET_X + column * FRAME_WIDTH,
                SHEET_Y + row * FRAME_HEIGHT,
                FRAME_WIDTH,
                FRAME_HEIGHT
        );
    }
}
