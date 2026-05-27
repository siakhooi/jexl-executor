package io.github.siakhooi.jexl.executor;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;

class JarListFileResolverTest {

    @Test
    void resolve_cliOnly_returnsCli() {
        File cli = new File("/cli/jars.txt");

        assertSame(cli, JarListFileResolver.resolve(cli, null));
    }

    @Test
    void resolve_yamlOnly_returnsYaml() {
        File yaml = new File("/yaml/jars.txt");

        assertSame(yaml, JarListFileResolver.resolve(null, yaml));
    }

    @Test
    void resolve_neither_returnsNull() {
        assertNull(JarListFileResolver.resolve(null, null));
    }

    @Test
    void resolve_bothNonNull_throwsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> JarListFileResolver.resolve(new File("/a.txt"), new File("/b.txt")));
        assertTrue(ex.getMessage().contains("not both"));
    }
}
