package io.github.siakhooi.jexl.executor;

import picocli.CommandLine;

public class Application {
    public static void main(String[] args) {
        CommandLine cmd = new CommandLine(new JexlExecutor());
        cmd.setUsageHelpWidth(120);
        int exitCode = cmd.execute(args);
        System.exit(exitCode);
    }
}
