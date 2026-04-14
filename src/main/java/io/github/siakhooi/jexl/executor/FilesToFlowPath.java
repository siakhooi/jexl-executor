package io.github.siakhooi.jexl.executor;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.siakhooi.jexl.executor.config.ExecutionStep;
import io.github.siakhooi.jexl.executor.config.ExecutionType;
import io.github.siakhooi.jexl.executor.config.FlowPath;

public class FilesToFlowPath {
    private static final Logger logger = LoggerFactory.getLogger(FilesToFlowPath.class);

    private FilesToFlowPath() {}

    private static String getBaseName(String scriptFilePath) {
        String basename = Paths.get(scriptFilePath).getFileName().toString();
        int dotIndex = basename.lastIndexOf('.');
        if (dotIndex > 0) {
            basename = basename.substring(0, dotIndex);
        }
        return basename;
    }

    private static ExecutionType getExecutionType(String scriptFilePath) {
        String filename = Paths.get(scriptFilePath).getFileName().toString();
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return ExecutionType.fromExtension(filename.substring(dotIndex + 1));
        }
        return ExecutionType.UNKNOWN;
    }

    static FlowPath generate(List<File> scriptFiles) {
        FlowPath flowPath = new FlowPath();

        List<ExecutionStep> steps = scriptFiles.stream()
                .map(file -> new ExecutionStep(getBaseName(file.getAbsolutePath()), file,
                        getExecutionType(file.getAbsolutePath())))
                .toList();
        flowPath.setSteps(steps);
        logger.debug("Resolved flow: {}",
                steps.stream().map(s -> s.name() + "(" + s.executionType() + ")").collect(Collectors.joining(" -> ")));
        return flowPath;
    }

}
