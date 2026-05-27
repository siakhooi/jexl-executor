package io.github.siakhooi.jexl.executor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * Loads a {@link FlowFileSpec} from a YAML file. Relative paths are resolved against the YAML file's directory.
 *
 * <p>Expected shape:
 *
 * <pre>
 * contextFile: context.json
 * scriptFiles:
 *   - step1.jexl
 *   - step2.json
 * resultPathTemplate: "{name}"   # optional, defaults to {name}
 * jarListFile: jars.txt          # optional; same format as --jarfile (mutually exclusive with --jarfile/-j)
 * exitCodeExpr: "status"         # optional JEXL on final context for process exit (--flow-spec mode only; no --exit-code-expr/-e)
 * </pre>
 */
public final class FlowFileSpecYaml {

    private static final String DEFAULT_RESULT_PATH_TEMPLATE = "{name}";

    private FlowFileSpecYaml() {
    }

    public static FlowFileSpec load(File yamlFile) throws IOException {
        Objects.requireNonNull(yamlFile, "yamlFile");
        File baseDir = yamlFile.getAbsoluteFile().getParentFile();
        if (baseDir == null) {
            baseDir = new File(".").getAbsoluteFile();
        }

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        Dto dto = mapper.readValue(yamlFile, Dto.class);

        if (dto.contextFile == null || dto.contextFile.isBlank()) {
            throw new IOException("YAML flow spec must set non-blank 'contextFile'");
        }
        if (dto.scriptFiles == null || dto.scriptFiles.isEmpty()) {
            throw new IOException("YAML flow spec must set non-empty 'scriptFiles'");
        }

        File context = resolvePath(baseDir, dto.contextFile.trim());
        List<File> scripts = new ArrayList<>(dto.scriptFiles.size());
        for (String path : dto.scriptFiles) {
            if (path == null || path.isBlank()) {
                throw new IOException("YAML flow spec 'scriptFiles' entries must be non-blank");
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
        if (dto.exitCodeExpr != null && !dto.exitCodeExpr.isBlank()) {
            exitCodeExpr = dto.exitCodeExpr.trim();
        }

        return new FlowFileSpec(context, scripts, template, jarList, exitCodeExpr);
    }

    private static File resolvePath(File baseDir, String path) {
        File f = new File(path);
        return f.isAbsolute() ? f : new File(baseDir, path);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static final class Dto {
        public String contextFile;
        public List<String> scriptFiles;
        public String resultPathTemplate;
        public String jarListFile;
        public String exitCodeExpr;
    }
}
