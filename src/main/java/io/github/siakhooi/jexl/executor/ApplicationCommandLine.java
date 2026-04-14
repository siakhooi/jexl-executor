package io.github.siakhooi.jexl.executor;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

import ch.qos.logback.classic.Level;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;

@Command(name = "jexl-executor", mixinStandardHelpOptions = true, version = Version.APPLICATION_VERSION, description = "Execute JEXL scripts with JSON context in a chain")
public class ApplicationCommandLine implements Callable<Integer> {

    @Spec
    CommandSpec spec;

        // Input files group
    @Option(names = { "--jarfile", "-j" }, description = "File containing JAR paths (one per line) to load for JEXL scripts")
    private File jarListFile;

    @Parameters(index = "0", description = "Initial context JSON file")
    private File contextFile;

    @Parameters(index = "1..*", arity = "1..*", description = "JEXL script or JSON files to execute in sequence")
    private List<File> scriptFiles;

    // Execution options group
    @Option(names = { "--result-path", "-r" }, defaultValue = "{name}", description = "Path template for results. Use {name} as placeholder for script basename (default: ${DEFAULT-VALUE}). Examples: {name}, output.{name}, results.{name}.data")
    private String resultPathTemplate;

    @Option(names = { "--log-level" }, paramLabel = "<level>", defaultValue = "info", description = "Root log level: trace, debug, info, warn, error, off, all (=trace) (default: ${DEFAULT-VALUE})")
    private String logLevel;

    @Option(names = { "--debug" }, description = "Shorthand for --log-level debug")
    private boolean debug;

    @Option(names = { "--full", "-F" }, description = "Print full context instead of result")
    private boolean fullContext;

    @Override
    public Integer call() throws Exception {
        Level rootLevel;
        try {
            rootLevel = debug ? Level.DEBUG : LogLevelControl.parseLogLevel(logLevel);
        } catch (IllegalArgumentException e) {
            throw new ParameterException(spec.commandLine(), e.getMessage());
        }
        return (new JexlExecutor(jarListFile, contextFile, scriptFiles, resultPathTemplate, rootLevel, fullContext))
                .execute();

    }

}
