package io.github.siakhooi.jexl.executor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Output {
    private Output() {
    }

    public static void print(boolean fullContext, Object contextMap, Object scriptResult) throws JsonProcessingException {
        ObjectMapper prettyMapper = new ObjectMapper();
        String resultJson;
        if (fullContext) {
            resultJson = prettyMapper.writerWithDefaultPrettyPrinter().writeValueAsString(contextMap);
        } else {
            resultJson = prettyMapper.writerWithDefaultPrettyPrinter().writeValueAsString(scriptResult);
        }
        Console.printf("%s%n", resultJson);
    }
}
