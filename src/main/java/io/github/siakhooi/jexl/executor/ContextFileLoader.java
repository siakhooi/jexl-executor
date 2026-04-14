package io.github.siakhooi.jexl.executor;

import static io.github.siakhooi.jexl.executor.InputFile.readFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ContextFileLoader {
    private static final Logger logger = LoggerFactory.getLogger(ContextFileLoader.class);

    private ContextFileLoader() {
    }

    static Map<String, Object> get(File contextFile) throws IOException {
        String contextJson = readFile(contextFile);
        if (contextJson.isBlank()) {
            logger.debug("Context file '{}' is empty; using empty object", contextFile.getAbsolutePath());
            return new HashMap<>();
        }
        ObjectMapper objectMapper = new ObjectMapper();
        @SuppressWarnings("unchecked")
        Map<String, Object> contextMap = objectMapper.readValue(contextJson, Map.class);
        logger.debug("Loaded context from '{}' ({} top-level keys)", contextFile.getAbsolutePath(), contextMap.size());
        logger.debug("Context keys: {}", contextMap.keySet());
        return contextMap;
    }
}
