package io.github.siakhooi.jexl.executor;

import picocli.CommandLine;

public class Application {

    public static int run(String[] args) {
        CommandLine cmd = new CommandLine(new ApplicationCommandLine());
        cmd.setUsageHelpWidth(120);
        if (args.length == 0) {
            cmd.usage(System.out);
            return 0;
        }
        return cmd.execute(args);
    }

    public static void main(String[] args) {
        int exitCode = run(args);
        if (args.length > 0) {
            System.exit(exitCode);
        }
    }
}
