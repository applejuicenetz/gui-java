package de.applejuicenet.client.gui.plugins;

import de.applejuicenet.client.fassade.listener.DataUpdateListener.DATALISTENER_TYPE;
import de.applejuicenet.client.fassade.listener.CoreStatusListener.STATUS;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import javax.swing.*;
import java.util.Map;
import java.util.Properties;
import static org.junit.Assert.*;

public class SpeedGraphPluginTest {
    @Rule public TemporaryFolder folder = new TemporaryFolder();

    @Test public void acceptsMapEventsOnWorkerThreadAndHandlesCoreClosure() throws Exception {
        SpeedGraphPlugin[] plugin = new SpeedGraphPlugin[1];
        SwingUtilities.invokeAndWait(() -> plugin[0] = new SpeedGraphPlugin(new Properties(), Map.of(), new ImageIcon(), Map.of(), folder.getRoot().toPath().resolve("settings.properties"), null));
        plugin[0].fireContentChanged(DATALISTENER_TYPE.SPEED_CHANGED, Map.of("uploadspeed", 1024L, "downloadspeed", 1048576L));
        SwingUtilities.invokeAndWait(() -> {
            plugin[0].registerSelected();
            assertEquals("1 MiB/s", ((GraphPanel) plugin[0].getComponent(0)).downloadText());
            assertNotNull(plugin[0].getOptionPanel());
        });
        plugin[0].fireStatusChanged(STATUS.CLOSED);
        SwingUtilities.invokeAndWait(() -> assertEquals("—", ((GraphPanel) plugin[0].getComponent(0)).downloadText()));
    }

    @Test public void changesOpenPluginControlsToEnglishAndBack() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            var selector = de.applejuicenet.client.gui.controller.LanguageSelector.getInstance(
                    java.nio.file.Paths.get("../../resources/language/deutsch.properties").toAbsolutePath().normalize().toString());
            var plugin = new SpeedGraphPlugin(new Properties(), Map.of(), new ImageIcon(), Map.of(), folder.getRoot().toPath().resolve("i18n.properties"), null);
            de.applejuicenet.client.gui.controller.LanguageSelector.getInstance(
                    java.nio.file.Paths.get("../../resources/language/english.properties").toAbsolutePath().normalize().toString());
            plugin.fireLanguageChanged();
            assertTrue(containsText(plugin, "Limit lines"));
            de.applejuicenet.client.gui.controller.LanguageSelector.getInstance(
                    java.nio.file.Paths.get("../../resources/language/deutsch.properties").toAbsolutePath().normalize().toString());
            assertFalse(containsText(plugin, "Limit lines"));
        });
    }
    private static boolean containsText(java.awt.Container parent, String text) {
        for (java.awt.Component c : parent.getComponents()) {
            if (c instanceof javax.swing.AbstractButton b && text.equals(b.getText())) return true;
            if (c instanceof java.awt.Container child && containsText(child, text)) return true;
        }
        return false;
    }
}
