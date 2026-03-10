package io.github.siakhooi.jexl.executor.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ExecutionTypeTest {
    @Test
    void testFromExtensionJexl() {
        assertEquals(ExecutionType.JEXL, ExecutionType.fromExtension("jexl"));
        assertEquals(ExecutionType.JEXL, ExecutionType.fromExtension("JEXL"));
    }

    @Test
    void testFromExtensionJson() {
        assertEquals(ExecutionType.JSON, ExecutionType.fromExtension("json"));
        assertEquals(ExecutionType.JSON, ExecutionType.fromExtension("JSON"));
    }

    @Test
    void testFromExtensionUnknown() {
        assertEquals(ExecutionType.UNKNOWN, ExecutionType.fromExtension("txt"));
        assertEquals(ExecutionType.UNKNOWN, ExecutionType.fromExtension(""));
        assertEquals(ExecutionType.UNKNOWN, ExecutionType.fromExtension("unknown"));
    }

    @Test
    void testFromExtensionNull() {
        assertThrows(NullPointerException.class, () -> ExecutionType.fromExtension(null));
    }
}
