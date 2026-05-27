package io.github.siakhooi.jexl.executor;

import java.io.File;
import java.io.IOException;
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
    @Option(names = { "--jarfile", "-j" }, description = "File containing JAR paths (one per line) to load for JEXL scripts (mutually exclusive with jarListFile in a --flow-spec YAML file)")
    private File jarListFile;

    @Option(names = { "--flow-spec", "-f" }, paramLabel = "<file.yaml>", description = "YAML file with contextFile, scriptFiles, and optional resultPathTemplate, jarListFile, and exitCodeExpr (mutually exclusive with positional arguments; relative paths resolve against the YAML file's directory)")
    private File flowSpecYaml;

    @Parameters(index = "0", arity = "0..1", description = "Initial context JSON file (required unless --flow-spec/-f is set)")
    private File contextFile;

    @Parameters(index = "1..*", arity = "0..*", description = "JEXL script or JSON files to execute in sequence (required unless --flow-spec/-f is set)")
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

    @Option(names = { "--jexl-debug" }, description = "Enable Apache Commons JEXL engine debug mode for richer diagnostics when a script fails (independent of --log-level)")
    private boolean jexlDebug;

    @Option(names = { "--exit-code-expr", "-e" }, paramLabel = "<expr>", description = "Positional mode only: JEXL expression on the final merged context, or @file:<path> to load JEXL from a file (relative paths use the current working directory). Integral numeric result becomes the process exit code. Not allowed with --flow-spec/-f (use exitCodeExpr in the YAML file instead).")
    private String exitCodeExpr;

    @Override
    public Integer call() throws Exception {
        Level rootLevel;
        try {
            rootLevel = debug ? Level.DEBUG : LogLevelControl.parseLogLevel(logLevel);
        } catch (IllegalArgumentException e) {
            throw new ParameterException(spec.commandLine(), e.getMessage());
        }
        FlowFileSpec flowFileSpec;
        try {
            flowFileSpec = FlowFileSpecResolver.resolve(flowSpecYaml, contextFile, scriptFiles, resultPathTemplate,
                    exitCodeExpr);
        } catch (IllegalArgumentException e) {
            throw new ParameterException(spec.commandLine(), e.getMessage());
        } catch (IOException e) {
            throw new ParameterException(spec.commandLine(), e.getMessage(), e);
        }
        File effectiveJarListFile;
        try {
            effectiveJarListFile = JarListFileResolver.resolve(jarListFile, flowFileSpec.jarListFile());
        } catch (IllegalArgumentException e) {
            throw new ParameterException(spec.commandLine(), e.getMessage());
        }
        FlowFileSpec runSpec = new FlowFileSpec(flowFileSpec.contextFile(), flowFileSpec.scriptFiles(),
                flowFileSpec.resultPathTemplate(), effectiveJarListFile, flowFileSpec.exitCodeExpr());
        return (new JexlExecutor(runSpec, rootLevel, fullContext, jexlDebug)).execute();

    }

}
