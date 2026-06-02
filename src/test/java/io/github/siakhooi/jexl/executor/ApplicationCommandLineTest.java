package io.github.siakhooi.jexl.executor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import picocli.CommandLine;

class ApplicationCommandLineTest {

    private static String minimalFlowYaml() {
        return """
                flows:
                  default:
                    contextFile: c.json
                    scriptFiles:
                      - s.jexl
                """;
    }

    @Test
    void executionConfigWithExitCodeExprFlag_exitsWithPicocliUserError(@TempDir Path tempDir) throws IOException {
        Files.writeString(tempDir.resolve("c.json"), "{}");
        Files.writeString(tempDir.resolve("s.jexl"), "1");
        Path yaml = tempDir.resolve("execution-config.yaml");
        Files.writeString(yaml, minimalFlowYaml());

        CommandLine cmd = new CommandLine(new ApplicationCommandLine());
        int exit = cmd.execute("-c", yaml.toString(), "-e", "0");

        assertEquals(2, exit);
    }

    @Test
    void flowIdWithoutExecutionConfig_exitsWithPicocliUserError() {
        CommandLine cmd = new CommandLine(new ApplicationCommandLine());
        int exit = cmd.execute("--id", "default", "/tmp/x.json", "/tmp/y.jexl");

        assertEquals(2, exit);
    }

    @Test
    void executionConfigWithUnknownFlowId_exitsWithPicocliUserError(@TempDir Path tempDir) throws IOException {
        Files.writeString(tempDir.resolve("c.json"), "{}");
        Files.writeString(tempDir.resolve("s.jexl"), "1");
        Path yaml = tempDir.resolve("execution-config.yaml");
        Files.writeString(yaml, minimalFlowYaml());

        CommandLine cmd = new CommandLine(new ApplicationCommandLine());
        int exit = cmd.execute("-c", yaml.toString(), "--id", "nope");

        assertEquals(2, exit);
    }
}
