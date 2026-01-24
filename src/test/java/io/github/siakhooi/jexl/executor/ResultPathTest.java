package io.github.siakhooi.jexl.executor;


import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class ResultPathTest {
    static Stream<org.junit.jupiter.params.provider.Arguments> provideScriptFilePathAndExpected() {
        return Stream.of(
            org.junit.jupiter.params.provider.Arguments.of("/path/to/script.jexl", "/tmp/{name}.result.json", new String[]{"/tmp/script", "result", "json"}),
            org.junit.jupiter.params.provider.Arguments.of("/path/to/script", "/tmp/{name}.result.json", new String[]{"/tmp/script", "result", "json"}),
            org.junit.jupiter.params.provider.Arguments.of("/path/to/my.script.jexl", "/tmp/{name}.result.json", new String[]{"/tmp/my", "script", "result", "json"})
        );
    }

    @ParameterizedTest
    @MethodSource("provideScriptFilePathAndExpected")
    void testGetParameterized(String scriptFilePath, String template, String[] expected) {
        String[] result = ResultPath.get(scriptFilePath, template);
        assertArrayEquals(expected, result);
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
