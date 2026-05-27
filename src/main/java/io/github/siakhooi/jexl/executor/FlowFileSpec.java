package io.github.siakhooi.jexl.executor;

import java.io.File;
import java.util.List;

/**
 * Files and merge template that define the pipeline to run (CLI or config-derived).
 *
 * @param jarListFile optional file listing JAR paths (one per line); {@code null} when not configured or after
 *                    merging CLI/YAML so only one source applies
 * @param exitCodeExpr optional JEXL expression evaluated against the final merged context after all steps; when
 *                     non-null, its numeric value becomes the process exit code (from YAML {@code exitCodeExpr} with
 *                     {@code --flow-spec}, or from {@code --exit-code-expr}/{@code -e} in positional mode only)
 */
public record FlowFileSpec(File contextFile, List<File> scriptFiles, String resultPathTemplate, File jarListFile,
        String exitCodeExpr) {
}
