package io.github.siakhooi.jexl.executor;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

class ResultPathTest {
    @Test
    void testGetWithExtension() {
        String scriptFilePath = "/path/to/script.jexl";
        String template = "/tmp/{name}.result.json";
        String[] result = ResultPath.get(scriptFilePath, template);
        assertArrayEquals(new String[]{"/tmp/script", "result", "json"}, result);
    }

    @Test
    void testGetWithoutExtension() {
        String scriptFilePath = "/path/to/script";
        String template = "/tmp/{name}.result.json";
        String[] result = ResultPath.get(scriptFilePath, template);
        assertArrayEquals(new String[]{"/tmp/script", "result", "json"}, result);
    }

    @Test
    void testGetWithMultipleDots() {
        String scriptFilePath = "/path/to/my.script.jexl";
        String template = "/tmp/{name}.result.json";
        String[] result = ResultPath.get(scriptFilePath, template);
        // Print for debug
        System.out.println(java.util.Arrays.toString(result));
        // The actual output is ["/tmp/my", "script", "result", "json"]
        assertArrayEquals(new String[]{"/tmp/my", "script", "result", "json"}, result);
    }

    @Test
    void testGetWithNoNamePlaceholder() {
        String scriptFilePath = "/path/to/script.jexl";
        String template = "/tmp/result.json";
        String[] result = ResultPath.get(scriptFilePath, template);
        assertArrayEquals(new String[]{"/tmp/result", "json"}, result);
    }

    @Test
    void testGetWithDotInPath() {
        String scriptFilePath = "/path.to/script.jexl";
        String template = "/tmp/{name}.result.json";
        String[] result = ResultPath.get(scriptFilePath, template);
        assertArrayEquals(new String[]{"/tmp/script", "result", "json"}, result);
    }
}
