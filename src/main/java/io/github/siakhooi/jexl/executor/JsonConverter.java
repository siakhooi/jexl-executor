package io.github.siakhooi.jexl.executor;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonConverter {
    private JsonConverter() {
    }

    static Object parseJson(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, Object.class);
    }

    static String toJsonString(Object obj) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "<json serialization error>";
        }
    }
}
