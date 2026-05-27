package io.github.siakhooi.jexl.executor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ch.qos.logback.classic.Level;

class JexlExecutorTest {

    @Test
    void execute_returnsZeroOnSuccess(@TempDir Path tempDir) throws IOException {
        // Arrange
        Path contextPath = tempDir.resolve("context.json");
        Path scriptPath = tempDir.resolve("script.jexl");
        String resultPathTemplate = "{name}";
        Level rootLogLevel = Level.INFO;
        boolean fullContext = false;
        boolean jexlDebug = false;

        Files.createFile(contextPath);
        Files.createFile(scriptPath);
        Files.writeString(contextPath, "{}\n");

        FlowFileSpec flowFileSpec = new FlowFileSpec(
                contextPath.toFile(), Collections.singletonList(scriptPath.toFile()), resultPathTemplate, null, null);
        JexlExecutor executor = new JexlExecutor(flowFileSpec, rootLogLevel, fullContext, jexlDebug);

        // Act
        int result = executor.execute();

        // Assert
        assertEquals(0, result, "Should return 0 on success");
    }

    @Test
    void execute_returnsOneOnException() {
        // Arrange: Use invalid files to trigger exception
        File contextFile = new File("/invalid/path/context.json");
        File scriptFile = new File("/invalid/path/script.jexl");
        String resultPathTemplate = "{name}";
        Level rootLogLevel = Level.INFO;
        boolean fullContext = false;
        boolean jexlDebug = false;

        FlowFileSpec flowFileSpec = new FlowFileSpec(contextFile, Collections.singletonList(scriptFile), resultPathTemplate, null, null);
        JexlExecutor executor = new JexlExecutor(flowFileSpec, rootLogLevel, fullContext, jexlDebug);

        // Act
        int result = executor.execute();

        // Assert
        assertEquals(1, result, "Should return 1 on exception");
    }

    @Test
    void execute_returnsEvaluatedExitCode(@TempDir Path tempDir) throws IOException {
        Path contextPath = tempDir.resolve("context.json");
        Path scriptPath = tempDir.resolve("script.jexl");
        Files.writeString(contextPath, "{}\n");
        Files.writeString(scriptPath, "41\n");

        FlowFileSpec flowFileSpec = new FlowFileSpec(
                contextPath.toFile(), Collections.singletonList(scriptPath.toFile()), "{name}", null, "script");
        JexlExecutor executor = new JexlExecutor(flowFileSpec, Level.INFO, false, false);

        assertEquals(41, executor.execute());
    }

    @Test
    void execute_returnsOneWhenExitCodeExprYieldsNonNumber(@TempDir Path tempDir) throws IOException {
        Path contextPath = tempDir.resolve("context.json");
        Path scriptPath = tempDir.resolve("script.jexl");
        Files.writeString(contextPath, "{}\n");
        Files.writeString(scriptPath, "1\n");

        FlowFileSpec flowFileSpec = new FlowFileSpec(
                contextPath.toFile(), Collections.singletonList(scriptPath.toFile()), "{name}", null, "'nope'");
        JexlExecutor executor = new JexlExecutor(flowFileSpec, Level.INFO, false, false);

        assertEquals(1, executor.execute());
    }
}
