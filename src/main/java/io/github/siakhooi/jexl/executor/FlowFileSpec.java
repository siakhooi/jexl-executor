package io.github.siakhooi.jexl.executor;

import java.io.File;
import java.util.List;

/**
 * Files and merge template that define the pipeline to run (CLI or config-derived).
 *
 * @param jarListFile optional file listing JAR paths (one per line); {@code null} when not configured or after
 *                    merging CLI/YAML so only one source applies
 */
public record FlowFileSpec(File contextFile, List<File> scriptFiles, String resultPathTemplate, File jarListFile) {
}
