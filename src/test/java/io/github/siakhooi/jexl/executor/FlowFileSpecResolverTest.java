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

class FlowFileSpecResolverTest {

    @Test
    void resolve_positionalMode_returnsFlowFileSpec() throws IOException {
        File ctx = new File("/tmp/ctx.json");
        List<File> scripts = List.of(new File("/tmp/a.jexl"));

        FlowFileSpec spec = FlowFileSpecResolver.resolve(null, ctx, scripts, "out.{name}", null, null);

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

        FlowFileSpec spec = FlowFileSpecResolver.resolve(yaml.toFile(), null, null, "{ignored}", null, "default");

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

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> FlowFileSpecResolver.resolve(yaml.toFile(), new File("/any/context.json"), null, "{name}", null,
                        "default"));
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

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> FlowFileSpecResolver.resolve(yaml.toFile(), null, List.of(new File("/other.jexl")), "{name}", null,
                        "default"));
        assertTrue(ex.getMessage().contains("not both"));
    }

    @Test
    void resolve_nothingProvided_throwsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> FlowFileSpecResolver.resolve(null, null, null, "{name}", null, null));
        assertTrue(ex.getMessage().contains("Missing required parameters"));
    }

    @Test
    void resolve_contextWithoutScripts_throwsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> FlowFileSpecResolver.resolve(null, new File("/ctx.json"), null, "{name}", null, null));
        assertTrue(ex.getMessage().contains("Missing required parameters"));
    }

    @Test
    void resolve_contextWithEmptyScripts_throwsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> FlowFileSpecResolver.resolve(null, new File("/ctx.json"), Collections.emptyList(), "{name}", null,
                        null));
        assertTrue(ex.getMessage().contains("Missing required parameters"));
    }

    @Test
    void resolve_scriptsWithoutContext_throwsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> FlowFileSpecResolver.resolve(null, null, List.of(new File("/a.jexl")), "{name}", null, null));
        assertTrue(ex.getMessage().contains("Missing required parameters"));
    }

    @Test
    void resolve_missingYamlFile_throwsIOException(@TempDir Path tempDir) {
        Path missing = tempDir.resolve("does-not-exist.yaml");

        assertThrows(IOException.class,
                () -> FlowFileSpecResolver.resolve(missing.toFile(), null, null, "{name}", null, "default"));
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

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> FlowFileSpecResolver.resolve(yaml.toFile(), null, null, "{name}", "0", "default"));
        assertTrue(ex.getMessage().contains("--exit-code-expr"));
    }

    @Test
    void resolve_positionalMode_appliesTrimmedCliExitCodeExpr() throws IOException {
        File ctx = new File("/tmp/ctx.json");
        List<File> scripts = List.of(new File("/tmp/a.jexl"));

        FlowFileSpec spec = FlowFileSpecResolver.resolve(null, ctx, scripts, "out.{name}", "  script  ", null);

        assertEquals("script", spec.exitCodeExpr());
    }

    @Test
    void resolve_positionalMode_loadsExitCodeFromAtFile(@TempDir Path tempDir) throws IOException {
        Files.writeString(tempDir.resolve("exit.jexl"), "7\n");
        File ctx = tempDir.resolve("ctx.json").toFile();
        Files.writeString(ctx.toPath(), "{}");
        List<File> scripts = List.of(tempDir.resolve("s.jexl").toFile());
        Files.writeString(scripts.get(0).toPath(), "1");

        FlowFileSpec spec = FlowFileSpecResolver.resolve(null, ctx, scripts, "{name}",
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

        FlowFileSpec spec = FlowFileSpecResolver.resolve(yaml.toFile(), null, null, "{name}", null, "alt");

        assertEquals(tempDir.resolve("b.json").toAbsolutePath().normalize(), spec.contextFile().toPath().normalize());
    }
}
