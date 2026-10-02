package de.applejuicenet.client.gui.tray;

import io.github.red171.libtray.TrayMenu;
import org.junit.Test;
import javax.swing.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.Assert.*;

public class SwingTrayMenuTest {
    @Test public void nativeSelectionRunsSwingActionOnEdt() throws Exception {
        AtomicBoolean called = new AtomicBoolean();
        SwingUtilities.invokeAndWait(() -> {
            JPopupMenu popup = new JPopupMenu();
            JMenuItem item = new JMenuItem("Optionen");
            item.addActionListener(event -> called.set(SwingUtilities.isEventDispatchThread()));
            popup.add(item);
            SwingTrayMenu menu = new SwingTrayMenu(popup, changed -> {});
            menu.select(menu.snapshot().items().get(0).id());
        });
        SwingUtilities.invokeAndWait(() -> {});
        assertTrue(called.get());
    }

    @Test public void translatedLabelsAndDisabledActionsUpdateNativeMenu() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            AtomicReference<TrayMenu> updated = new AtomicReference<>();
            AtomicBoolean called = new AtomicBoolean();
            JPopupMenu popup = new JPopupMenu();
            JMenuItem item = new JMenuItem("Anzeigen");
            item.addActionListener(event -> called.set(true));
            popup.add(item);
            try (SwingTrayMenu menu = new SwingTrayMenu(popup, updated::set)) {
                String id = menu.snapshot().items().get(0).id();
                item.setText("Show");
                assertEquals("Show", updated.get().items().get(0).label());
                assertEquals(id, updated.get().items().get(0).id());
                item.setEnabled(false);
                assertFalse(updated.get().items().get(0).enabled());
                menu.select(id);
            }
            assertFalse(called.get());
        });
    }

    @Test public void disabledNativeSelectionDoesNotRunAction() throws Exception {
        AtomicBoolean called = new AtomicBoolean();
        AtomicReference<SwingTrayMenu> menu = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> {
            JPopupMenu popup = new JPopupMenu();
            JMenuItem item = new JMenuItem("Optionen");
            item.setEnabled(false);
            item.addActionListener(event -> called.set(true));
            popup.add(item);
            menu.set(new SwingTrayMenu(popup, changed -> {}));
            menu.get().select(menu.get().snapshot().items().get(0).id());
        });
        SwingUtilities.invokeAndWait(() -> {});
        try {
            assertFalse(called.get());
        } finally {
            SwingUtilities.invokeAndWait(menu.get()::close);
        }
    }
}
