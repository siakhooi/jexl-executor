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

class ExecutionConfigYamlTest {

    @Test
    void load_resolvesRelativePathsAgainstYamlDirectory(@TempDir Path tempDir) throws IOException {
        Path sub = tempDir.resolve("flow");
        Files.createDirectories(sub);
        Path yaml = sub.resolve("flow.yaml");
        Files.writeString(sub.resolve("ctx.json"), "{}");
        Files.writeString(sub.resolve("step.jexl"), "1");
        Files.writeString(yaml, """
                resultPathTemplate: "output.{name}"
                flows:
                  default:
                    contextFile: ctx.json
                    scriptFiles:
                      - step.jexl
                """);

        ExecutionConfig spec = ExecutionConfigYaml.load(yaml.toFile(), ExecutionConfigYaml.DEFAULT_FLOW_ID);

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
                flows:
                  default:
                    contextFile: c.json
                    scriptFiles:
                      - s.jexl
                """);

        ExecutionConfig spec = ExecutionConfigYaml.load(yaml.toFile(), null);

        assertEquals("{name}", spec.resultPathTemplate());
        assertNull(spec.jarListFile());
        assertNull(spec.exitCodeExpr());
    }

    @Test
    void load_rejectsEmptyScriptFiles(@TempDir Path tempDir) throws IOException {
        Path yaml = tempDir.resolve("bad.yaml");
        Files.writeString(yaml, """
                flows:
                  default:
                    contextFile: c.json
                    scriptFiles: []
                """);

        IOException ex = assertThrows(IOException.class, () -> ExecutionConfigYaml.load(yaml.toFile(), "default"));
        assertTrue(ex.getMessage().contains("scriptFiles"));
    }

    @Test
    void load_rejectsMissingFlows(@TempDir Path tempDir) throws IOException {
        Path yaml = tempDir.resolve("bad.yaml");
        Files.writeString(yaml, "resultPathTemplate: \"{name}\"\n");

        IOException ex = assertThrows(IOException.class, () -> ExecutionConfigYaml.load(yaml.toFile(), "default"));
        assertTrue(ex.getMessage().contains("flows"));
    }

    @Test
    void load_rejectsLegacyRootPipelineFields(@TempDir Path tempDir) throws IOException {
        Path yaml = tempDir.resolve("legacy.yaml");
        Files.writeString(yaml, """
                contextFile: c.json
                scriptFiles:
                  - s.jexl
                """);

        IOException ex = assertThrows(IOException.class, () -> ExecutionConfigYaml.load(yaml.toFile(), "default"));
        assertTrue(ex.getMessage().contains("flows"));
    }

    @Test
    void load_rejectsMissingContextFileOnFlow(@TempDir Path tempDir) throws IOException {
        Path yaml = tempDir.resolve("bad.yaml");
        Files.writeString(yaml, """
                flows:
                  default:
                    scriptFiles:
                      - a.jexl
                """);

        IOException ex = assertThrows(IOException.class, () -> ExecutionConfigYaml.load(yaml.toFile(), "default"));
        assertTrue(ex.getMessage().contains("contextFile"));
    }

    @Test
    void load_rejectsBlankScriptEntry(@TempDir Path tempDir) throws IOException {
        Path yaml = tempDir.resolve("bad.yaml");
        Files.writeString(yaml, """
                flows:
                  default:
                    contextFile: c.json
                    scriptFiles:
                      - "   "
                """);

        IOException ex = assertThrows(IOException.class, () -> ExecutionConfigYaml.load(yaml.toFile(), "default"));
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
                flows:
                  default:
                    contextFile: "%s"
                    scriptFiles:
                      - "%s"
                """, ctx.toAbsolutePath(), script.toAbsolutePath()));

        ExecutionConfig spec = ExecutionConfigYaml.load(yaml.toFile(), "default");

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
                jarListFile: jars.txt
                flows:
                  default:
                    contextFile: c.json
                    scriptFiles:
                      - s.jexl
                """);

        ExecutionConfig spec = ExecutionConfigYaml.load(yaml.toFile(), "default");

        assertEquals(tempDir.resolve("jars.txt").toAbsolutePath().normalize(), spec.jarListFile().toPath().normalize());
    }

    @Test
    void load_readsExitCodeExpr(@TempDir Path tempDir) throws IOException {
        Files.writeString(tempDir.resolve("c.json"), "{}");
        Files.writeString(tempDir.resolve("s.jexl"), "1");
        Path yaml = tempDir.resolve("flow.yaml");
        Files.writeString(yaml, """
                flows:
                  default:
                    contextFile: c.json
                    scriptFiles:
                      - s.jexl
                    exitCodeExpr: "script + 40"
                """);

        ExecutionConfig spec = ExecutionConfigYaml.load(yaml.toFile(), "default");

        assertEquals("script + 40", spec.exitCodeExpr());
    }

    @Test
    void load_exitCodeExprLoadsFromAtFile(@TempDir Path tempDir) throws IOException {
        Files.writeString(tempDir.resolve("c.json"), "{}");
        Files.writeString(tempDir.resolve("s.jexl"), "1");
        Files.writeString(tempDir.resolve("exit-code.jexl"), "script + 40\n");
        Path yaml = tempDir.resolve("flow.yaml");
        Files.writeString(yaml, """
                flows:
                  default:
                    contextFile: c.json
                    scriptFiles:
                      - s.jexl
                    exitCodeExpr: "@file:exit-code.jexl"
                """);

        ExecutionConfig spec = ExecutionConfigYaml.load(yaml.toFile(), "default");

        assertEquals("script + 40\n", spec.exitCodeExpr());
    }

    @Test
    void load_throwsWhenFlowIdNotDefined(@TempDir Path tempDir) throws IOException {
        Files.writeString(tempDir.resolve("c.json"), "{}");
        Files.writeString(tempDir.resolve("s.jexl"), "1");
        Path yaml = tempDir.resolve("flow.yaml");
        Files.writeString(yaml, """
                flows:
                  default:
                    contextFile: c.json
                    scriptFiles:
                      - s.jexl
                """);

        IOException ex = assertThrows(IOException.class, () -> ExecutionConfigYaml.load(yaml.toFile(), "missing"));
        assertTrue(ex.getMessage().contains("missing"));
        assertTrue(ex.getMessage().contains("default"));
    }

    @Test
    void load_selectsNamedFlow(@TempDir Path tempDir) throws IOException {
        Files.writeString(tempDir.resolve("a.json"), "{}");
        Files.writeString(tempDir.resolve("b.json"), "{}");
        Files.writeString(tempDir.resolve("x.jexl"), "1");
        Path yaml = tempDir.resolve("flow.yaml");
        Files.writeString(yaml, """
                flows:
                  default:
                    contextFile: a.json
                    scriptFiles:
                      - x.jexl
                  other:
                    contextFile: b.json
                    scriptFiles:
                      - x.jexl
                """);

        ExecutionConfig spec = ExecutionConfigYaml.load(yaml.toFile(), "other");

        assertEquals(tempDir.resolve("b.json").toAbsolutePath().normalize(), spec.contextFile().toPath().normalize());
    }
}
