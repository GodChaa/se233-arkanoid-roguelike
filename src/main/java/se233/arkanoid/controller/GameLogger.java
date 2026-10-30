package se233.arkanoid.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.UUID;

public final class GameLogger implements GameEventLogger {
    private final Logger logger;

    public GameLogger() {
        this(Path.of("logs", "game.log"));
    }

    public GameLogger(Path logFile) {
        logger = Logger.getLogger("se233.arkanoid." + UUID.randomUUID());
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.ALL);
        try {
            Path absolute = logFile.toAbsolutePath();
            Files.createDirectories(absolute.getParent());
            FileHandler handler = new FileHandler(absolute.toString(), true);
            handler.setFormatter(new SimpleFormatter());
            handler.setLevel(Level.ALL);
            logger.addHandler(handler);
        } catch (IOException | SecurityException exception) {
            logger.setUseParentHandlers(true);
            logger.log(Level.WARNING, "Could not open game log file: " + logFile, exception);
        }
    }

    @Override public void info(String message) { logger.info(message); }
    @Override public void action(String message) { logger.fine(message); }
    @Override public void score(int score) { logger.info("Score: " + score); }
    @Override public void warning(String message) { logger.warning(message); }
    @Override public void error(String message, Throwable cause) { logger.log(Level.SEVERE, message, cause); }
}
