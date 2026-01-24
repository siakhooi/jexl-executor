package io.github.siakhooi.jexl.executor;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Output {
    private Output() {
    }

    @SuppressWarnings("java:S106")
    public static void print(Map<String, Object> contextMap) throws JsonProcessingException {
        ObjectMapper prettyMapper = new ObjectMapper();
        String resultJson = prettyMapper.writerWithDefaultPrettyPrinter().writeValueAsString(contextMap);
        System.out.println(resultJson);
    }
}
