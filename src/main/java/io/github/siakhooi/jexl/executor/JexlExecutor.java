package io.github.siakhooi.jexl.executor;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import io.github.siakhooi.jexl.executor.config.FlowPath;

public class JexlExecutor {
    private static final Logger logger = LoggerFactory.getLogger(JexlExecutor.class);

    private final File jarListFile;
    private final File contextFile;
    private final List<File> scriptFiles;
    private final String resultPathTemplate;
    private final Level rootLogLevel;
    private final boolean fullContext;

    public JexlExecutor(File jarListFile, File contextFile, List<File> scriptFiles, String resultPathTemplate,
            Level rootLogLevel, boolean fullContext) {
        this.jarListFile = jarListFile;
        this.contextFile = contextFile;
        this.scriptFiles = scriptFiles;
        this.resultPathTemplate = resultPathTemplate;
        this.rootLogLevel = rootLogLevel;
        this.fullContext = fullContext;
    }

    public int execute() {
        LogLevelControl.setRootLogLevel(rootLogLevel);
        try {
            logger.debug("Starting execution with context file '{}' and {} script step(s)",
                    contextFile.getAbsolutePath(), scriptFiles.size());
            logger.debug("Result path template: {}", resultPathTemplate);
            if (jarListFile != null) {
                logger.debug("JAR list file: {}", jarListFile.getAbsolutePath());
            }

            ClassLoader classLoader = ApplicationClassLoader.get(jarListFile);
            Map<String, Object> initialContextMap = ContextFileLoader.get(contextFile);

            FlowPath flowPath = FilesToFlowPath.generate(scriptFiles);
            logger.debug("Script files in order: {}",
                    scriptFiles.stream().map(f -> f.getName()).collect(Collectors.joining(", ")));

            StepExecutor stepExecutor = new StepExecutor(resultPathTemplate, classLoader);

            FlowExecutor flowExecutor = new FlowExecutor();
            FlowExecutor.Result flowResult = flowExecutor.execute(flowPath, stepExecutor, initialContextMap);
            Output.print(fullContext, flowResult.contextMap, flowResult.scriptResult);
            logger.debug("Execution finished successfully");
            return 0;
        } catch (Exception e) {
            logger.error("Execution failed: {}", e.getMessage(), e);
            return 1;
        }
    }

}
