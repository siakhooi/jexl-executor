package io.github.siakhooi.jexl.executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ExitCodeExprSourceTest {

    @Test
    void expand_returnsInlineExpressionUnchanged() throws IOException {
        assertEquals("a + b", ExitCodeExprSource.expand("  a + b  ", null));
    }

    @Test
    void expand_atFile_readsContent(@TempDir Path tempDir) throws IOException {
        Path f = tempDir.resolve("exit.jexl");
        Files.writeString(f, "3 + 4\n");

        assertEquals("3 + 4\n", ExitCodeExprSource.expand("@file:" + f.toAbsolutePath(), null));
    }

    @Test
    void expand_atFileRelative_resolvesAgainstBaseDir(@TempDir Path tempDir) throws IOException {
        Path sub = tempDir.resolve("sub");
        Files.createDirectories(sub);
        Files.writeString(sub.resolve("x.jexl"), "99");

        assertEquals("99", ExitCodeExprSource.expand("@file:x.jexl", sub.toFile()));
    }

    @Test
    void expand_blankPathAfterPrefix_throws() {
        assertThrows(IllegalArgumentException.class, () -> ExitCodeExprSource.expand("@file:  ", null));
        assertThrows(IllegalArgumentException.class, () -> ExitCodeExprSource.expand("@file:", null));
    }
}
