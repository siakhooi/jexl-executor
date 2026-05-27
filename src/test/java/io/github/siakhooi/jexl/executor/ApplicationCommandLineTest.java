package io.github.siakhooi.jexl.executor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import picocli.CommandLine;

class ApplicationCommandLineTest {

    @Test
    void flowSpecWithExitCodeExprFlag_exitsWithPicocliUserError(@TempDir Path tempDir) throws IOException {
        Files.writeString(tempDir.resolve("c.json"), "{}");
        Files.writeString(tempDir.resolve("s.jexl"), "1");
        Path yaml = tempDir.resolve("flow.yaml");
        Files.writeString(yaml, """
                contextFile: c.json
                scriptFiles:
                  - s.jexl
                """);

        CommandLine cmd = new CommandLine(new ApplicationCommandLine());
        int exit = cmd.execute("-f", yaml.toString(), "-e", "0");

        assertEquals(2, exit);
    }
}
