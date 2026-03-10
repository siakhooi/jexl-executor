package io.github.siakhooi.jexl.executor;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.siakhooi.jexl.executor.config.ExecutionStep;
import io.github.siakhooi.jexl.executor.config.ExecutionType;

public class StepExecutor {
    private static final Logger logger = LoggerFactory.getLogger(StepExecutor.class);
    private final JexlScriptExecutor jexlScriptExecutor = new JexlScriptExecutor();
    private final String resultPathTemplate;
    private final boolean debug;
    private final ClassLoader classLoader;

    public StepExecutor(String resultPathTemplate, boolean debug, ClassLoader classLoader) {
        this.resultPathTemplate = resultPathTemplate;
        this.debug = debug;
        this.classLoader = classLoader;
    }

    public StepResult executeStep(ExecutionStep step, Map<String, Object> contextMap) throws Exception {
        logger.debug("Executing step: {}", step.name());
        String jexlScript = InputFile.readFile(step.scriptFile());
        logger.debug("jexlScript: {}", jexlScript);
        Object scriptResult;
        if (step.executionType() == ExecutionType.JEXL) {
            scriptResult = jexlScriptExecutor.execute(contextMap, jexlScript, classLoader);
            if (debug) {
                logger.debug("scriptResult: {}", JsonConverter.toJsonString(scriptResult));
            }
        } else if (step.executionType() == ExecutionType.JSON) {
            scriptResult = JsonConverter.parseJson(jexlScript);
            if (debug) {
                logger.debug("scriptResult: {}", JsonConverter.toJsonString(scriptResult));
            }
        } else {
            logger.warn("Unknown execution type for step '{}', skipping execution", step.name());
            return new StepResult(contextMap, null);
        }
        String[] pathParts = ResultPath.get(step.name(), resultPathTemplate);
        Map<String, Object> newContextMap = ContextMapMerger.merge(contextMap, scriptResult, pathParts);
        if (debug) {
            logger.debug("result context: {}", JsonConverter.toJsonString(newContextMap));
        }
        return new StepResult(newContextMap, scriptResult);
    }

    public static class StepResult {
        public final Map<String, Object> contextMap;
        public final Object scriptResult;
        public StepResult(Map<String, Object> contextMap, Object scriptResult) {
            this.contextMap = contextMap;
            this.scriptResult = scriptResult;
        }
    }
}
