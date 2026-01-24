package io.github.siakhooi.jexl.executor;

import java.nio.file.Paths;

public class ResultPath {
    private ResultPath() {
    }

    static String[] get(String scriptFilePath, String resultPathTemplate) {
        String basename = Paths.get(scriptFilePath).getFileName().toString();
        int dotIndex = basename.lastIndexOf('.');
        if (dotIndex > 0) {
            basename = basename.substring(0, dotIndex);
        }

        String resultPath = resultPathTemplate.replace("{name}", basename);
        return resultPath.split("\\.");
    }

}
