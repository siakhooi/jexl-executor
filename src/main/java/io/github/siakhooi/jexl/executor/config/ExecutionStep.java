package io.github.siakhooi.jexl.executor.config;

import java.io.File;

public record ExecutionStep(String name, File scriptFile, ExecutionType executionType) {

}
