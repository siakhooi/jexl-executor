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
 * Loads an {@link ExecutionConfig} from a YAML execution config file. Relative paths are resolved against the YAML file's directory.
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
public final class ExecutionConfigYaml {

    public static final String DEFAULT_FLOW_ID = "default";

    private static final String DEFAULT_RESULT_PATH_TEMPLATE = "{name}";

    private ExecutionConfigYaml() {
    }

    /**
     * @param yamlFile YAML execution config file
     * @param requestedFlowId which entry under {@code flows} to run; {@code null} or blank means {@link #DEFAULT_FLOW_ID}
     */
    public static ExecutionConfig load(File yamlFile, String requestedFlowId) throws IOException {
        Objects.requireNonNull(yamlFile, "yamlFile");
        File baseDir = resolveBaseDir(yamlFile);

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        Dto dto = mapper.readValue(yamlFile, Dto.class);

        rejectLegacyRootPipelineFields(dto);
        requireNonEmptyFlows(dto);

        String flowId = resolveFlowId(requestedFlowId);
        FlowBodyDto flow = requireFlow(dto, flowId);
        validateFlowBody(flow, flowId);

        File context = resolvePath(baseDir, flow.contextFile.trim());
        List<File> scripts = resolveScriptFiles(baseDir, flow, flowId);

        return new ExecutionConfig(context, scripts, resolveResultPathTemplate(dto),
                resolveOptionalJarList(baseDir, dto), resolveOptionalExitCodeExpr(baseDir, flow));
    }

    private static File resolveBaseDir(File yamlFile) {
        File baseDir = yamlFile.getAbsoluteFile().getParentFile();
        return baseDir == null ? new File(".").getAbsoluteFile() : baseDir;
    }

    private static void requireNonEmptyFlows(Dto dto) throws IOException {
        if (dto.flows == null || dto.flows.isEmpty()) {
            throw new IOException("Execution config must set non-empty 'flows'");
        }
    }

    private static String resolveFlowId(String requestedFlowId) {
        return (requestedFlowId == null || requestedFlowId.isBlank())
                ? DEFAULT_FLOW_ID
                : requestedFlowId.trim();
    }

    private static FlowBodyDto requireFlow(Dto dto, String flowId) throws IOException {
        if (!dto.flows.containsKey(flowId)) {
            throw new IOException("Execution config has no flow '" + flowId + "'; defined flows: "
                    + String.join(", ", new TreeSet<>(dto.flows.keySet())));
        }
        FlowBodyDto flow = dto.flows.get(flowId);
        if (flow == null) {
            throw new IOException("Execution config flows." + flowId + " must be a non-null mapping");
        }
        return flow;
    }

    private static void validateFlowBody(FlowBodyDto flow, String flowId) throws IOException {
        if (flow.contextFile == null || flow.contextFile.isBlank()) {
            throw new IOException("Execution config flows." + flowId + " must set non-blank 'contextFile'");
        }
        if (flow.scriptFiles == null || flow.scriptFiles.isEmpty()) {
            throw new IOException("Execution config flows." + flowId + " must set non-empty 'scriptFiles'");
        }
    }

    private static List<File> resolveScriptFiles(File baseDir, FlowBodyDto flow, String flowId) throws IOException {
        List<File> scripts = new ArrayList<>(flow.scriptFiles.size());
        for (String path : flow.scriptFiles) {
            if (path == null || path.isBlank()) {
                throw new IOException("Execution config flows." + flowId + " 'scriptFiles' entries must be non-blank");
            }
            scripts.add(resolvePath(baseDir, path.trim()));
        }
        return scripts;
    }

    private static String resolveResultPathTemplate(Dto dto) {
        String template = dto.resultPathTemplate;
        if (template == null || template.isBlank()) {
            template = DEFAULT_RESULT_PATH_TEMPLATE;
        }
        return template;
    }

    private static File resolveOptionalJarList(File baseDir, Dto dto) {
        if (dto.jarListFile == null || dto.jarListFile.isBlank()) {
            return null;
        }
        return resolvePath(baseDir, dto.jarListFile.trim());
    }

    private static String resolveOptionalExitCodeExpr(File baseDir, FlowBodyDto flow) throws IOException {
        if (flow.exitCodeExpr == null || flow.exitCodeExpr.isBlank()) {
            return null;
        }
        return ExitCodeExprSource.expand(flow.exitCodeExpr.trim(), baseDir);
    }

    private static void rejectLegacyRootPipelineFields(Dto dto) throws IOException {
        boolean legacy = (dto.contextFile != null && !dto.contextFile.isBlank())
                || (dto.scriptFiles != null && !dto.scriptFiles.isEmpty())
                || (dto.exitCodeExpr != null && !dto.exitCodeExpr.isBlank());
        if (legacy) {
            throw new IOException(
                    "Execution config must define contextFile, scriptFiles, and exitCodeExpr under 'flows:<id>:'; root-level contextFile/scriptFiles/exitCodeExpr are not supported");
        }
    }

    private static File resolvePath(File baseDir, String path) {
        File f = new File(path);
        return f.isAbsolute() ? f : new File(baseDir, path);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @SuppressWarnings("java:S1104") // Jackson YAML binding DTO; public fields intentional
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
    @SuppressWarnings("java:S1104") // Jackson YAML binding DTO; public fields intentional
    static final class FlowBodyDto {
        public String contextFile;
        public List<String> scriptFiles;
        public String exitCodeExpr;
    }
}
