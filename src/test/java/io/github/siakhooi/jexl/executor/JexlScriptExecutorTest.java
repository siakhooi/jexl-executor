package io.github.siakhooi.jexl.executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.apache.commons.jexl3.JexlException;
import org.junit.jupiter.api.Test;

class JexlScriptExecutorTest {
    @Test
    void testExecuteSimpleExpression() {
        ClassLoader cl = JexlScriptExecutorTest.class.getClassLoader();
        JexlScriptExecutor executor = new JexlScriptExecutor(cl);
        Map<String, Object> context = Map.of("a", 2, "b", 3);
        String script = "a + b";
        Object result = executor.execute(context, script, "test.jexl");
        assertEquals(5, result);
    }

    @Test
    void testExecuteWithEmptyContext() {
        ClassLoader cl = JexlScriptExecutorTest.class.getClassLoader();
        JexlScriptExecutor executor = new JexlScriptExecutor(cl);
        Map<String, Object> context = Map.of();
        String script = "1 + 2";
        Object result = executor.execute(context, script, "test.jexl");
        assertEquals(3, result);
    }

    @Test
    void testExecuteWithInvalidScript() {
        ClassLoader cl = JexlScriptExecutorTest.class.getClassLoader();
        JexlScriptExecutor executor = new JexlScriptExecutor(cl);
        Map<String, Object> context = Map.of();
        String script = "invalid syntax";
        assertThrows(Exception.class, () -> executor.execute(context, script, "bad.jexl"));
    }

    @Test
    void testExecuteJexlErrorMessageIncludesSourceLabel() {
        ClassLoader cl = JexlScriptExecutorTest.class.getClassLoader();
        JexlScriptExecutor executor = new JexlScriptExecutor(cl);
        Map<String, Object> context = Map.of();
        String script = "invalid syntax !!!";
        JexlException ex = assertThrows(JexlException.class,
                () -> executor.execute(context, script, "my-step.jexl"));
        assertTrue(ex.getMessage().contains("my-step.jexl"));
    }
}
