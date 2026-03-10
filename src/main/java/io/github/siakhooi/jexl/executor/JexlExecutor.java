package io.github.siakhooi.jexl.executor;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.siakhooi.jexl.executor.config.FlowPath;

public class JexlExecutor {
    private static final Logger logger = LoggerFactory.getLogger(JexlExecutor.class);

    private final File jarListFile;
    private final File contextFile;
    private final List<File> scriptFiles;
    private final String resultPathTemplate;
    private final boolean debug;
    private final boolean fullContext;

    public JexlExecutor(File jarListFile, File contextFile, List<File> scriptFiles, String resultPathTemplate,
            boolean debug, boolean fullContext) {
        this.jarListFile = jarListFile;
        this.contextFile = contextFile;
        this.scriptFiles = scriptFiles;
        this.resultPathTemplate = resultPathTemplate;
        this.debug = debug;
        this.fullContext = fullContext;
    }

    public int execute() {
        LogLevelUtil.setRootLogLevelDebug(debug);
        try {
            ClassLoader classLoader = ApplicationClassLoader.get(jarListFile);
            Map<String, Object> initialContextMap = ContextFileLoader.get(contextFile);

            FlowPath flowPath = FilesToFlowPath.generate(scriptFiles);
            StepExecutor stepExecutor = new StepExecutor(resultPathTemplate, classLoader);

            FlowExecutor flowExecutor = new FlowExecutor();
            FlowExecutor.Result flowResult = flowExecutor.execute(flowPath, stepExecutor, initialContextMap);
            Output.print(fullContext, flowResult.contextMap, flowResult.scriptResult);
            return 0;
        } catch (Exception e) {
            logger.error("Error: {}", e.getMessage(), e);
            return 1;
        }
    }

}
