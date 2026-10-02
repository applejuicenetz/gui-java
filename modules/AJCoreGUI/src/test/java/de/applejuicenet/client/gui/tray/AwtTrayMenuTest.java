package de.applejuicenet.client.gui.tray;

import org.junit.Test;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AwtTrayMenuTest {
    @Test
    public void keepsNativeLabelsEnabledStateAndSeparatorsInSync() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JPopupMenu source = new JPopupMenu();
            JMenuItem show = new JMenuItem("Zeigen");
            source.add(show);
            source.addSeparator();
            source.add(new JMenuItem("Optionen"));
            try (AwtTrayMenu menu = new AwtTrayMenu(source)) {
                assertEquals(3, menu.getPopupMenu().getItemCount());
                assertEquals("Zeigen", menu.getPopupMenu().getItem(0).getLabel());
                assertEquals("-", menu.getPopupMenu().getItem(1).getLabel());
                show.setText("Verstecken");
                assertEquals("Verstecken", menu.getPopupMenu().getItem(0).getLabel());
                show.setEnabled(false);
                assertFalse(menu.getPopupMenu().getItem(0).isEnabled());
                source.add(new JMenuItem("Über"));
                assertEquals(4, menu.getPopupMenu().getItemCount());
            }
        });
    }

    @Test
    public void nativeExitSelectionRunsSwingActionOnEdt() throws Exception {
        AtomicBoolean called = new AtomicBoolean();
        AwtTrayMenu[] menu = new AwtTrayMenu[1];
        SwingUtilities.invokeAndWait(() -> {
            JPopupMenu source = new JPopupMenu();
            JMenuItem exit = new JMenuItem("Beenden");
            exit.addActionListener(event -> called.set(SwingUtilities.isEventDispatchThread()));
            source.add(exit);
            menu[0] = new AwtTrayMenu(source);
            assertEquals("Beenden", menu[0].getPopupMenu().getItem(0).getLabel());
            select(menu[0].getPopupMenu().getItem(0));
        });
        SwingUtilities.invokeAndWait(() -> {});
        try {
            assertTrue(called.get());
        } finally {
            SwingUtilities.invokeAndWait(menu[0]::close);
        }
    }

    @Test
    public void disabledNativeSelectionDoesNotRunAction() throws Exception {
        AtomicBoolean called = new AtomicBoolean();
        AwtTrayMenu[] menu = new AwtTrayMenu[1];
        SwingUtilities.invokeAndWait(() -> {
            JPopupMenu source = new JPopupMenu();
            JMenuItem options = new JMenuItem("Optionen");
            options.setEnabled(false);
            options.addActionListener(event -> called.set(true));
            source.add(options);
            menu[0] = new AwtTrayMenu(source);
            select(menu[0].getPopupMenu().getItem(0));
        });
        SwingUtilities.invokeAndWait(() -> {});
        try {
            assertFalse(called.get());
        } finally {
            SwingUtilities.invokeAndWait(menu[0]::close);
        }
    }

    @Test
    public void closedMenuIgnoresQueuedActionsAndUpdates() throws Exception {
        AtomicBoolean called = new AtomicBoolean();
        SwingUtilities.invokeAndWait(() -> {
            JPopupMenu source = new JPopupMenu();
            JMenuItem options = new JMenuItem("Optionen");
            options.addActionListener(event -> called.set(true));
            source.add(options);
            AwtTrayMenu menu = new AwtTrayMenu(source);
            select(menu.getPopupMenu().getItem(0));
            menu.close();
            options.setText("Options");
            assertEquals(0, menu.getPopupMenu().getItemCount());
        });
        SwingUtilities.invokeAndWait(() -> {});
        assertFalse(called.get());
    }

    private void select(MenuItem item) {
        ActionEvent event = new ActionEvent(item, ActionEvent.ACTION_PERFORMED, item.getLabel());
        for (ActionListener listener : item.getActionListeners()) {
            listener.actionPerformed(event);
        }
    }
}
