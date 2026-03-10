package io.github.siakhooi.jexl.executor;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

class OutputTest {
    @Test
    void testPrintFullContext() throws JsonProcessingException {
        Map<String, String> context = Map.of("key", "value");
        Map<String, String> scriptResult = Map.of("result", "ok");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));
        try {
            Output.print(true, context, scriptResult);
        } finally {
            System.setOut(original);
        }
        String output = out.toString();
        assertTrue(output.contains("key"));
        assertTrue(output.contains("value"));
    }

    @Test
    void testPrintScriptResult() throws JsonProcessingException {
        Map<String, String> context = Map.of("key", "value");
        Map<String, String> scriptResult = Map.of("result", "ok");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));
        try {
            Output.print(false, context, scriptResult);
        } finally {
            System.setOut(original);
        }
        String output = out.toString();
        assertTrue(output.contains("result"));
        assertTrue(output.contains("ok"));
    }

    @Test
    void testPrintThrowsJsonProcessingException() {
        Object badObject = new Object() {
            // Jackson cannot serialize this anonymous inner class
        };
        Exception exception = assertThrows(JsonProcessingException.class, () -> Output.print(true, badObject, badObject));
        assertNotNull(exception);
    }
}
