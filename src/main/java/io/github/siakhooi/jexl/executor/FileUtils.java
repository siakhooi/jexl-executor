package io.github.siakhooi.jexl.executor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileUtils {
    private FileUtils() {
    }

    public static String readFile(File file) throws IOException {
        return Files.readString(file.toPath());
    }
}
