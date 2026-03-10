package io.github.siakhooi.jexl.executor;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.siakhooi.jexl.executor.config.ExecutionStep;
import io.github.siakhooi.jexl.executor.config.FlowPath;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "jexl-executor", mixinStandardHelpOptions = true, version = Version.APPLICATION_VERSION, description = "Execute JEXL scripts with JSON context in a chain")
public class JexlExecutor implements Callable<Integer> {
    private static final Logger logger = LoggerFactory.getLogger(JexlExecutor.class);

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

    @Option(names = { "--debug" }, description = "Enable debug mode")
    private boolean debug;

    @Option(names = { "--full", "-F" }, description = "Print full context instead of result")
    private boolean fullContext;

    @Override
    public Integer call() throws Exception {
        LogLevelUtil.setRootLogLevelDebug(debug);
        try {
            ClassLoader classLoader = ApplicationClassLoader.get(jarListFile);
            Map<String, Object> contextMap = ContextFileLoader.get(contextFile);
            Object scriptResult = new HashMap<String, Object>();

            FlowPath flowPath = FilesToFlowPath.generate(scriptFiles);
            StepExecutor stepExecutor = new StepExecutor(resultPathTemplate, classLoader);

            for (ExecutionStep step : flowPath.getSteps()) {
                StepExecutor.StepResult stepResult = stepExecutor.executeStep(step, contextMap);
                contextMap = stepResult.contextMap;
                scriptResult = stepResult.scriptResult;
            }
            Output.print(fullContext, contextMap, scriptResult);
            return 0;
        } catch (Exception e) {
            logger.error("Error: {}", e.getMessage(), e);
            return 1;
        }
    }

}
