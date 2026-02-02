package io.github.siakhooi.jexl.executor;


import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

public class LogLevelUtil {
    public static void setRootLogLevelDebug(boolean debug) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger rootLogger = loggerContext.getLogger("ROOT");
        rootLogger.setLevel(debug ? Level.DEBUG : Level.INFO);
    }
}
