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

        FlowFileSpec spec = FlowFileSpecResolver.resolve(null, ctx, scripts, "out.{name}", null);

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
                contextFile: c.json
                scriptFiles:
                  - s.jexl
                resultPathTemplate: "x.{name}"
                """);

        FlowFileSpec spec = FlowFileSpecResolver.resolve(yaml.toFile(), null, null, "{ignored}", null);

        assertEquals(tempDir.resolve("c.json").toAbsolutePath().normalize(), spec.contextFile().toPath().normalize());
        assertEquals(1, spec.scriptFiles().size());
        assertEquals("x.{name}", spec.resultPathTemplate());
        assertNull(spec.jarListFile());
        assertNull(spec.exitCodeExpr());
    }

    @Test
    void resolve_yamlWithContextFile_throwsIllegalArgumentException(@TempDir Path tempDir) throws IOException {
        Path yaml = tempDir.resolve("f.yaml");
        Files.writeString(yaml, "contextFile: c.json\nscriptFiles: [s.jexl]\n");
        Files.writeString(tempDir.resolve("c.json"), "{}");
        Files.writeString(tempDir.resolve("s.jexl"), "1");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> FlowFileSpecResolver.resolve(yaml.toFile(), new File("/any/context.json"), null, "{name}", null));
        assertTrue(ex.getMessage().contains("not both"));
    }

    @Test
    void resolve_yamlWithScriptFiles_throwsIllegalArgumentException(@TempDir Path tempDir) throws IOException {
        Path yaml = tempDir.resolve("f.yaml");
        Files.writeString(yaml, "contextFile: c.json\nscriptFiles: [s.jexl]\n");
        Files.writeString(tempDir.resolve("c.json"), "{}");
        Files.writeString(tempDir.resolve("s.jexl"), "1");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> FlowFileSpecResolver.resolve(yaml.toFile(), null, List.of(new File("/other.jexl")), "{name}",
                        null));
        assertTrue(ex.getMessage().contains("not both"));
    }

    @Test
    void resolve_nothingProvided_throwsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> FlowFileSpecResolver.resolve(null, null, null, "{name}", null));
        assertTrue(ex.getMessage().contains("Missing required parameters"));
    }

    @Test
    void resolve_contextWithoutScripts_throwsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> FlowFileSpecResolver.resolve(null, new File("/ctx.json"), null, "{name}", null));
        assertTrue(ex.getMessage().contains("Missing required parameters"));
    }

    @Test
    void resolve_contextWithEmptyScripts_throwsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> FlowFileSpecResolver.resolve(null, new File("/ctx.json"), Collections.emptyList(), "{name}",
                        null));
        assertTrue(ex.getMessage().contains("Missing required parameters"));
    }

    @Test
    void resolve_scriptsWithoutContext_throwsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> FlowFileSpecResolver.resolve(null, null, List.of(new File("/a.jexl")), "{name}", null));
        assertTrue(ex.getMessage().contains("Missing required parameters"));
    }

    @Test
    void resolve_missingYamlFile_throwsIOException(@TempDir Path tempDir) {
        Path missing = tempDir.resolve("does-not-exist.yaml");

        assertThrows(IOException.class,
                () -> FlowFileSpecResolver.resolve(missing.toFile(), null, null, "{name}", null));
    }

    @Test
    void resolve_yamlWithCliExitCodeExpr_throwsIllegalArgumentException(@TempDir Path tempDir) throws IOException {
        Path yaml = tempDir.resolve("f.yaml");
        Files.writeString(yaml, "contextFile: c.json\nscriptFiles: [s.jexl]\n");
        Files.writeString(tempDir.resolve("c.json"), "{}");
        Files.writeString(tempDir.resolve("s.jexl"), "1");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> FlowFileSpecResolver.resolve(yaml.toFile(), null, null, "{name}", "0"));
        assertTrue(ex.getMessage().contains("--exit-code-expr"));
    }

    @Test
    void resolve_positionalMode_appliesTrimmedCliExitCodeExpr() throws IOException {
        File ctx = new File("/tmp/ctx.json");
        List<File> scripts = List.of(new File("/tmp/a.jexl"));

        FlowFileSpec spec = FlowFileSpecResolver.resolve(null, ctx, scripts, "out.{name}", "  script  ");

        assertEquals("script", spec.exitCodeExpr());
    }
}
