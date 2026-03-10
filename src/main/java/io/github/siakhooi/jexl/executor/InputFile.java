package io.github.siakhooi.jexl.executor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class InputFile {
    private InputFile() {
    }

    static String readFile(File file) throws IOException {
        return Files.readString(file.toPath());
    }
}
