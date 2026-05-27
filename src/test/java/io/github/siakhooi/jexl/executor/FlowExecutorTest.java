package io.github.siakhooi.jexl.executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.siakhooi.jexl.executor.config.ExecutionStep;
import io.github.siakhooi.jexl.executor.config.FlowPath;

@ExtendWith(MockitoExtension.class)
class FlowExecutorTest {

    @Mock
    private FlowPath flowPath;

    @Mock
    private StepExecutor stepExecutor;

    @Mock
    private ExecutionStep step1;

    @Mock
    private ExecutionStep step2;

    @Test
    void testExecuteFlowSuccess() throws IOException {
        when(flowPath.getSteps()).thenReturn(List.of(step1, step2));

        StepExecutor.StepResult result1 = new StepExecutor.StepResult(Map.of("a", 1), "result1");
        StepExecutor.StepResult result2 = new StepExecutor.StepResult(Map.of("b", 2), "result2");
        when(stepExecutor.executeStep(step1, Map.of())).thenAnswer(invocation -> result1);
        when(stepExecutor.executeStep(step2, result1.contextMap)).thenAnswer(invocation -> result2);

        FlowExecutor flowExecutor = new FlowExecutor();
        FlowExecutor.Result flowResult = flowExecutor.execute(flowPath, stepExecutor, Map.of());

        assertNotNull(flowResult);
        assertEquals(Map.of("b", 2), flowResult.contextMap);
        assertEquals("result2", flowResult.scriptResult);
    }

    @Test
    void testExecuteFlowEmptySteps() throws IOException {
        when(flowPath.getSteps()).thenReturn(List.of());

        FlowExecutor flowExecutor = new FlowExecutor();
        FlowExecutor.Result flowResult = flowExecutor.execute(flowPath, stepExecutor, Map.of());

        assertNotNull(flowResult);
        assertEquals(Map.of(), flowResult.contextMap);
        assertNull(flowResult.scriptResult);
    }
}
