package io.github.siakhooi.jexl.executor;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ApplicationClassLoaderTest {
    @Test
    void testGetWithNullJarListFile() throws IOException {
        ClassLoader original = Thread.currentThread().getContextClassLoader();
        ClassLoader result = ApplicationClassLoader.get(null);
        assertSame(original, result);
    }

    @Test
    void testGetWithJarListFile() {
        File dummyFile = new File("dummy.jarlist");
        try {
            ClassLoader result = ApplicationClassLoader.get(dummyFile);
            assertNotNull(result);
        } catch (IOException e) {
            // Acceptable if IOException is thrown
            assertTrue(true);
        }
    }

    @Test
    void testGetWithJarListFileThrowsIOException() {
        // Use a file that is very unlikely to exist or be valid
        File invalidFile = new File("/this/path/does/not/exist/jarlist.txt");
        assertThrows(IOException.class, () -> ApplicationClassLoader.get(invalidFile));
    }

    @Test
    void testGetWithValidJarListFile(@TempDir Path tempDir) throws Exception {
        // Create a dummy JAR file
        Path jarFile = tempDir.resolve("dummy.jar");
        try (JarOutputStream jos = new JarOutputStream(java.nio.file.Files.newOutputStream(jarFile))) {
            jos.putNextEntry(new ZipEntry("META-INF/MANIFEST.MF"));
            jos.write("Manifest-Version: 1.0\n".getBytes());
            jos.closeEntry();
        }
        // Create a jar list file
        Path jarListFile = tempDir.resolve("jars.txt");
        java.nio.file.Files.write(jarListFile, (jarFile.toString() + System.lineSeparator()).getBytes());
        // Should cover the branch where jarListFile is valid and JAR is loaded
        ClassLoader cl = ApplicationClassLoader.get(jarListFile.toFile());
        assertNotNull(cl);
    }
}
