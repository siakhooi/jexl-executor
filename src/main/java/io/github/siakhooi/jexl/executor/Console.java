package io.github.siakhooi.jexl.executor;

public class Console {
    private Console() {
    }

    @SuppressWarnings("java:S106")
    static void printf(String format, Object... args) {
        System.out.printf(format, args);
    }

}
