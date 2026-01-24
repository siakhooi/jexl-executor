package io.github.siakhooi.jexl.executor;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CustomClassLoader {
    private CustomClassLoader() {
    }

    public static ClassLoader loadJars(File jarListFile) throws IOException {
        List<String> jarPaths = Files.readAllLines(jarListFile.toPath())
                .stream()
                .map(String::trim)
                .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                .collect(Collectors.toList());

        List<URL> urls = new ArrayList<>();
        for (String jarPath : jarPaths) {
            File jarFile = new File(jarPath);
            if (!jarFile.exists()) {
                System.err.println("Warning: JAR file not found: " + jarPath);
                continue;
            }
            urls.add(jarFile.toURI().toURL());
            System.out.println("Loaded JAR: " + jarPath);
        }

        return new URLClassLoader(urls.toArray(new URL[0]), Thread.currentThread().getContextClassLoader());
    }

}
