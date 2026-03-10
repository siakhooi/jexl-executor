package io.github.siakhooi.jexl.executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.Test;

class JexlScriptExecutorTest {
    @Test
    void testExecuteSimpleExpression() {
        JexlScriptExecutor executor = new JexlScriptExecutor();
        Map<String, Object> context = Map.of("a", 2, "b", 3);
        String script = "a + b";
        Object result = executor.execute(context, script, JexlScriptExecutor.class.getClassLoader());
        assertEquals(5, result);
    }

    @Test
    void testExecuteWithEmptyContext() {
        JexlScriptExecutor executor = new JexlScriptExecutor();
        Map<String, Object> context = Map.of();
        String script = "1 + 2";
        Object result = executor.execute(context, script, JexlScriptExecutor.class.getClassLoader());
        assertEquals(3, result);
    }

    @Test
    void testExecuteWithInvalidScript() {
        JexlScriptExecutor executor = new JexlScriptExecutor();
        Map<String, Object> context = Map.of();
        String script = "invalid syntax";
        Exception exception = assertThrows(Exception.class, () -> executor.execute(context, script, JexlScriptExecutor.class.getClassLoader()));
        assertNotNull(exception);
    }
}
