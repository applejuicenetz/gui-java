package de.applejuicenet.client.gui.plugins.sharetreemap;

import de.applejuicenet.client.gui.plugins.PluginConnector;
import de.applejuicenet.client.fassade.listener.DataUpdateListener.DATALISTENER_TYPE;
import de.applejuicenet.client.fassade.entity.Share;
import org.junit.Test;
import javax.swing.*;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Properties;
import static org.junit.Assert.*;

public class ShareTreemapPluginTest {
    @Test public void acceptsShareEventAndRendersCoreSnapshotOnEdt() throws Exception {
        Class<?> type = Class.forName("de.applejuicenet.client.gui.plugins.sharetreemap.ShareTreemapPlugin");
        Properties props = new Properties(); props.setProperty("general.title", "Share-Treemap");
        PluginConnector plugin = (PluginConnector) type.getConstructor(Properties.class, Map.class, ImageIcon.class, Map.class)
                .newInstance(props, Map.of(), new ImageIcon(new byte[0]), Map.of());
        Share share = (Share) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Share.class}, (p, method, args) -> switch (method.getName()) {
            case "getId" -> 10;
            case "getFilename" -> "/remote/Film.mkv";
            case "getSize" -> 1024L;
            case "getAjfspLink" -> "ajfsp://test";
            default -> null;
        });
        plugin.fireContentChanged(DATALISTENER_TYPE.SHARE_CHANGED, Map.of(10, share));
        SwingUtilities.invokeAndWait(() -> {
            TreemapView view = (TreemapView) plugin.getComponent(0);
            assertEquals(1024L, view.current().bytes());
        });
    }

    @Test public void changesOpenPluginControlsToEnglishAndBack() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            var selector = de.applejuicenet.client.gui.controller.LanguageSelector.getInstance(
                    java.nio.file.Paths.get("../../resources/language/deutsch.properties").toAbsolutePath().normalize().toString());
            var plugin = new ShareTreemapPlugin(new Properties(), Map.of(), new ImageIcon(), Map.of(), null);
            de.applejuicenet.client.gui.controller.LanguageSelector.getInstance(
                    java.nio.file.Paths.get("../../resources/language/english.properties").toAbsolutePath().normalize().toString());
            plugin.fireLanguageChanged();
            assertTrue(containsText(plugin, "Back"));
            de.applejuicenet.client.gui.controller.LanguageSelector.getInstance(
                    java.nio.file.Paths.get("../../resources/language/deutsch.properties").toAbsolutePath().normalize().toString());
            assertFalse(containsText(plugin, "Back"));
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
