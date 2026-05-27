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
        File jarListFile = null; // or create a temp file if needed
        Path contextPath = tempDir.resolve("context.json");
        Path scriptPath = tempDir.resolve("script.jexl");
        String resultPathTemplate = "{name}";
        Level rootLogLevel = Level.INFO;
        boolean fullContext = false;
        boolean jexlDebug = false;

        Files.createFile(contextPath);
        Files.createFile(scriptPath);
        Files.writeString(contextPath, "{}\n");

        JexlExecutor executor = new JexlExecutor(
                jarListFile,
                contextPath.toFile(),
                Collections.singletonList(scriptPath.toFile()),
                resultPathTemplate,
                rootLogLevel,
                fullContext,
                jexlDebug);

        // Act
        int result = executor.execute();

        // Assert
        assertEquals(0, result, "Should return 0 on success");
    }

    @Test
    void execute_returnsOneOnException() {
        // Arrange: Use invalid files to trigger exception
        File jarListFile = null;
        File contextFile = new File("/invalid/path/context.json");
        File scriptFile = new File("/invalid/path/script.jexl");
        String resultPathTemplate = "{name}";
        Level rootLogLevel = Level.INFO;
        boolean fullContext = false;
        boolean jexlDebug = false;

        JexlExecutor executor = new JexlExecutor(
                jarListFile,
                contextFile,
                Collections.singletonList(scriptFile),
                resultPathTemplate,
                rootLogLevel,
                fullContext,
                jexlDebug);

        // Act
        int result = executor.execute();

        // Assert
        assertEquals(1, result, "Should return 1 on exception");
    }
}
