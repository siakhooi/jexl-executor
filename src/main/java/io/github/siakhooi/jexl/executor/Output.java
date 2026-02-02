package io.github.siakhooi.jexl.executor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Output {
    private Output() {
    }

    public static void print(Object scriptResult) throws JsonProcessingException {
        ObjectMapper prettyMapper = new ObjectMapper();
        String resultJson = prettyMapper.writerWithDefaultPrettyPrinter().writeValueAsString(scriptResult);
        Console.printf("%s%n", resultJson);
    }
}
