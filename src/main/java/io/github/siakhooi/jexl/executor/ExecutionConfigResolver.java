package io.github.siakhooi.jexl.executor;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Chooses between a YAML execution config and positional CLI arguments and returns an {@link ExecutionConfig}.
 */
public final class ExecutionConfigResolver {

    private ExecutionConfigResolver() {
    }

    /**
     * @param executionConfigYaml optional YAML execution config ({@code --config} / {@code -c})
     * @param contextFile optional first positional argument
     * @param scriptFiles optional remaining positional arguments
     * @param resultPathTemplate used only in positional mode (CLI {@code --result-path})
     * @param cliExitCodeExpr from {@code --exit-code-expr} / {@code -e}; used only in positional mode (must be blank
     *                        when {@code executionConfigYaml} is set)
     * @param yamlFlowId which {@code flows} entry to load when using YAML; {@code null} or blank means {@code "default"}
     *                   ({@link ExecutionConfigYaml#DEFAULT_FLOW_ID}); ignored in positional mode
     * @return resolved execution config
     * @throws IllegalArgumentException if arguments are mutually exclusive or incomplete
     * @throws IOException if the YAML file cannot be read or parsed (see {@link ExecutionConfigYaml#load})
     */
    public static ExecutionConfig resolve(File executionConfigYaml, File contextFile, List<File> scriptFiles,
            String resultPathTemplate, String cliExitCodeExpr, String yamlFlowId) throws IOException {
        boolean hasPositional = contextFile != null || (scriptFiles != null && !scriptFiles.isEmpty());
        if (executionConfigYaml != null && hasPositional) {
            throw new IllegalArgumentException(
                    "Use either --config/-c <execution-config.yaml> or positional <contextFile> <scriptFiles>..., not both");
        }
        if (executionConfigYaml != null) {
            if (cliExitCodeExpr != null && !cliExitCodeExpr.isBlank()) {
                throw new IllegalArgumentException(
                        "Do not use --exit-code-expr/-e with --config/-c; set exitCodeExpr in the execution config YAML instead");
            }
            return ExecutionConfigYaml.load(executionConfigYaml, yamlFlowId);
        }
        if (contextFile == null || scriptFiles == null || scriptFiles.isEmpty()) {
            throw new IllegalArgumentException(
                    "Missing required parameters: specify --config/-c <execution-config.yaml> or <contextFile> <scriptFiles>...");
        }
        String exitExpr = (cliExitCodeExpr == null || cliExitCodeExpr.isBlank()) ? null : cliExitCodeExpr.trim();
        if (exitExpr != null) {
            exitExpr = ExitCodeExprSource.expand(exitExpr, null);
        }
        return new ExecutionConfig(contextFile, scriptFiles, resultPathTemplate, null, exitExpr);
    }
}
