package io.github.siakhooi.jexl.executor;

import java.io.File;
import java.io.IOException;

/**
 * Resolves an exit-code expression string: either inline JEXL, or {@code @file:&lt;path&gt;} to load script text from disk.
 * Relative paths resolve against {@code baseDirForRelativePaths} when non-null, otherwise against the JVM working directory.
 */
public final class ExitCodeExprSource {

    static final String FILE_PREFIX = "@file:";

    private ExitCodeExprSource() {
    }

    /**
     * @param expr trimmed or untrimmed expression; may be {@code null}
     * @param baseDirForRelativePaths directory for relative {@code @file:} paths, or {@code null} for current working directory
     * @return inline JEXL source to evaluate, or {@code null} if {@code expr} is null or blank
     * @throws IllegalArgumentException if {@code @file:} is used with a blank path
     * @throws IOException if the file cannot be read
     */
    public static String expand(String expr, File baseDirForRelativePaths) throws IOException {
        if (expr == null || expr.isBlank()) {
            return null;
        }
        String trimmed = expr.trim();
        if (!trimmed.startsWith(FILE_PREFIX)) {
            return trimmed;
        }
        String path = trimmed.substring(FILE_PREFIX.length()).trim();
        if (path.isEmpty()) {
            throw new IllegalArgumentException("exit code expression @file: requires a non-blank path after the prefix");
        }
        File file = new File(path);
        if (!file.isAbsolute()) {
            file = baseDirForRelativePaths != null ? new File(baseDirForRelativePaths, path) : new File(path);
        }
        return InputFile.readFile(file);
    }
}
