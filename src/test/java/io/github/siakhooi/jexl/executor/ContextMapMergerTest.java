package io.github.siakhooi.jexl.executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ContextMapMerger Tests")
class ContextMapMergerTest {
    @Test
    @DisplayName("Should merge result at simple path")
    void testMergeSimplePath() {
        Map<String, Object> context = new HashMap<>();
        String[] path = {"a"};
        Object result = 123;
        Map<String, Object> merged = ContextMapMerger.merge(context, result, path);
        assertEquals(123, merged.get("a"));
    }

    @Test
    @DisplayName("Should merge result at nested path with multiple levels")
    void testMergeNestedPath() {
        Map<String, Object> context = new HashMap<>();
        String[] path = {"a", "b", "c"};
        Object result = "value";
        Map<String, Object> merged = ContextMapMerger.merge(context, result, path);
        assertTrue(merged.containsKey("a"));
        
        Map<?, ?> mapA = assertInstanceOf(Map.class, merged.get("a"));
        Map<?, ?> mapB = assertInstanceOf(Map.class, mapA.get("b"));
        assertEquals("value", mapB.get("c"));
    }

    @Test
    @DisplayName("Should overwrite existing value at path")
    void testMergeOverwritesValue() {
        Map<String, Object> context = new HashMap<>();
        context.put("a", 1);
        String[] path = {"a"};
        Object result = 2;
        Map<String, Object> merged = ContextMapMerger.merge(context, result, path);
        assertEquals(2, merged.get("a"));
    }

    @Test
    @DisplayName("Should replace non-map value with map at path")
    void testMergeOverwritesNonMap() {
        Map<String, Object> context = new HashMap<>();
        context.put("a", 1);
        String[] path = {"a", "b"};
        Object result = 3;
        Map<String, Object> merged = ContextMapMerger.merge(context, result, path);
        Map<?, ?> mapA = assertInstanceOf(Map.class, merged.get("a"));
        assertEquals(3, mapA.get("b"));
    }

    @Test
    @DisplayName("Should merge into deep existing map structure")
    void testMergeDeepExistingMap() {
        Map<String, Object> context = new HashMap<>();
        Map<String, Object> inner = new HashMap<>();
        context.put("a", inner);
        String[] path = {"a", "b"};
        Object result = 4;
        Map<String, Object> merged = ContextMapMerger.merge(context, result, path);
        Map<?, ?> mapA = assertInstanceOf(Map.class, merged.get("a"));
        assertEquals(4, mapA.get("b"));
    }
}
