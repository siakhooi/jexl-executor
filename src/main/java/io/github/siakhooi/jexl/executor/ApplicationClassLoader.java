package io.github.siakhooi.jexl.executor;

import java.io.File;
import java.io.IOException;

public class ApplicationClassLoader {
    private ApplicationClassLoader() {
    }

    public static ClassLoader get(File jarListFile) throws IOException {
        if (jarListFile != null) {
            ClassLoader classLoader = CustomClassLoader.loadJars(jarListFile);
            Thread.currentThread().setContextClassLoader(classLoader);
            return classLoader;
        } else {
            return Thread.currentThread().getContextClassLoader();
        }

    }

}
