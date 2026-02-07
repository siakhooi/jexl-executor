package io.github.siakhooi.jexl.executor;

public class ResultPath {
    private ResultPath() {
    }

    static String[] get(String name, String resultPathTemplate) {

        String resultPath = resultPathTemplate.replace("{name}", name);
        return resultPath.split("\\.");
    }

}
