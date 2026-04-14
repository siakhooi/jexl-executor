package io.github.siakhooi.jexl.executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ch.qos.logback.classic.Level;

class JexlExecutorTest {

    @Test
    void execute_returnsZeroOnSuccess(@TempDir File tempDir) {
        // Arrange
        File jarListFile = null; // or create a temp file if needed
        File contextFile = new File(tempDir, "context.json");
        File scriptFile = new File(tempDir, "script.jexl");
        String resultPathTemplate = "{name}";
        Level rootLogLevel = Level.INFO;
        boolean fullContext = false;

        // Create dummy files and write valid JSON to contextFile
        try {
            contextFile.createNewFile();
            scriptFile.createNewFile();
            // Write valid JSON to contextFile
            java.nio.file.Files.writeString(contextFile.toPath(), "{}\n");
        } catch (Exception e) {
            fail("Failed to create temp files");
        }

        JexlExecutor executor = new JexlExecutor(
                jarListFile,
                contextFile,
                Collections.singletonList(scriptFile),
                resultPathTemplate,
                rootLogLevel,
                fullContext);

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

        JexlExecutor executor = new JexlExecutor(
                jarListFile,
                contextFile,
                Collections.singletonList(scriptFile),
                resultPathTemplate,
                rootLogLevel,
                fullContext);

        // Act
        int result = executor.execute();

        // Assert
        assertEquals(1, result, "Should return 1 on exception");
    }
}
