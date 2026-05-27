package io.github.siakhooi.jexl.executor;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Chooses between a YAML flow spec and positional CLI arguments and returns a {@link FlowFileSpec}.
 */
public final class FlowFileSpecResolver {

    private FlowFileSpecResolver() {
    }

    /**
     * @param flowSpecYaml optional YAML file ({@code --flow-spec} / {@code -f})
     * @param contextFile optional first positional argument
     * @param scriptFiles optional remaining positional arguments
     * @param resultPathTemplate used only in positional mode (CLI {@code --result-path})
     * @param cliExitCodeExpr from {@code --exit-code-expr} / {@code -e}; used only in positional mode (must be blank
     *                        when {@code flowSpecYaml} is set)
     * @return resolved spec
     * @throws IllegalArgumentException if arguments are mutually exclusive or incomplete
     * @throws IOException if the YAML file cannot be read or parsed (see {@link FlowFileSpecYaml#load})
     */
    public static FlowFileSpec resolve(File flowSpecYaml, File contextFile, List<File> scriptFiles,
            String resultPathTemplate, String cliExitCodeExpr) throws IOException {
        boolean hasPositional = contextFile != null || (scriptFiles != null && !scriptFiles.isEmpty());
        if (flowSpecYaml != null && hasPositional) {
            throw new IllegalArgumentException(
                    "Use either --flow-spec/-f <file.yaml> or positional <contextFile> <scriptFiles>..., not both");
        }
        if (flowSpecYaml != null) {
            if (cliExitCodeExpr != null && !cliExitCodeExpr.isBlank()) {
                throw new IllegalArgumentException(
                        "Do not use --exit-code-expr/-e with --flow-spec/-f; set exitCodeExpr in the YAML file instead");
            }
            return FlowFileSpecYaml.load(flowSpecYaml);
        }
        if (contextFile == null || scriptFiles == null || scriptFiles.isEmpty()) {
            throw new IllegalArgumentException(
                    "Missing required parameters: specify --flow-spec/-f <file.yaml> or <contextFile> <scriptFiles>...");
        }
        String exitExpr = (cliExitCodeExpr == null || cliExitCodeExpr.isBlank()) ? null : cliExitCodeExpr.trim();
        return new FlowFileSpec(contextFile, scriptFiles, resultPathTemplate, null, exitExpr);
    }
}
