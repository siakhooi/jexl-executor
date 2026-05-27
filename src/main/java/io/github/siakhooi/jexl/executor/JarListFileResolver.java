package io.github.siakhooi.jexl.executor;

import java.io.File;

/**
 * Resolves the JAR list file from CLI and YAML flow spec (at most one source).
 */
public final class JarListFileResolver {

    private JarListFileResolver() {
    }

    /**
     * @param cliJarListFile from {@code --jarfile} / {@code -j}, may be {@code null}
     * @param yamlJarListFile from YAML {@code jarListFile}, may be {@code null}
     * @return the file to use, or {@code null} if neither is set
     * @throws IllegalArgumentException if both are non-null
     */
    public static File resolve(File cliJarListFile, File yamlJarListFile) {
        if (cliJarListFile != null && yamlJarListFile != null) {
            throw new IllegalArgumentException(
                    "Specify JAR list in either the flow YAML (jarListFile) or with --jarfile/-j, not both");
        }
        return cliJarListFile != null ? cliJarListFile : yamlJarListFile;
    }
}
