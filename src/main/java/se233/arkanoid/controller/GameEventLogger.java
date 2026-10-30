package se233.arkanoid.controller;

public interface GameEventLogger {
    void info(String message);
    void action(String message);
    void score(int score);
    void warning(String message);
    void error(String message, Throwable cause);
}
