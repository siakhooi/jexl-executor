package io.github.siakhooi.jexl.executor;

import static io.github.siakhooi.jexl.executor.InputFile.readFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ContextFileLoader {
    private ContextFileLoader() {
    }

    static Map<String, Object> get(File contextFile) throws IOException {
        String contextJson = readFile(contextFile);
        if (contextJson.isBlank()) {
            return new HashMap<>();
        }
        ObjectMapper objectMapper = new ObjectMapper();
        @SuppressWarnings("unchecked")
        Map<String, Object> contextMap = objectMapper.readValue(contextJson, Map.class);
        return contextMap;
    }
}
