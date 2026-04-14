package io.github.siakhooi.jexl.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Output {
    private static final Logger logger = LoggerFactory.getLogger(Output.class);

    private Output() {
    }

    static void print(boolean fullContext, Object contextMap, Object scriptResult) throws JsonProcessingException {
        logger.debug("Printing {} to stdout", fullContext ? "full merged context" : "last step result");
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
