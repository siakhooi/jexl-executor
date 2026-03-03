package io.github.siakhooi.jexl.executor.config;

public enum ExecutionType {
    JEXL,
    JSON,
    UNKNOWN;

    public static ExecutionType fromExtension(String extension) {
        switch (extension.toLowerCase()) {
            case "jexl":
                return JEXL;
            case "json":
                return JSON;
            default:
                return UNKNOWN;
        }
    }
}
