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
}
