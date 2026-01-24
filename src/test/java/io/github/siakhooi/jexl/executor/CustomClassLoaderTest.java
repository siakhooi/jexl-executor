package io.github.siakhooi.jexl.executor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CustomClassLoaderTest {
    @Test
    void testLoadJarsWithValidJar(@TempDir Path tempDir) throws IOException {
        // Create a dummy JAR file
        Path jarFile = tempDir.resolve("dummy.jar");
        try (JarOutputStream jos = new JarOutputStream(Files.newOutputStream(jarFile))) {
            jos.putNextEntry(new ZipEntry("META-INF/MANIFEST.MF"));
            jos.write("Manifest-Version: 1.0\n".getBytes());
            jos.closeEntry();
        }
        // Create a jar list file
        Path jarListFile = tempDir.resolve("jars.txt");
        Files.write(jarListFile, (jarFile.toString() + System.lineSeparator()).getBytes());

        ClassLoader cl = CustomClassLoader.loadJars(jarListFile.toFile());
        assertNotNull(cl);
        assertTrue(cl instanceof URLClassLoader);
        boolean found = false;
        try (URLClassLoader ucl = (URLClassLoader) cl) {
            for (java.net.URL url : ucl.getURLs()) {
                if (url.getPath().endsWith("dummy.jar")) {
                    found = true;
                    break;
                }
            }
        }
        assertTrue(found, "dummy.jar should be loaded");
    }

    @Test
    void testLoadJarsWithNonExistentJar(@TempDir Path tempDir) throws IOException {
        // Create a jar list file with a non-existent jar
        Path jarListFile = tempDir.resolve("jars.txt");
        String nonExistent = tempDir.resolve("nope.jar").toString();
        Files.write(jarListFile, (nonExistent + System.lineSeparator()).getBytes());
        ClassLoader cl = CustomClassLoader.loadJars(jarListFile.toFile());
        assertNotNull(cl);
        assertTrue(cl instanceof URLClassLoader);
        boolean found = false;
        try (URLClassLoader ucl = (URLClassLoader) cl) {
            // Should not contain the non-existent jar
            for (java.net.URL url : ucl.getURLs()) {
                if (url.getPath().endsWith("nope.jar")) {
                    found = true;
                    break;
                }
            }
        }
        assertFalse(found, "nope.jar should not be loaded");
    }

    @Test
    void testLoadJarsWithCommentsAndBlanks(@TempDir Path tempDir) throws IOException {
        // Create a dummy JAR file
        Path jarFile = tempDir.resolve("dummy2.jar");
        try (JarOutputStream jos = new JarOutputStream(Files.newOutputStream(jarFile))) {
            jos.putNextEntry(new ZipEntry("META-INF/MANIFEST.MF"));
            jos.write("Manifest-Version: 1.0\n".getBytes());
            jos.closeEntry();
        }
        // Create a jar list file with comments and blanks
        Path jarListFile = tempDir.resolve("jars.txt");
        String content = "# comment line\n\n" + jarFile.toString() + "\n\n";
        Files.write(jarListFile, content.getBytes());
        ClassLoader cl = CustomClassLoader.loadJars(jarListFile.toFile());
        assertNotNull(cl);
        assertTrue(cl instanceof URLClassLoader);
        boolean found = false;
        try (URLClassLoader ucl = (URLClassLoader) cl) {
            for (java.net.URL url : ucl.getURLs()) {
                if (url.getPath().endsWith("dummy2.jar")) {
                    found = true;
                    break;
                }
            }
        }
        assertTrue(found, "dummy2.jar should be loaded");
    }
}
