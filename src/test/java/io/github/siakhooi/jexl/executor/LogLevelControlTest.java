package io.github.siakhooi.jexl.executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

class LogLevelControlTest {

    @AfterEach
    void resetLogLevel() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger rootLogger = loggerContext.getLogger("ROOT");
        rootLogger.setLevel(Level.INFO);
    }

    @Test
    void setRootLogLevel_setsTrace() {
        LogLevelControl.setRootLogLevel(Level.TRACE);
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = loggerContext.getLogger("ROOT");
        assertEquals(Level.TRACE, rootLogger.getLevel());
    }

    @Test
    void setRootLogLevel_setsInfo() {
        LogLevelControl.setRootLogLevel(Level.INFO);
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = loggerContext.getLogger("ROOT");
        assertEquals(Level.INFO, rootLogger.getLevel());
    }

    @Test
    void parseLogLevel_acceptsMixedCase() {
        assertEquals(Level.WARN, LogLevelControl.parseLogLevel("warn"));
        assertEquals(Level.DEBUG, LogLevelControl.parseLogLevel("DEBUG"));
    }

    @Test
    void parseLogLevel_blankDefaultsToInfo() {
        assertEquals(Level.INFO, LogLevelControl.parseLogLevel("  "));
        assertEquals(Level.INFO, LogLevelControl.parseLogLevel(null));
    }

    @Test
    void parseLogLevel_rejectsUnknown() {
        assertThrows(IllegalArgumentException.class, () -> LogLevelControl.parseLogLevel("verbose"));
    }

    @Test
    void parseLogLevel_allAliasMapsToTrace() {
        assertEquals(Level.TRACE, LogLevelControl.parseLogLevel("all"));
    }
}
