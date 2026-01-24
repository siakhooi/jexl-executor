package io.github.siakhooi.jexl.executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class ContextMapMergerTest {
    @Test
    void testMergeSimplePath() {
        Map<String, Object> context = new HashMap<>();
        String[] path = {"a"};
        Object result = 123;
        Map<String, Object> merged = ContextMapMerger.merge(context, result, path);
        assertEquals(123, merged.get("a"));
    }

    @Test
    void testMergeNestedPath() {
        Map<String, Object> context = new HashMap<>();
        String[] path = {"a", "b", "c"};
        Object result = "value";
        Map<String, Object> merged = ContextMapMerger.merge(context, result, path);
        assertTrue(merged.containsKey("a"));
        Object a = merged.get("a");
        assertTrue(a instanceof Map);
        Map<?, ?> mapA = (Map<?, ?>) a;
        Object b = mapA.get("b");
        assertTrue(b instanceof Map);
        Map<?, ?> mapB = (Map<?, ?>) b;
        assertEquals("value", mapB.get("c"));
    }

    @Test
    void testMergeOverwritesValue() {
        Map<String, Object> context = new HashMap<>();
        context.put("a", 1);
        String[] path = {"a"};
        Object result = 2;
        Map<String, Object> merged = ContextMapMerger.merge(context, result, path);
        assertEquals(2, merged.get("a"));
    }

    @Test
    void testMergeOverwritesNonMap() {
        Map<String, Object> context = new HashMap<>();
        context.put("a", 1);
        String[] path = {"a", "b"};
        Object result = 3;
        Map<String, Object> merged = ContextMapMerger.merge(context, result, path);
        assertTrue(merged.get("a") instanceof Map);
        Map<?, ?> mapA = (Map<?, ?>) merged.get("a");
        assertEquals(3, mapA.get("b"));
    }

    @Test
    void testMergeDeepExistingMap() {
        Map<String, Object> context = new HashMap<>();
        Map<String, Object> inner = new HashMap<>();
        context.put("a", inner);
        String[] path = {"a", "b"};
        Object result = 4;
        Map<String, Object> merged = ContextMapMerger.merge(context, result, path);
        assertEquals(4, ((Map<?, ?>) merged.get("a")).get("b"));
    }
}
