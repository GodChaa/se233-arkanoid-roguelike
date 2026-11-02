package se233.arkanoid.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class GameLoggerTest {
    @TempDir
    Path tempDir;

    @Test
    void infoActionAndScoreWriteMessagesToConfiguredLog() throws Exception {
        Path logFile = tempDir.resolve("events.log");
        GameLogger logger = new GameLogger(logFile);

        logger.info("game started");
        logger.action("paddle moved");
        logger.score(120);

        String contents = Files.readString(logFile);
        assertTrue(contents.contains("game started"));
        assertTrue(contents.contains("paddle moved"));
        assertTrue(contents.contains("Score: 120"));
    }

    @Test
    void warningAndErrorWriteMessagesAndExceptionDetails() throws Exception {
        Path logFile = tempDir.resolve("errors.log");
        GameLogger logger = new GameLogger(logFile);
        RuntimeException cause = new RuntimeException("broken simulation");

        logger.warning("life lost");
        logger.error("game stopped", cause);

        String contents = Files.readString(logFile);
        assertTrue(contents.contains("life lost"));
        assertTrue(contents.contains("game stopped"));
        assertTrue(contents.contains("broken simulation"));
    }

    @Test
    void pathConstructorCreatesMissingParentDirectories() throws Exception {
        Path nestedLog = tempDir.resolve("nested/logs/game.log");
        new GameLogger(nestedLog).info("nested");
        assertTrue(Files.exists(nestedLog));
    }

    @Test
    void pathConstructorHandlesAnUnusableLogDestination() {
        Path directoryAsFile = tempDir.resolve("directory");
        assertDoesNotThrow(() -> new GameLogger(directoryAsFile));
    }
}
