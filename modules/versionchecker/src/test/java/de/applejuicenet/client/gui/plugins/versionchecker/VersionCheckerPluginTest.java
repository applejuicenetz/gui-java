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
}
