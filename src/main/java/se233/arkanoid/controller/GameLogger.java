package se233.arkanoid.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class GameLogger {
    private final Logger logger;

    public GameLogger() {
        this(Path.of("logs", "game.log"));
    }

    public GameLogger(Path logFile) {
        String loggerName = "se233.arkanoid.game." + UUID.randomUUID();
        Logger resolvedLogger;
        try {
            Path absolute = logFile.toAbsolutePath();
            Files.createDirectories(absolute.getParent());
            LoggerContext context = (LoggerContext) LogManager.getContext(false);
            Configuration configuration = context.getConfiguration();
            String appenderName = loggerName + ".file";
            PatternLayout layout = PatternLayout.newBuilder()
                    .withConfiguration(configuration)
                    .withPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level %msg%n%throwable{full}")
                    .build();
            FileAppender appender = FileAppender.newBuilder()
                    .setName(appenderName)
                    .withFileName(absolute.toString())
                    .withAppend(true)
                    .setLayout(layout)
                    .setConfiguration(configuration)
                    .build();
            appender.start();

            LoggerConfig loggerConfig = new LoggerConfig(loggerName, Level.ALL, false);
            loggerConfig.addAppender(appender, null, null);
            configuration.addAppender(appender);
            configuration.addLogger(loggerName, loggerConfig);
            context.updateLoggers();
            resolvedLogger = context.getLogger(loggerName);
        } catch (IOException | SecurityException exception) {
            resolvedLogger = LogManager.getLogger(GameLogger.class);
            resolvedLogger.warn("Could not open game log file: {}", logFile, exception);
        }
        logger = resolvedLogger;
    }

    GameLogger(Logger logger) {
        this.logger = logger;
    }

    public void info(String message) { logger.info(message); }
    public void action(String message) { logger.debug(message); }
    public void score(int score) { logger.info("Score: {}", score); }
    public void warning(String message) { logger.warn(message); }
    public void error(String message, Throwable cause) { logger.error(message, cause); }
}
