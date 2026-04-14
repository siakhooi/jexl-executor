package io.github.siakhooi.jexl.executor;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.siakhooi.jexl.executor.config.ExecutionStep;
import io.github.siakhooi.jexl.executor.config.FlowPath;

public class FlowExecutor {
    private static final Logger logger = LoggerFactory.getLogger(FlowExecutor.class);
    public static class Result {
        public final Map<String, Object> contextMap;
        public final Object scriptResult;
        public Result(Map<String, Object> contextMap, Object scriptResult) {
            this.contextMap = contextMap;
            this.scriptResult = scriptResult;
        }
    }

    public Result execute(FlowPath flowPath, StepExecutor stepExecutor, Map<String, Object> contextMap) throws IOException {
        var steps = flowPath.getSteps();
        logger.debug("Running {} flow step(s)", steps.size());
        Object scriptResult = null;
        int index = 0;
        for (ExecutionStep step : steps) {
            index++;
            logger.debug("Flow step {}/{}: '{}' ({})", index, steps.size(), step.name(), step.executionType());
            StepExecutor.StepResult stepResult = stepExecutor.executeStep(step, contextMap);
            contextMap = stepResult.contextMap;
            scriptResult = stepResult.scriptResult;
        }
        return new Result(contextMap, scriptResult);
    }
}
