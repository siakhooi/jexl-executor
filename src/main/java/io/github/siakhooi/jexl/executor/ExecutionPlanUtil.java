package io.github.siakhooi.jexl.executor;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import io.github.siakhooi.jexl.executor.config.ExecutionPlan;
import io.github.siakhooi.jexl.executor.config.ExecutionStep;
import io.github.siakhooi.jexl.executor.config.ExecutionType;

public class ExecutionPlanUtil {
    private ExecutionPlanUtil() {}

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

    public static ExecutionPlan loadExecutionPlan(List<File> scriptFiles) {
        ExecutionPlan executionPlan = new ExecutionPlan();

        List<ExecutionStep> steps = scriptFiles.stream()
                .map(file -> new ExecutionStep(getBaseName(file.getAbsolutePath()), file,
                        getExecutionType(file.getAbsolutePath())))
                .toList();
        executionPlan.setSteps(steps);
        return executionPlan;
    }

}
