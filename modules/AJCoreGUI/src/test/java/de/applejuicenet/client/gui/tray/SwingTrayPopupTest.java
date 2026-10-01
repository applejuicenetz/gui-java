package de.applejuicenet.client.gui.tray;

import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeFalse;

public class SwingTrayPopupTest {
    @Before public void requireDisplay() { assumeFalse(GraphicsEnvironment.isHeadless()); }

    @Test public void popupUsesVisibleOwnerWithoutShowingMainWindow() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JFrame mainWindow = new JFrame();
            JPopupMenu popup = new JPopupMenu();
            popup.add(new JMenuItem("Optionen"));
            try (SwingTrayPopup trayPopup = new SwingTrayPopup(popup)) {
                trayPopup.show(100, 200);
                assertTrue(popup.isVisible());
                assertTrue(popup.getInvoker() instanceof JDialog);
                assertTrue(popup.getInvoker().isShowing());
                assertEquals(100, popup.getInvoker().getX());
                assertEquals(200, popup.getInvoker().getY());
                assertFalse(mainWindow.isVisible());
                popup.setVisible(false);
                assertFalse(popup.getInvoker().isVisible());
                trayPopup.show(150, 250);
                assertTrue(popup.isVisible());
            } finally {
                mainWindow.dispose();
            }
            assertFalse(popup.isVisible());
            assertFalse(((Window) popup.getInvoker()).isDisplayable());
        });
    }

    @Test public void repeatedPopupRequestsKeepMenuOpenAndActionsWork() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            AtomicInteger selected = new AtomicInteger();
            JPopupMenu popup = new JPopupMenu();
            JMenuItem item = new JMenuItem("Optionen");
            item.addActionListener(event -> selected.incrementAndGet());
            popup.add(item);
            try (SwingTrayPopup trayPopup = new SwingTrayPopup(popup)) {
                trayPopup.show(100, 200);
                trayPopup.show(100, 200);
                assertTrue(popup.isVisible());
                item.doClick();
                assertEquals(1, selected.get());
                MenuSelectionManager.defaultManager().clearSelectedPath();
                assertFalse(popup.isVisible());
                assertFalse(popup.getInvoker().isVisible());
            }
        });
    }
}
