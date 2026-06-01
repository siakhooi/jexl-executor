package io.github.siakhooi.jexl.executor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * Loads a {@link FlowFileSpec} from a YAML file. Relative paths are resolved against the YAML file's directory.
 *
 * <p>Expected shape:
 *
 * <pre>
 * resultPathTemplate: "{name}"   # optional, defaults to {name}
 * jarListFile: jars.txt          # optional; same format as --jarfile (mutually exclusive with --jarfile/-j)
 * flows:
 *   default:
 *     contextFile: context.json
 *     scriptFiles:
 *       - step1.jexl
 *     exitCodeExpr: "status"     # optional; or @file:exit.jexl (relative to this YAML's directory)
 *   other_flow_id:
 *     contextFile: other.json
 *     scriptFiles:
 *       - a.jexl
 * </pre>
 */
public final class FlowFileSpecYaml {

    public static final String DEFAULT_FLOW_ID = "default";

    private static final String DEFAULT_RESULT_PATH_TEMPLATE = "{name}";

    private FlowFileSpecYaml() {
    }

    /**
     * @param yamlFile YAML flow spec file
     * @param requestedFlowId which entry under {@code flows} to run; {@code null} or blank means {@link #DEFAULT_FLOW_ID}
     */
    public static FlowFileSpec load(File yamlFile, String requestedFlowId) throws IOException {
        Objects.requireNonNull(yamlFile, "yamlFile");
        File baseDir = yamlFile.getAbsoluteFile().getParentFile();
        if (baseDir == null) {
            baseDir = new File(".").getAbsoluteFile();
        }

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        Dto dto = mapper.readValue(yamlFile, Dto.class);

        rejectLegacyRootPipelineFields(dto);

        if (dto.flows == null || dto.flows.isEmpty()) {
            throw new IOException("YAML flow spec must set non-empty 'flows'");
        }

        String flowId = (requestedFlowId == null || requestedFlowId.isBlank())
                ? DEFAULT_FLOW_ID
                : requestedFlowId.trim();

        if (!dto.flows.containsKey(flowId)) {
            throw new IOException("YAML flow spec has no flow '" + flowId + "'; defined flows: "
                    + String.join(", ", new TreeSet<>(dto.flows.keySet())));
        }
        FlowBodyDto flow = dto.flows.get(flowId);
        if (flow == null) {
            throw new IOException("YAML flow spec flows." + flowId + " must be a non-null mapping");
        }

        if (flow.contextFile == null || flow.contextFile.isBlank()) {
            throw new IOException("YAML flow spec flows." + flowId + " must set non-blank 'contextFile'");
        }
        if (flow.scriptFiles == null || flow.scriptFiles.isEmpty()) {
            throw new IOException("YAML flow spec flows." + flowId + " must set non-empty 'scriptFiles'");
        }

        File context = resolvePath(baseDir, flow.contextFile.trim());
        List<File> scripts = new ArrayList<>(flow.scriptFiles.size());
        for (String path : flow.scriptFiles) {
            if (path == null || path.isBlank()) {
                throw new IOException("YAML flow spec flows." + flowId + " 'scriptFiles' entries must be non-blank");
            }
            scripts.add(resolvePath(baseDir, path.trim()));
        }

        String template = dto.resultPathTemplate;
        if (template == null || template.isBlank()) {
            template = DEFAULT_RESULT_PATH_TEMPLATE;
        }

        File jarList = null;
        if (dto.jarListFile != null && !dto.jarListFile.isBlank()) {
            jarList = resolvePath(baseDir, dto.jarListFile.trim());
        }

        String exitCodeExpr = null;
        if (flow.exitCodeExpr != null && !flow.exitCodeExpr.isBlank()) {
            exitCodeExpr = ExitCodeExprSource.expand(flow.exitCodeExpr.trim(), baseDir);
        }

        return new FlowFileSpec(context, scripts, template, jarList, exitCodeExpr);
    }

    private static void rejectLegacyRootPipelineFields(Dto dto) throws IOException {
        boolean legacy = (dto.contextFile != null && !dto.contextFile.isBlank())
                || (dto.scriptFiles != null && !dto.scriptFiles.isEmpty())
                || (dto.exitCodeExpr != null && !dto.exitCodeExpr.isBlank());
        if (legacy) {
            throw new IOException(
                    "YAML flow spec must define contextFile, scriptFiles, and exitCodeExpr under 'flows:<id>:'; root-level contextFile/scriptFiles/exitCodeExpr are not supported");
        }
    }

    private static File resolvePath(File baseDir, String path) {
        File f = new File(path);
        return f.isAbsolute() ? f : new File(baseDir, path);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static final class Dto {
        /** Legacy root keys; if set, {@link #rejectLegacyRootPipelineFields} rejects the file. */
        public String contextFile;
        public List<String> scriptFiles;
        public String exitCodeExpr;

        public Map<String, FlowBodyDto> flows;
        public String resultPathTemplate;
        public String jarListFile;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static final class FlowBodyDto {
        public String contextFile;
        public List<String> scriptFiles;
        public String exitCodeExpr;
    }
}
