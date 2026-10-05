package de.applejuicenet.client.gui.plugins.versionchecker;

import org.junit.Test;
import javax.swing.*;
import java.util.Map;
import java.util.Properties;
import de.applejuicenet.client.fassade.listener.DataUpdateListener.DATALISTENER_TYPE;
import de.applejuicenet.client.fassade.listener.CoreStatusListener.STATUS;
import static org.junit.Assert.*;

public class VersionCheckerPluginTest {
    @Test public void acceptsGenericMapsAndHandlesLifecycleWithoutCore() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            var plugin = new VersionCheckerPlugin(new Properties(), Map.of(), new ImageIcon(), Map.of(), null);
            plugin.fireContentChanged(DATALISTENER_TYPE.DOWNLOAD_CHANGED, Map.of());
            plugin.fireContentChanged(DATALISTENER_TYPE.UPLOAD_CHANGED, Map.of());
            plugin.fireStatusChanged(STATUS.CLOSED);
            plugin.registerSelected();
            assertEquals(1, plugin.getComponentCount());
        });
    }

    @Test public void changesOpenPluginControlsToEnglishAndBack() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            var selector = de.applejuicenet.client.gui.controller.LanguageSelector.getInstance(
                    java.nio.file.Paths.get("../../resources/language/deutsch.properties").toAbsolutePath().normalize().toString());
            var plugin = new VersionCheckerPlugin(new Properties(), Map.of(), new ImageIcon(), Map.of(), null);
            de.applejuicenet.client.gui.controller.LanguageSelector.getInstance(
                    java.nio.file.Paths.get("../../resources/language/english.properties").toAbsolutePath().normalize().toString());
            plugin.fireLanguageChanged();
            assertTrue(containsText(plugin, "Reset statistics"));
            de.applejuicenet.client.gui.controller.LanguageSelector.getInstance(
                    java.nio.file.Paths.get("../../resources/language/deutsch.properties").toAbsolutePath().normalize().toString());
            assertFalse(containsText(plugin, "Reset statistics"));
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
