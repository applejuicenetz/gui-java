package de.applejuicenet.client.gui.plugins;

import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Plugin metadata: English default in plugin.properties, one language file per supported language. */
public class PluginMetadataTest {
    private static final Path MODULES = Paths.get("..").toAbsolutePath().normalize();

    private static Properties load(Path file) throws IOException {
        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            properties.load(reader);
        }
        return properties;
    }

    private static List<Path> pluginResources() throws IOException {
        try (Stream<Path> modules = Files.list(MODULES)) {
            return modules.map(m -> m.resolve("src/main/resources"))
                    .filter(r -> Files.isRegularFile(r.resolve("plugin.properties")))
                    .sorted().collect(Collectors.toList());
        }
    }

    @Test
    public void everyPluginHasAnEnglishDefaultDescription() throws IOException {
        List<Path> plugins = pluginResources();
        assertTrue("expected the four bundled plugins", plugins.size() >= 4);
        for (Path resources : plugins) {
            String description = load(resources.resolve("plugin.properties")).getProperty("general.description");
            assertTrue(resources + " has no description", description != null && !description.isBlank());
            assertFalse(resources + " description is not English: " + description,
                    description.matches("(?s).*\\b(der|die|das|und|nicht|zeigt|mit)\\b.*"));
        }
    }

    @Test
    public void everyPluginHasGermanAndEnglishLanguageFiles() throws IOException {
        for (Path resources : pluginResources()) {
            for (String language : new String[] {"deutsch", "english"}) {
                Path file = resources.resolve("language_" + language + ".properties");
                assertTrue(file + " missing", Files.isRegularFile(file));
                Properties properties = load(file);
                assertEquals(language, properties.getProperty("language"));
                String description = properties.getProperty("language.description");
                assertTrue(file + " has no description", description != null && !description.isBlank());
            }
        }
    }

    @Test
    public void pluginClassesAreDeclared() throws IOException {
        for (Path resources : pluginResources()) {
            String className = load(resources.resolve("plugin.properties")).getProperty("general.classname");
            assertTrue(resources + " has no general.classname", className != null && className.startsWith("de.applejuicenet."));
        }
    }
}
