package io.github.siakhooi.jexl.executor.config;

import java.util.List;

public class ExecutionPlan {
    private List<ExecutionStep> steps;

    public void setSteps(List<ExecutionStep> steps) {
        this.steps = steps;
    }

    public List<ExecutionStep> getSteps() {
        return steps;
    }
}
