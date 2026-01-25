package io.github.siakhooi.jexl.executor;


import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("ResultPath Tests")
class ResultPathTest {
    static Stream<Arguments> provideScriptFilePathAndExpected() {
        return Stream.of(
            Arguments.of("/path/to/script.jexl", "/tmp/{name}.result.json", new String[]{"/tmp/script", "result", "json"}),
            Arguments.of("/path/to/script", "/tmp/{name}.result.json", new String[]{"/tmp/script", "result", "json"}),
            Arguments.of("/path/to/my.script.jexl", "/tmp/{name}.result.json", new String[]{"/tmp/my", "script", "result", "json"})
        );
    }

    @ParameterizedTest(name = "{0} with template {1}")
    @MethodSource("provideScriptFilePathAndExpected")
    @DisplayName("Should parse script path with template placeholder")
    void testGetParameterized(String scriptFilePath, String template, String[] expected) {
        String[] result = ResultPath.get(scriptFilePath, template);
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Should parse path without name placeholder")
    void testGetWithNoNamePlaceholder() {
        String scriptFilePath = "/path/to/script.jexl";
        String template = "/tmp/result.json";
        String[] result = ResultPath.get(scriptFilePath, template);
        assertArrayEquals(new String[]{"/tmp/result", "json"}, result);
    }

    @Test
    @DisplayName("Should handle dots in directory path")
    void testGetWithDotInPath() {
        String scriptFilePath = "/path.to/script.jexl";
        String template = "/tmp/{name}.result.json";
        String[] result = ResultPath.get(scriptFilePath, template);
        assertArrayEquals(new String[]{"/tmp/script", "result", "json"}, result);
    }
}
