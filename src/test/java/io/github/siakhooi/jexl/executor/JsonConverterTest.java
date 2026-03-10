package io.github.siakhooi.jexl.executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.Test;

class JsonConverterTest {
    @Test
    void testParseJsonValid() throws IOException {
        String json = "{\"key\":\"value\"}";
        Object result = JsonConverter.parseJson(json);
        assertTrue(result instanceof Map);
        assertEquals("value", ((Map<?, ?>) result).get("key"));
    }

    @Test
    void testParseJsonInvalid() {
        String invalidJson = "{key:value}";
        assertThrows(IOException.class, () -> JsonConverter.parseJson(invalidJson));
    }

    @Test
    void testToJsonStringValid() {
        Map<String, String> map = Map.of("key", "value");
        String json = JsonConverter.toJsonString(map);
        assertTrue(json.contains("\"key\":\"value\""));
    }

    @Test
    void testToJsonStringInvalid() {
        Object obj = new Object() {
            // Jackson cannot serialize this anonymous inner class
        };
        String json = JsonConverter.toJsonString(obj);
        assertEquals("<json serialization error>", json);
    }
}
