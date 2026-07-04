package io.github.siakhooi.jexl.executor;

import java.io.PrintStream;

public class Console {
    private Console() {
    }

    @SuppressWarnings("java:S106")
    static PrintStream out() {
        return System.out;
    }

    @SuppressWarnings("java:S106")
    static void printf(String format, Object... args) {
        System.out.printf(format, args);
    }

}
