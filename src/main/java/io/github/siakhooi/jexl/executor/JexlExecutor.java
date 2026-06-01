package io.github.siakhooi.jexl.executor;

import java.io.File;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.introspection.JexlPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import io.github.siakhooi.jexl.executor.config.FlowPath;

public class JexlExecutor {
    private static final Logger logger = LoggerFactory.getLogger(JexlExecutor.class);

    private final FlowFileSpec flowFileSpec;
    private final Level rootLogLevel;
    private final boolean fullContext;
    private final boolean jexlDebug;

    public JexlExecutor(FlowFileSpec flowFileSpec, Level rootLogLevel, boolean fullContext, boolean jexlDebug) {
        this.flowFileSpec = flowFileSpec;
        this.rootLogLevel = rootLogLevel;
        this.fullContext = fullContext;
        this.jexlDebug = jexlDebug;
    }

    public int execute() {
        LogLevelControl.setRootLogLevel(rootLogLevel);
        JexlBuilder.setDefaultPermissions(JexlPermissions.UNRESTRICTED);

        try {
            logger.debug("Starting execution with context file '{}' and {} script step(s)",
                    flowFileSpec.contextFile().getAbsolutePath(), flowFileSpec.scriptFiles().size());
            logger.debug("Result path template: {}", flowFileSpec.resultPathTemplate());
            if (flowFileSpec.jarListFile() != null) {
                logger.debug("JAR list file: {}", flowFileSpec.jarListFile().getAbsolutePath());
            }
            if (jexlDebug) {
                logger.debug("JEXL script engine debug enabled (--jexl-debug)");
            }

            ClassLoader classLoader = ApplicationClassLoader.get(flowFileSpec.jarListFile());
            Map<String, Object> initialContextMap = ContextFileLoader.get(flowFileSpec.contextFile());

            FlowPath flowPath = FilesToFlowPath.generate(flowFileSpec.scriptFiles());
            if(logger.isDebugEnabled()) {
                logger.debug("Script files in order: {}",
                        flowFileSpec.scriptFiles().stream().map(File::getName).collect(Collectors.joining(", ")));
            }

            StepExecutor stepExecutor = new StepExecutor(flowFileSpec.resultPathTemplate(), classLoader, jexlDebug);

            FlowExecutor flowExecutor = new FlowExecutor();
            FlowExecutor.Result flowResult = flowExecutor.execute(flowPath, stepExecutor, initialContextMap);
            Output.print(fullContext, flowResult.contextMap, flowResult.scriptResult);
            String exitExpr = flowFileSpec.exitCodeExpr();
            if (exitExpr == null || exitExpr.isBlank()) {
                logger.debug("Execution finished successfully");
                return 0;
            }
            JexlScriptExecutor exitCodeJexl = new JexlScriptExecutor(classLoader, jexlDebug);
            Object exitValue = exitCodeJexl.execute(flowResult.contextMap, exitExpr, "--exit-code-expr");
            int exitCode = ExitCodeConverter.toProcessExitCode(exitValue);
            logger.debug("Exit code expression evaluated to {}", exitCode);
            return exitCode;
        } catch (Exception e) {
            logger.error("Execution failed: {}", e.getMessage(), e);
            return 1;
        }
    }

}
