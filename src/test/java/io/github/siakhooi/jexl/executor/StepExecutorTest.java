package io.github.siakhooi.jexl.executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.github.siakhooi.jexl.executor.config.ExecutionStep;
import io.github.siakhooi.jexl.executor.config.ExecutionType;

class StepExecutorTest {

    @Test
    void executeStep_jexlType_mergesContext() throws IOException {
        // Arrange
        StepExecutor executor = new StepExecutor("{name}", getClass().getClassLoader());
        Map<String, Object> context = new HashMap<>();
        context.put("foo", "bar");

        ExecutionStep step = Mockito.mock(ExecutionStep.class);
        Mockito.when(step.name()).thenReturn("testStep");
        Mockito.when(step.scriptFile()).thenReturn(createTempScriptFile("1+1"));
        Mockito.when(step.executionType()).thenReturn(ExecutionType.JEXL);

        // Act
        StepExecutor.StepResult result = executor.executeStep(step, context);

        // Assert
        assertNotNull(result.contextMap);
        assertNotNull(result.scriptResult);
    }

    @Test
    void executeStep_jsonType_mergesContext() throws IOException {
        StepExecutor executor = new StepExecutor("{name}", getClass().getClassLoader());
        Map<String, Object> context = new HashMap<>();
        context.put("foo", "bar");

        ExecutionStep step = Mockito.mock(ExecutionStep.class);
        Mockito.when(step.name()).thenReturn("jsonStep");
        Mockito.when(step.scriptFile()).thenReturn(createTempScriptFile("{\"baz\":123}"));
        Mockito.when(step.executionType()).thenReturn(ExecutionType.JSON);

        StepExecutor.StepResult result = executor.executeStep(step, context);
        assertNotNull(result.contextMap);
        assertNotNull(result.scriptResult);
    }

    @Test
    void executeStep_unknownType_returnsOriginalContext() throws IOException {
        StepExecutor executor = new StepExecutor("{name}", getClass().getClassLoader());
        Map<String, Object> context = new HashMap<>();
        context.put("foo", "bar");

        ExecutionStep step = Mockito.mock(ExecutionStep.class);
        Mockito.when(step.name()).thenReturn("unknownStep");
        Mockito.when(step.scriptFile()).thenReturn(createTempScriptFile("irrelevant"));
        // Use a custom enum value that is not JEXL or JSON
        Mockito.when(step.executionType()).thenReturn(ExecutionType.valueOf("UNKNOWN"));

        StepExecutor.StepResult result = executor.executeStep(step, context);
        assertEquals(context, result.contextMap);
        assertNull(result.scriptResult);
    }

    // Helper to create a temp script file
    private File createTempScriptFile(String content) throws IOException {
        File temp = File.createTempFile("script", ".jexl");
        java.nio.file.Files.writeString(temp.toPath(), content);
        temp.deleteOnExit();
        return temp;
    }
}
