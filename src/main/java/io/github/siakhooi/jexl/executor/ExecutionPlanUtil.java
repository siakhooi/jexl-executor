package io.github.siakhooi.jexl.executor;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import io.github.siakhooi.jexl.executor.config.ExecutionPlan;
import io.github.siakhooi.jexl.executor.config.ExecutionStep;
import io.github.siakhooi.jexl.executor.config.ExecutionType;

public class ExecutionPlanUtil {
    private ExecutionPlanUtil() {
    }

    private static String getBaseName(String scriptFilePath) {
        String basename = Paths.get(scriptFilePath).getFileName().toString();
        int dotIndex = basename.lastIndexOf('.');
        if (dotIndex > 0) {
            basename = basename.substring(0, dotIndex);
        }
        return basename;
    }

    public static ExecutionPlan loadExecutionPlan(List<File> scriptFiles) {
        ExecutionPlan executionPlan = new ExecutionPlan();

        List<ExecutionStep> steps = scriptFiles.stream()
                .map(file -> new ExecutionStep(
                        getBaseName(file.getAbsolutePath()),
                        file,
                        ExecutionType.JEXL))
                .toList();
        executionPlan.setSteps(steps);
        return executionPlan;
    }

}
