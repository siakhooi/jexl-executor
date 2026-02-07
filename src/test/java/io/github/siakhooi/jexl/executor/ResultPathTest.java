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
    static Stream<Arguments> provideNameAndExpected() {
        return Stream.of(
            Arguments.of("script", "/tmp/{name}.result.json", new String[]{"/tmp/script", "result", "json"}),
            Arguments.of("myfile", "/output/{name}.out.txt", new String[]{"/output/myfile", "out", "txt"}),
            Arguments.of("test", "{name}.json", new String[]{"test", "json"})
        );
    }

    @ParameterizedTest(name = "name={0} with template {1}")
    @MethodSource("provideNameAndExpected")
    @DisplayName("Should replace name placeholder and split by dots")
    void testGetParameterized(String name, String template, String[] expected) {
        String[] result = ResultPath.get(name, template);
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Should parse path without name placeholder")
    void testGetWithNoNamePlaceholder() {
        String name = "script";
        String template = "/tmp/result.json";
        String[] result = ResultPath.get(name, template);
        assertArrayEquals(new String[]{"/tmp/result", "json"}, result);
    }

    @Test
    @DisplayName("Should handle multiple name placeholders")
    void testGetWithMultipleNamePlaceholders() {
        String name = "data";
        String template = "/output/{name}/{name}.result.json";
        String[] result = ResultPath.get(name, template);
        assertArrayEquals(new String[]{"/output/data/data", "result", "json"}, result);
    }

    @Test
    @DisplayName("Should handle name with special characters")
    void testGetWithSpecialCharactersInName() {
        String name = "my-file_v1";
        String template = "/tmp/{name}.result.json";
        String[] result = ResultPath.get(name, template);
        assertArrayEquals(new String[]{"/tmp/my-file_v1", "result", "json"}, result);
    }
}
