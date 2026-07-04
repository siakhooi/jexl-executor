package io.github.siakhooi.jexl.executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ExecutionConfigResolverTest {

    @Test
    void resolve_positionalMode_returnsExecutionConfig() throws IOException {
        File ctx = new File("/tmp/ctx.json");
        List<File> scripts = List.of(new File("/tmp/a.jexl"));

        ExecutionConfig spec = ExecutionConfigResolver.resolve(null, ctx, scripts, "out.{name}", null, null);

        assertSame(ctx, spec.contextFile());
        assertEquals(scripts, spec.scriptFiles());
        assertEquals("out.{name}", spec.resultPathTemplate());
        assertNull(spec.jarListFile());
        assertNull(spec.exitCodeExpr());
    }

    @Test
    void resolve_yamlMode_returnsLoadedSpec(@TempDir Path tempDir) throws IOException {
        Files.writeString(tempDir.resolve("c.json"), "{}");
        Files.writeString(tempDir.resolve("s.jexl"), "1");
        Path yaml = tempDir.resolve("flow.yaml");
        Files.writeString(yaml, """
                resultPathTemplate: "x.{name}"
                flows:
                  default:
                    contextFile: c.json
                    scriptFiles:
                      - s.jexl
                """);

        ExecutionConfig spec = ExecutionConfigResolver.resolve(yaml.toFile(), null, null, "{ignored}", null, "default");

        assertEquals(tempDir.resolve("c.json").toAbsolutePath().normalize(), spec.contextFile().toPath().normalize());
        assertEquals(1, spec.scriptFiles().size());
        assertEquals("x.{name}", spec.resultPathTemplate());
        assertNull(spec.jarListFile());
        assertNull(spec.exitCodeExpr());
    }

    @Test
    void resolve_yamlWithContextFile_throwsIllegalArgumentException(@TempDir Path tempDir) throws IOException {
        Path yaml = tempDir.resolve("f.yaml");
        Files.writeString(yaml, """
                flows:
                  default:
                    contextFile: c.json
                    scriptFiles:
                      - s.jexl
                """);
        Files.writeString(tempDir.resolve("c.json"), "{}");
        Files.writeString(tempDir.resolve("s.jexl"), "1");

        File yamlFile = yaml.toFile();
        File contextFile = new File("/any/context.json");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ExecutionConfigResolver.resolve(yamlFile, contextFile, null, "{name}", null, "default"));
        assertTrue(ex.getMessage().contains("not both"));
    }

    @Test
    void resolve_yamlWithScriptFiles_throwsIllegalArgumentException(@TempDir Path tempDir) throws IOException {
        Path yaml = tempDir.resolve("f.yaml");
        Files.writeString(yaml, """
                flows:
                  default:
                    contextFile: c.json
                    scriptFiles:
                      - s.jexl
                """);
        Files.writeString(tempDir.resolve("c.json"), "{}");
        Files.writeString(tempDir.resolve("s.jexl"), "1");

        File yamlFile = yaml.toFile();
        List<File> otherScripts = List.of(new File("/other.jexl"));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ExecutionConfigResolver.resolve(yamlFile, null, otherScripts, "{name}", null, "default"));
        assertTrue(ex.getMessage().contains("not both"));
    }

    @Test
    void resolve_nothingProvided_throwsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ExecutionConfigResolver.resolve(null, null, null, "{name}", null, null));
        assertTrue(ex.getMessage().contains("Missing required parameters"));
    }

    @Test
    void resolve_contextWithoutScripts_throwsIllegalArgumentException() {
        File contextFile = new File("/ctx.json");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ExecutionConfigResolver.resolve(null, contextFile, null, "{name}", null, null));
        assertTrue(ex.getMessage().contains("Missing required parameters"));
    }

    @Test
    void resolve_contextWithEmptyScripts_throwsIllegalArgumentException() {
        File contextFile = new File("/ctx.json");
        List<File> emptyScripts = Collections.emptyList();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ExecutionConfigResolver.resolve(null, contextFile, emptyScripts, "{name}", null, null));
        assertTrue(ex.getMessage().contains("Missing required parameters"));
    }

    @Test
    void resolve_scriptsWithoutContext_throwsIllegalArgumentException() {
        List<File> scripts = List.of(new File("/a.jexl"));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ExecutionConfigResolver.resolve(null, null, scripts, "{name}", null, null));
        assertTrue(ex.getMessage().contains("Missing required parameters"));
    }

    @Test
    void resolve_missingYamlFile_throwsIOException(@TempDir Path tempDir) {
        Path missing = tempDir.resolve("does-not-exist.yaml");

        File missingYaml = missing.toFile();
        assertThrows(IOException.class,
                () -> ExecutionConfigResolver.resolve(missingYaml, null, null, "{name}", null, "default"));
    }

    @Test
    void resolve_yamlWithCliExitCodeExpr_throwsIllegalArgumentException(@TempDir Path tempDir) throws IOException {
        Path yaml = tempDir.resolve("f.yaml");
        Files.writeString(yaml, """
                flows:
                  default:
                    contextFile: c.json
                    scriptFiles:
                      - s.jexl
                """);
        Files.writeString(tempDir.resolve("c.json"), "{}");
        Files.writeString(tempDir.resolve("s.jexl"), "1");

        File yamlFile = yaml.toFile();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ExecutionConfigResolver.resolve(yamlFile, null, null, "{name}", "0", "default"));
        assertTrue(ex.getMessage().contains("--exit-code-expr"));
    }

    @Test
    void resolve_positionalMode_appliesTrimmedCliExitCodeExpr() throws IOException {
        File ctx = new File("/tmp/ctx.json");
        List<File> scripts = List.of(new File("/tmp/a.jexl"));

        ExecutionConfig spec = ExecutionConfigResolver.resolve(null, ctx, scripts, "out.{name}", "  script  ", null);

        assertEquals("script", spec.exitCodeExpr());
    }

    @Test
    void resolve_positionalMode_loadsExitCodeFromAtFile(@TempDir Path tempDir) throws IOException {
        Files.writeString(tempDir.resolve("exit.jexl"), "7\n");
        File ctx = tempDir.resolve("ctx.json").toFile();
        Files.writeString(ctx.toPath(), "{}");
        List<File> scripts = List.of(tempDir.resolve("s.jexl").toFile());
        Files.writeString(scripts.get(0).toPath(), "1");

        ExecutionConfig spec = ExecutionConfigResolver.resolve(null, ctx, scripts, "{name}",
                "@file:" + tempDir.resolve("exit.jexl").toAbsolutePath(), null);

        assertEquals("7\n", spec.exitCodeExpr());
    }

    @Test
    void resolve_yamlMode_passesFlowIdToLoader(@TempDir Path tempDir) throws IOException {
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
                  alt:
                    contextFile: b.json
                    scriptFiles:
                      - x.jexl
                """);

        ExecutionConfig spec = ExecutionConfigResolver.resolve(yaml.toFile(), null, null, "{name}", null, "alt");

        assertEquals(tempDir.resolve("b.json").toAbsolutePath().normalize(), spec.contextFile().toPath().normalize());
    }
}
