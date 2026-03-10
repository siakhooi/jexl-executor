package io.github.siakhooi.jexl.executor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

class LogLevelUtilTest {
    @Test
    void testSetRootLogLevelDebugTrue() {
        LogLevelUtil.setRootLogLevelDebug(true);
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = loggerContext.getLogger("ROOT");
        assertEquals(Level.DEBUG, rootLogger.getLevel());
    }

    @Test
    void testSetRootLogLevelDebugFalse() {
        LogLevelUtil.setRootLogLevelDebug(false);
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = loggerContext.getLogger("ROOT");
        assertEquals(Level.INFO, rootLogger.getLevel());
    }
}
