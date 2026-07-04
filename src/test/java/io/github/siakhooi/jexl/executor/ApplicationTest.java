package io.github.siakhooi.jexl.executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ApplicationTest {

    @Test
    void run_withHelpFlag_returnsZero() {
        assertEquals(0, Application.run(new String[] { "--help" }));
    }

    @Test
    void run_withPositionalArgs_executesFlow(@TempDir Path tempDir) throws IOException {
        Path context = tempDir.resolve("context.json");
        Files.writeString(context, "{}");
        Path script = tempDir.resolve("step.jexl");
        Files.writeString(script, "42");

        int exit = Application.run(new String[] { context.toString(), script.toString() });

        assertEquals(0, exit);
    }

    @Test
    void main_withNoArgs_printsUsageAndReturns() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream prevOut = System.out;
        System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));
        try {
            Application.main(new String[0]);
            String usage = out.toString(StandardCharsets.UTF_8);
            assertTrue(usage.contains("Usage: jexl-executor"));
            assertTrue(usage.contains("jexl-executor context.json script1.jexl script2.jexl..."));
            assertTrue(!usage.contains("Missing required parameters"));
        } finally {
            System.setOut(prevOut);
        }
    }
}
