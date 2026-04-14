package io.github.siakhooi.jexl.executor;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

public class LogLevelControl {
    private LogLevelControl() {
    }

    /**
     * Parses a log level name (case-insensitive). Accepts standard Logback levels:
     * TRACE, DEBUG, INFO, WARN, ERROR, OFF. The alias {@code all} selects TRACE (finest).
     *
     * @throws IllegalArgumentException if the name is not a known level
     */
    static Level parseLogLevel(String name) {
        if (name == null || name.isBlank()) {
            return Level.INFO;
        }
        return switch (name.trim().toUpperCase()) {
            case "TRACE" -> Level.TRACE;
            case "DEBUG" -> Level.DEBUG;
            case "INFO" -> Level.INFO;
            case "WARN" -> Level.WARN;
            case "ERROR" -> Level.ERROR;
            case "OFF" -> Level.OFF;
            case "ALL" -> Level.TRACE;
            default -> throw new IllegalArgumentException(
                    "Unknown log level '" + name.trim() + "'. Use: trace, debug, info, warn, error, off, all (trace).");
        };
    }

    static void setRootLogLevel(Level level) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger rootLogger = loggerContext.getLogger("ROOT");
        rootLogger.setLevel(level);
    }
}
