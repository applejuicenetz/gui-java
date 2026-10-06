package de.applejuicenet.client.gui.options;

import org.junit.Test;

import javax.swing.*;

import static org.junit.Assert.*;

public class ODAnsichtPanelTest {
    @Test
    public void panelHasContentAndTitle() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            ODAnsichtPanel panel = new ODAnsichtPanel();
            assertTrue(panel.getComponentCount() > 0);
            assertNotNull(panel.getMenuText());
        });
    }
}
