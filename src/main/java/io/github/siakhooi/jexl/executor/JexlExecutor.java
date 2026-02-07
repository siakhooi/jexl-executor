package io.github.siakhooi.jexl.executor;

import static io.github.siakhooi.jexl.executor.FileUtils.readFile;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.MapContext;
import org.apache.commons.jexl3.introspection.JexlPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.siakhooi.jexl.executor.config.ExecutionPlan;
import io.github.siakhooi.jexl.executor.config.ExecutionStep;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "jexl-executor", mixinStandardHelpOptions = true, version = Version.APPLICATION_VERSION, description = "Execute JEXL scripts with JSON context in a chain")
public class JexlExecutor implements Callable<Integer> {
    private static final Logger logger = LoggerFactory.getLogger(JexlExecutor.class);

    @Option(names = { "--result-path",
            "-r" }, defaultValue = "{name}", description = "Path template for results. Use {name} as placeholder for script basename (default: ${DEFAULT-VALUE}). Examples: {name}, output.{name}, results.{name}.data")
    private String resultPathTemplate;

    @Option(names = { "--jarfile",
            "-j" }, description = "File containing JAR paths (one per line) to load for JEXL scripts")
    private File jarListFile;

    @Parameters(index = "0", description = "Initial context JSON file")
    private File contextFile;

    @Parameters(index = "1..*", arity = "1..*", description = "JEXL script files to execute in sequence")
    private List<File> scriptFiles;

    @Option(names = { "--debug" }, description = "Enable debug mode")
    private boolean debug;

    @Override
    public Integer call() throws Exception {
        LogLevelUtil.setRootLogLevelDebug(debug);
        try {
            ClassLoader classLoader = ApplicationClassLoader.get(jarListFile);

            Map<String, Object> contextMap = ContextFileLoader.get(contextFile);

            Object scriptResult = new HashMap<String, Object>();

            ExecutionPlan executionPlan = ExecutionPlanUtil.loadExecutionPlan(scriptFiles);

            for(ExecutionStep step : executionPlan.getSteps()) {
                logger.debug("Executing step: {}", step.name());
                String jexlScript = readFile(step.scriptFile());

                logger.debug("jexlScript: {}", jexlScript);

                scriptResult = executeJexl(contextMap, jexlScript, classLoader);
                logger.debug("scriptResult: {}", scriptResult);

                String[] pathParts = ResultPath.get(step.name(), resultPathTemplate);
                contextMap = ContextMapMerger.merge(contextMap, scriptResult, pathParts);
                logger.debug("result contextMap: {}", contextMap);
            }

            Output.print(scriptResult);

            return 0;
        } catch (Exception e) {
            logger.error("Error: {}", e.getMessage(), e);
            return 1;
        }
    }

    private static Object executeJexl(Map<String, Object> contextMap, String jexlScript, ClassLoader classLoader) {
        JexlPermissions permissions = JexlPermissions.UNRESTRICTED;
        JexlEngine jexl = new JexlBuilder().loader(classLoader).permissions(permissions).create();
        JexlContext context = new MapContext(contextMap);

        var script = jexl.createScript(jexlScript);
        return script.execute(context);
    }
}
