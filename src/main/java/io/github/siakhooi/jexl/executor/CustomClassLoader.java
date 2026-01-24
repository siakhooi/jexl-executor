package io.github.siakhooi.jexl.executor;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomClassLoader {
    private static final Logger logger = LoggerFactory.getLogger(CustomClassLoader.class);

    private CustomClassLoader() {
    }

    public static ClassLoader loadJars(File jarListFile) throws IOException {
        List<String> jarPaths = Files.readAllLines(jarListFile.toPath())
                .stream()
                .map(String::trim)
                .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                .toList();

        List<URL> urls = new ArrayList<>();
        for (String jarPath : jarPaths) {
            File jarFile = new File(jarPath);
            if (!jarFile.exists()) {
                logger.error("Error: JAR file not found: {}", jarPath);
                continue;
            }
            urls.add(jarFile.toURI().toURL());
            logger.info("Loaded JAR: {}", jarPath);
        }

        return new URLClassLoader(urls.toArray(new URL[0]), Thread.currentThread().getContextClassLoader());
    }

}
