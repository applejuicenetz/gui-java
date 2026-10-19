package de.applejuicenet.client.gui.options;

import org.junit.Test;

import javax.swing.*;

import static org.junit.Assert.*;

public class ODViewPanelTest {
    @Test
    public void panelHasContentAndTitle() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            ODViewPanel panel = new ODViewPanel();
            assertTrue(panel.getComponentCount() > 0);
            assertNotNull(panel.getMenuText());
        });
    }
}
