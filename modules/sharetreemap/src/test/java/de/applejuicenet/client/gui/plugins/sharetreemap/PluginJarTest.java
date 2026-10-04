package de.applejuicenet.client.gui.plugins.sharetreemap;

import de.applejuicenet.client.shared.PluginJarClassLoader;
import org.junit.Test;
import java.io.File;
import static org.junit.Assert.*;

/** Run after package with -Dtreemap.jar=... to exercise the real GUI loader. */
public class PluginJarTest {
    @Test public void loadsPackagedPluginUsingGuiClassLoader() throws Exception {
        String path = System.getProperty("treemap.jar");
        org.junit.Assume.assumeNotNull(path);
        var plugin = new PluginJarClassLoader().getPlugin(new File(path));
        assertNotNull(plugin);
        assertEquals("Share-Treemap", plugin.getTitle());
        assertTrue(plugin.istReiter());
    }
}
