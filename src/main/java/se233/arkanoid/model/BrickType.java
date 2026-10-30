package se233.arkanoid.model;

public enum BrickType {
    NORMAL(1, 10, "N", "#38bdf8"),
    STRONG(2, 20, "S", "#fb7185"),
    BONUS(1, 50, "B", "#facc15"),
    SPECIAL(1, 30, "+", "#c084fc");

    private final int hitPoints;
    private final int scoreValue;
    private final String symbol;
    private final String colorHex;

    BrickType(int hitPoints, int scoreValue, String symbol, String colorHex) {
        this.hitPoints = hitPoints;
        this.scoreValue = scoreValue;
        this.symbol = symbol;
        this.colorHex = colorHex;
    }

    public int getHitPoints() { return hitPoints; }
    public int getScoreValue() { return scoreValue; }
    public String getSymbol() { return symbol; }
    public String getColorHex() { return colorHex; }
}
