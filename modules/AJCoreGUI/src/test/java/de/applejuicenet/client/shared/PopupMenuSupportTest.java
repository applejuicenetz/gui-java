package de.applejuicenet.client.shared;

import com.formdev.flatlaf.FlatLightLaf;
import org.junit.Test;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.MetalLookAndFeel;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PopupMenuSupportTest {
    @Test
    public void updateAllRefreshesRegisteredPopupMenus() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            LookAndFeel previous = UIManager.getLookAndFeel();
            try {
                UIManager.setLookAndFeel(new MetalLookAndFeel());
                JPopupMenu popup = PopupMenuSupport.create();
                JMenuItem item = new JMenuItem("Entry");
                popup.add(item);
                assertFalse("Metal popup must not use a FlatLaf UI",
                        popup.getUI().getClass().getName().startsWith("com.formdev.flatlaf"));

                UIManager.setLookAndFeel(new FlatLightLaf());
                PopupMenuSupport.updateAll();

                assertTrue("Popup menu UI was not refreshed: " + popup.getUI().getClass().getName(),
                        popup.getUI().getClass().getName().startsWith("com.formdev.flatlaf"));
                assertTrue("Menu item UI was not refreshed: " + item.getUI().getClass().getName(),
                        item.getUI().getClass().getName().startsWith("com.formdev.flatlaf"));
            } catch (Exception failure) {
                throw new AssertionError(failure);
            } finally {
                try {
                    UIManager.setLookAndFeel(previous);
                } catch (UnsupportedLookAndFeelException failure) {
                    throw new AssertionError(failure);
                }
            }
        });
    }
}
