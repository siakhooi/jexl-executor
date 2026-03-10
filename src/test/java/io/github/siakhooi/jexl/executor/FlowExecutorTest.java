package io.github.siakhooi.jexl.executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.github.siakhooi.jexl.executor.config.ExecutionStep;
import io.github.siakhooi.jexl.executor.config.FlowPath;

class FlowExecutorTest {
    @Test
    void testExecuteFlowSuccess() throws IOException {
        FlowPath flowPath = Mockito.mock(FlowPath.class);
        ExecutionStep step1 = Mockito.mock(ExecutionStep.class);
        ExecutionStep step2 = Mockito.mock(ExecutionStep.class);
        Mockito.when(flowPath.getSteps()).thenReturn(List.of(step1, step2));

        StepExecutor stepExecutor = Mockito.mock(StepExecutor.class);
        StepExecutor.StepResult result1 = new StepExecutor.StepResult(Map.of("a", 1), "result1");
        StepExecutor.StepResult result2 = new StepExecutor.StepResult(Map.of("b", 2), "result2");
        Mockito.when(stepExecutor.executeStep(step1, Map.of())).thenAnswer(invocation -> result1);
        Mockito.when(stepExecutor.executeStep(step2, result1.contextMap)).thenAnswer(invocation -> result2);

        FlowExecutor flowExecutor = new FlowExecutor();
        FlowExecutor.Result flowResult = flowExecutor.execute(flowPath, stepExecutor, Map.of());

        assertNotNull(flowResult);
        assertEquals(Map.of("b", 2), flowResult.contextMap);
        assertEquals("result2", flowResult.scriptResult);
    }

    @Test
    void testExecuteFlowEmptySteps() throws IOException {
        FlowPath flowPath = Mockito.mock(FlowPath.class);
        Mockito.when(flowPath.getSteps()).thenReturn(List.of());

        StepExecutor stepExecutor = Mockito.mock(StepExecutor.class);
        FlowExecutor flowExecutor = new FlowExecutor();
        FlowExecutor.Result flowResult = flowExecutor.execute(flowPath, stepExecutor, Map.of());

        assertNotNull(flowResult);
        assertEquals(Map.of(), flowResult.contextMap);
        assertNull(flowResult.scriptResult);
    }
}
