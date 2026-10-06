package de.applejuicenet.client.shared;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.stream.Stream;

import static org.junit.Assert.*;

/** Die Plugin-Loader duerfen nach dem Einlesen kein JAR-Handle offen halten. */
public class JarLoaderResourceTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File jarWith(String name, String... entries) throws IOException {
        File jar = folder.newFile(name);
        try (JarOutputStream out = new JarOutputStream(Files.newOutputStream(jar.toPath()))) {
            for (int i = 0; i < entries.length; i += 2) {
                out.putNextEntry(new JarEntry(entries[i]));
                out.write(entries[i + 1].getBytes());
                out.closeEntry();
            }
        }
        return jar;
    }

    private static long openHandlesOn(File file) throws IOException {
        Path self = Path.of("/proc/self/fd");
        if (!Files.isDirectory(self)) {
            return -1;
        }
        long count = 0;
        try (Stream<Path> fds = Files.list(self)) {
            for (Path fd : (Iterable<Path>) fds::iterator) {
                try {
                    if (Files.readSymbolicLink(fd).equals(file.toPath().toRealPath())) {
                        count++;
                    }
                } catch (IOException ignored) {
                    // Handle wurde zwischenzeitlich geschlossen
                }
            }
        }
        return count;
    }

    @Test
    public void pluginLoaderClosesTheJarEvenWhenThePluginIsInvalid() throws Exception {
        org.junit.Assume.assumeTrue("nur mit /proc", Files.isDirectory(Path.of("/proc/self/fd")));
        File jar = jarWith("kaputt.jar", "plugin.properties", "general.classname=gibt.es.nicht\n");
        long before = openHandlesOn(jar);
        for (int i = 0; i < 20; i++) {
            assertNull(new PluginJarClassLoader().getPlugin(jar));
        }
        assertEquals("JAR-Handle nach dem Laden noch offen", before, openHandlesOn(jar));
        assertEquals("JAR-Handle nach dem Laden noch offen", before, openHandlesOn(jar));
    }

    @Test
    public void pluginLoaderSurvivesManyLoadsWithoutLeakingHandles() throws Exception {
        org.junit.Assume.assumeTrue("nur mit /proc", Files.isDirectory(Path.of("/proc/self/fd")));
        File jar = jarWith("viele.jar", "plugin.properties", "general.classname=x.Y\n", "language_deutsch.properties", "language=deutsch\n");
        long handlesBefore;
        try (Stream<Path> fds = Files.list(Path.of("/proc/self/fd"))) {
            handlesBefore = fds.count();
        }
        for (int i = 0; i < 200; i++) {
            new PluginJarClassLoader().getPlugin(jar);
        }
        // kein System.gc(): ein vergessenes close() soll sichtbar bleiben
        long handlesAfter;
        try (Stream<Path> fds = Files.list(Path.of("/proc/self/fd"))) {
            handlesAfter = fds.count();
        }
        assertTrue("Dateihandles gewachsen: " + handlesBefore + " -> " + handlesAfter, handlesAfter - handlesBefore < 20);
    }
}
