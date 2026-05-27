package io.github.siakhooi.jexl.executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FlowFileSpecYamlTest {

    @Test
    void load_resolvesRelativePathsAgainstYamlDirectory(@TempDir Path tempDir) throws IOException {
        Path sub = tempDir.resolve("flow");
        Files.createDirectories(sub);
        Path yaml = sub.resolve("flow.yaml");
        Files.writeString(sub.resolve("ctx.json"), "{}");
        Files.writeString(sub.resolve("step.jexl"), "1");
        Files.writeString(yaml, """
                contextFile: ctx.json
                scriptFiles:
                  - step.jexl
                resultPathTemplate: "output.{name}"
                """);

        FlowFileSpec spec = FlowFileSpecYaml.load(yaml.toFile());

        assertEquals(sub.resolve("ctx.json").toAbsolutePath().normalize(), spec.contextFile().toPath().normalize());
        assertEquals(1, spec.scriptFiles().size());
        assertEquals(sub.resolve("step.jexl").toAbsolutePath().normalize(), spec.scriptFiles().get(0).toPath().normalize());
        assertEquals("output.{name}", spec.resultPathTemplate());
        assertNull(spec.jarListFile());
        assertNull(spec.exitCodeExpr());
    }

    @Test
    void load_defaultsResultPathTemplateWhenOmitted(@TempDir Path tempDir) throws IOException {
        Path yaml = tempDir.resolve("flow.yaml");
        Files.writeString(tempDir.resolve("c.json"), "{}");
        Files.writeString(tempDir.resolve("s.jexl"), "2");
        Files.writeString(yaml, """
                contextFile: c.json
                scriptFiles:
                  - s.jexl
                """);

        FlowFileSpec spec = FlowFileSpecYaml.load(yaml.toFile());

        assertEquals("{name}", spec.resultPathTemplate());
        assertNull(spec.jarListFile());
        assertNull(spec.exitCodeExpr());
    }

    @Test
    void load_rejectsEmptyScriptFiles(@TempDir Path tempDir) throws IOException {
        Path yaml = tempDir.resolve("bad.yaml");
        Files.writeString(yaml, """
                contextFile: c.json
                scriptFiles: []
                """);

        IOException ex = assertThrows(IOException.class, () -> FlowFileSpecYaml.load(yaml.toFile()));
        assertTrue(ex.getMessage().contains("scriptFiles"));
    }

    @Test
    void load_rejectsMissingContextFile(@TempDir Path tempDir) throws IOException {
        Path yaml = tempDir.resolve("bad.yaml");
        Files.writeString(yaml, """
                scriptFiles:
                  - a.jexl
                """);

        IOException ex = assertThrows(IOException.class, () -> FlowFileSpecYaml.load(yaml.toFile()));
        assertTrue(ex.getMessage().contains("contextFile"));
    }

    @Test
    void load_rejectsBlankScriptEntry(@TempDir Path tempDir) throws IOException {
        Path yaml = tempDir.resolve("bad.yaml");
        Files.writeString(yaml, """
                contextFile: c.json
                scriptFiles:
                  - "   "
                """);

        IOException ex = assertThrows(IOException.class, () -> FlowFileSpecYaml.load(yaml.toFile()));
        assertTrue(ex.getMessage().contains("scriptFiles"));
    }

    @Test
    void load_preservesAbsolutePaths(@TempDir Path tempDir) throws IOException {
        Path ctx = tempDir.resolve("abs.json");
        Path script = tempDir.resolve("abs.jexl");
        Files.writeString(ctx, "{}");
        Files.writeString(script, "3");
        Path yaml = tempDir.resolve("nested").resolve("f.yaml");
        Files.createDirectories(yaml.getParent());
        Files.writeString(yaml, String.format("""
                contextFile: "%s"
                scriptFiles:
                  - "%s"
                """, ctx.toAbsolutePath(), script.toAbsolutePath()));

        FlowFileSpec spec = FlowFileSpecYaml.load(yaml.toFile());

        assertEquals(ctx.toAbsolutePath().normalize(), spec.contextFile().toPath().normalize());
        assertEquals(List.of(script.toAbsolutePath().normalize()), spec.scriptFiles().stream().map(f -> f.toPath().normalize()).toList());
        assertNull(spec.jarListFile());
        assertNull(spec.exitCodeExpr());
    }

    @Test
    void load_resolvesJarListFileRelativeToYamlDirectory(@TempDir Path tempDir) throws IOException {
        Files.writeString(tempDir.resolve("c.json"), "{}");
        Files.writeString(tempDir.resolve("s.jexl"), "1");
        Files.writeString(tempDir.resolve("jars.txt"), "/tmp/placeholder.jar\n");
        Path yaml = tempDir.resolve("flow.yaml");
        Files.writeString(yaml, """
                contextFile: c.json
                scriptFiles:
                  - s.jexl
                jarListFile: jars.txt
                """);

        FlowFileSpec spec = FlowFileSpecYaml.load(yaml.toFile());

        assertEquals(tempDir.resolve("jars.txt").toAbsolutePath().normalize(), spec.jarListFile().toPath().normalize());
    }

    @Test
    void load_readsExitCodeExpr(@TempDir Path tempDir) throws IOException {
        Files.writeString(tempDir.resolve("c.json"), "{}");
        Files.writeString(tempDir.resolve("s.jexl"), "1");
        Path yaml = tempDir.resolve("flow.yaml");
        Files.writeString(yaml, """
                contextFile: c.json
                scriptFiles:
                  - s.jexl
                exitCodeExpr: "script + 40"
                """);

        FlowFileSpec spec = FlowFileSpecYaml.load(yaml.toFile());

        assertEquals("script + 40", spec.exitCodeExpr());
    }

    @Test
    void load_exitCodeExprLoadsFromAtFile(@TempDir Path tempDir) throws IOException {
        Files.writeString(tempDir.resolve("c.json"), "{}");
        Files.writeString(tempDir.resolve("s.jexl"), "1");
        Files.writeString(tempDir.resolve("exit-code.jexl"), "script + 40\n");
        Path yaml = tempDir.resolve("flow.yaml");
        Files.writeString(yaml, """
                contextFile: c.json
                scriptFiles:
                  - s.jexl
                exitCodeExpr: "@file:exit-code.jexl"
                """);

        FlowFileSpec spec = FlowFileSpecYaml.load(yaml.toFile());

        assertEquals("script + 40\n", spec.exitCodeExpr());
    }
}
