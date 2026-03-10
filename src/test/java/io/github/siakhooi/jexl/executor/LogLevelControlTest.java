package io.github.siakhooi.jexl.executor;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void testSetRootLogLevelDebugTrue() {
        LogLevelControl.setRootLogLevelDebug(true);
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = loggerContext.getLogger("ROOT");
        assertEquals(Level.DEBUG, rootLogger.getLevel());
    }

    @Test
    void testSetRootLogLevelDebugFalse() {
        LogLevelControl.setRootLogLevelDebug(false);
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = loggerContext.getLogger("ROOT");
        assertEquals(Level.INFO, rootLogger.getLevel());
    }
}
