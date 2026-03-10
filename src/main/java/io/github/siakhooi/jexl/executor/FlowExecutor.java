package io.github.siakhooi.jexl.executor;

import java.io.IOException;
import java.util.Map;

import io.github.siakhooi.jexl.executor.config.ExecutionStep;
import io.github.siakhooi.jexl.executor.config.FlowPath;

public class FlowExecutor {
    public static class Result {
        public final Map<String, Object> contextMap;
        public final Object scriptResult;
        public Result(Map<String, Object> contextMap, Object scriptResult) {
            this.contextMap = contextMap;
            this.scriptResult = scriptResult;
        }
    }

    public Result execute(FlowPath flowPath, StepExecutor stepExecutor, Map<String, Object> contextMap) throws IOException {
        Object scriptResult = null;
        for (ExecutionStep step : flowPath.getSteps()) {
            StepExecutor.StepResult stepResult = stepExecutor.executeStep(step, contextMap);
            contextMap = stepResult.contextMap;
            scriptResult = stepResult.scriptResult;
        }
        return new Result(contextMap, scriptResult);
    }
}
