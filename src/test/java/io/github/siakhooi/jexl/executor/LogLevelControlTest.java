package io.github.siakhooi.jexl.executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

class LogLevelControlTest {

    private static Logger rootLogger() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        return loggerContext.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
    }

    @AfterEach
    void resetLogLevel() {
        rootLogger().setLevel(Level.INFO);
    }

    static Stream<Level> logbackStandardLevels() {
        return Stream.of(Level.TRACE, Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR, Level.OFF);
    }

    @ParameterizedTest
    @MethodSource("logbackStandardLevels")
    void setRootLogLevel_setsStandardLevels(Level level) {
        LogLevelControl.setRootLogLevel(level);
        assertEquals(level, rootLogger().getLevel());
    }

    static Stream<Arguments> parseLogLevelValid() {
        return Stream.of(
                arguments("trace", Level.TRACE),
                arguments("TRACE", Level.TRACE),
                arguments("debug", Level.DEBUG),
                arguments("DEBUG", Level.DEBUG),
                arguments("info", Level.INFO),
                arguments("INFO", Level.INFO),
                arguments("warn", Level.WARN),
                arguments("WARN", Level.WARN),
                arguments("error", Level.ERROR),
                arguments("ERROR", Level.ERROR),
                arguments("off", Level.OFF),
                arguments("OFF", Level.OFF),
                arguments("all", Level.TRACE),
                arguments("ALL", Level.TRACE),
                arguments("  warn  ", Level.WARN),
                arguments("\tDEBUG\n", Level.DEBUG));
    }

    @ParameterizedTest
    @MethodSource("parseLogLevelValid")
    void parseLogLevel_acceptsKnownNames(String input, Level expected) {
        assertEquals(expected, LogLevelControl.parseLogLevel(input));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "\t", "  \n  " })
    void parseLogLevel_blankDefaultsToInfo(String input) {
        assertEquals(Level.INFO, LogLevelControl.parseLogLevel(input));
    }

    @ParameterizedTest
    @CsvSource({ "verbose", "FATAL", "notice", "x" })
    void parseLogLevel_rejectsUnknown(String name) {
        assertThrows(IllegalArgumentException.class, () -> LogLevelControl.parseLogLevel(name));
    }

    @Test
    void parseLogLevel_unknownMessageMentionsLevelAndAllowedValues() {
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> LogLevelControl.parseLogLevel("fictional"));
        assertTrue(ex.getMessage().contains("Unknown log level"));
        assertTrue(ex.getMessage().contains("fictional"));
        assertTrue(ex.getMessage().contains("trace"));
        assertTrue(ex.getMessage().contains("off"));
    }
}
