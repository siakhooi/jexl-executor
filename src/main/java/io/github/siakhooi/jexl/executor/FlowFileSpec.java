package io.github.siakhooi.jexl.executor;

import java.io.File;
import java.util.List;

/**
 * Files and merge template that define the pipeline to run (CLI or config-derived).
 */
public record FlowFileSpec(File contextFile, List<File> scriptFiles, String resultPathTemplate) {
}
