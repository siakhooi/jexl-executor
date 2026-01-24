package io.github.siakhooi.jexl.executor;

import java.util.HashMap;
import java.util.Map;

public class ContextMapMerger {
    private ContextMapMerger() {
    }

    static Map<String, Object> merge(Map<String, Object> contextMap, Object result, String[] pathParts) {
        Map<String, Object> currentMap = contextMap;
        for (int i = 0; i < pathParts.length - 1; i++) {
            String part = pathParts[i];
            Object next = currentMap.computeIfAbsent(part, k -> new HashMap<String, Object>());
            if (!(next instanceof Map)) {
                Map<String, Object> newMap = new HashMap<>();
                currentMap.put(part, newMap);
                currentMap = newMap;
            } else {
                @SuppressWarnings("unchecked")
                Map<String, Object> nextMap = (Map<String, Object>) next;
                currentMap = nextMap;
            }
        }

        currentMap.put(pathParts[pathParts.length - 1], result);
        return contextMap;

    }

}
