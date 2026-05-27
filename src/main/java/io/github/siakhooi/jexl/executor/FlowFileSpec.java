package io.github.siakhooi.jexl.executor;

import java.io.File;
import java.util.List;

/**
 * Files and merge template that define the pipeline to run (CLI or config-derived).
 *
 * @param jarListFile optional file listing JAR paths (one per line); {@code null} when not configured or after
 *                    merging CLI/YAML so only one source applies
 * @param exitCodeExpr optional JEXL source evaluated against the final merged context after all steps; may be inline
 *                     JEXL or loaded from a file when the stored value begins with {@code @file:} (resolved when the
 *                     spec is built). When non-null, its numeric evaluation becomes the process exit code.
 */
public record FlowFileSpec(File contextFile, List<File> scriptFiles, String resultPathTemplate, File jarListFile,
        String exitCodeExpr) {
}
