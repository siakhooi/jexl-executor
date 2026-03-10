package io.github.siakhooi.jexl.executor;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.siakhooi.jexl.executor.config.ExecutionStep;

public class StepExecutor {
    private static final Logger logger = LoggerFactory.getLogger(StepExecutor.class);
    private final JexlScriptExecutor jexlScriptExecutor;
    private final String resultPathTemplate;

    public StepExecutor(String resultPathTemplate, ClassLoader classLoader) {
        this.resultPathTemplate = resultPathTemplate;
        this.jexlScriptExecutor = new JexlScriptExecutor(classLoader);
    }

    public StepResult executeStep(ExecutionStep step, Map<String, Object> contextMap) throws IOException {
        logger.debug("Executing step: {}", step.name());
        String jexlScript = InputFile.readFile(step.scriptFile());
        logger.debug("jexlScript: {}", jexlScript);
        Object scriptResult;
        switch (step.executionType()) {
            case JEXL:
                scriptResult = executeJexlStep(contextMap, jexlScript);
                break;
            case JSON:
                scriptResult = executeJsonStep(jexlScript);
                break;
            default:
                return handleUnknownStep(step, contextMap);
        }
        String[] pathParts = ResultPath.get(step.name(), resultPathTemplate);
        Map<String, Object> newContextMap = ContextMapMerger.merge(contextMap, scriptResult, pathParts);
        if(logger.isDebugEnabled()) {
            logger.debug("Merged context: {}", JsonConverter.toJsonString(newContextMap));
        }
        return new StepResult(newContextMap, scriptResult);
    }

    private Object executeJexlStep(Map<String, Object> contextMap, String jexlScript) {
        Object scriptResult = jexlScriptExecutor.execute(contextMap, jexlScript);
        if(logger.isDebugEnabled()) {
            logger.debug("scriptResult: {}", JsonConverter.toJsonString(scriptResult));
        }
        return scriptResult;
    }

    private Object executeJsonStep(String jexlScript) throws IOException {
        Object scriptResult = JsonConverter.parseJson(jexlScript);
        if(logger.isDebugEnabled()) {
            logger.debug("scriptResult: {}", JsonConverter.toJsonString(scriptResult));
        }
        return scriptResult;
    }

    private StepResult handleUnknownStep(ExecutionStep step, Map<String, Object> contextMap) {
        logger.warn("Unknown execution type for step '{}', skipping execution", step.name());
        return new StepResult(contextMap, null);
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
