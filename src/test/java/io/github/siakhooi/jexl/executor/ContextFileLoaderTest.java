package io.github.siakhooi.jexl.executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ContextFileLoaderTest {
    @Test
    void testGetValidJson(@TempDir Path tempDir) throws IOException {
        String json = "{" +
                "\"a\": 1, " +
                "\"b\": \"text\", " +
                "\"c\": {\"d\": 2}" +
                "}";
        File file = tempDir.resolve("context.json").toFile();
        Files.write(file.toPath(), json.getBytes());
        Map<String, Object> map = ContextFileLoader.get(file);
        assertEquals(1, map.get("a"));
        assertEquals("text", map.get("b"));
        assertTrue(map.get("c") instanceof Map);
        Map<?, ?> c = (Map<?, ?>) map.get("c");
        assertEquals(2, c.get("d"));
    }

    @Test
    void testGetEmptyJson(@TempDir Path tempDir) throws IOException {
        String json = "{}";
        File file = tempDir.resolve("empty.json").toFile();
        Files.write(file.toPath(), json.getBytes());
        Map<String, Object> map = ContextFileLoader.get(file);
        assertTrue(map.isEmpty());
    }

    @Test
    void testGetInvalidJson(@TempDir Path tempDir) throws IOException {
        String json = "{invalid json}";
        File file = tempDir.resolve("bad.json").toFile();
        Files.write(file.toPath(), json.getBytes());
        assertThrows(IOException.class, () -> ContextFileLoader.get(file));
    }
}
