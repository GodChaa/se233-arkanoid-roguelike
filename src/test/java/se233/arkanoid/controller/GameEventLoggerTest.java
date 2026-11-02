package se233.arkanoid.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class GameEventLoggerTest {
    @TempDir
    Path tempDir;

    @Test
    void loggerContractCanBeImplementedAndReceivesAllEventKinds() throws Exception {
        GameEventLogger logger = new GameLogger(tempDir.resolve("contract.log"));
        logger.info("info event");
        logger.action("action event");
        logger.score(45);
        logger.warning("warning event");
        logger.error("error event", new IllegalStateException("cause"));

        String text = Files.readString(tempDir.resolve("contract.log"));
        assertTrue(text.contains("info event"));
        assertTrue(text.contains("action event"));
        assertTrue(text.contains("Score: 45"));
        assertTrue(text.contains("warning event"));
        assertTrue(text.contains("cause"));
    }

    @Test
    void eventLoggerContractSupportsIndependentImplementations() {
        int[] calls = {0};
        GameEventLogger logger = new GameEventLogger() {
            @Override public void info(String message) { calls[0]++; }
            @Override public void action(String message) { calls[0]++; }
            @Override public void score(int score) { calls[0]++; }
            @Override public void warning(String message) { calls[0]++; }
            @Override public void error(String message, Throwable cause) { calls[0]++; }
        };
        logger.info("i");
        logger.action("a");
        logger.score(1);
        logger.warning("w");
        logger.error("e", null);
        assertEquals(5, calls[0]);
    }
}
