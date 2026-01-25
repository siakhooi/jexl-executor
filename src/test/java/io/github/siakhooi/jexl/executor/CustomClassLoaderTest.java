package io.github.siakhooi.jexl.executor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@DisplayName("CustomClassLoader Tests")
class CustomClassLoaderTest {
    
    private boolean containsJarFile(URLClassLoader ucl, String jarName) {
        for (URL url : ucl.getURLs()) {
            if (url.getPath().endsWith(jarName)) {
                return true;
            }
        }
        return false;
    }
    
    @Test
    @DisplayName("Should load valid JAR file from list")
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
        
        try (URLClassLoader ucl = assertInstanceOf(URLClassLoader.class, cl)) {
            assertTrue(containsJarFile(ucl, "dummy.jar"), "dummy.jar should be loaded");
        }
    }

    @Test
    @DisplayName("Should skip non-existent JAR files")
    void testLoadJarsWithNonExistentJar(@TempDir Path tempDir) throws IOException {
        // Create a jar list file with a non-existent jar
        Path jarListFile = tempDir.resolve("jars.txt");
        String nonExistent = tempDir.resolve("nope.jar").toString();
        Files.write(jarListFile, (nonExistent + System.lineSeparator()).getBytes());
        
        ClassLoader cl = CustomClassLoader.loadJars(jarListFile.toFile());
        assertNotNull(cl);
        
        try (URLClassLoader ucl = assertInstanceOf(URLClassLoader.class, cl)) {
            assertFalse(containsJarFile(ucl, "nope.jar"), "nope.jar should not be loaded");
        }
    }

    @Test
    @DisplayName("Should parse JAR list ignoring comments and blank lines")
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
        
        try (URLClassLoader ucl = assertInstanceOf(URLClassLoader.class, cl)) {
            assertTrue(containsJarFile(ucl, "dummy2.jar"), "dummy2.jar should be loaded");
        }
    }
}
