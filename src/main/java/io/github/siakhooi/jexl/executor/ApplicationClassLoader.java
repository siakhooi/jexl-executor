package io.github.siakhooi.jexl.executor;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationClassLoader {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationClassLoader.class);

    private ApplicationClassLoader() {
    }

    static ClassLoader get(File jarListFile) throws IOException {
        if (jarListFile != null) {
            logger.debug("Building classloader from JAR list '{}'", jarListFile.getAbsolutePath());
            ClassLoader classLoader = CustomClassLoader.loadJars(jarListFile);
            Thread.currentThread().setContextClassLoader(classLoader);
            return classLoader;
        }
        logger.debug("No JAR list file; using current thread context classloader");
        return Thread.currentThread().getContextClassLoader();

    }

}
