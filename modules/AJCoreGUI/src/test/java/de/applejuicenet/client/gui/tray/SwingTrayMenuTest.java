package de.applejuicenet.client.gui.tray;

import dev.hivens.libtray.TrayMenu;
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
            SwingTrayMenu menu = new SwingTrayMenu(popup, () -> {}, changed -> {});
            menu.select(menu.snapshot().getItems().get(0).getId());
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
            try (SwingTrayMenu menu = new SwingTrayMenu(popup, () -> {}, updated::set)) {
                String id = menu.snapshot().getItems().get(0).getId();
                item.setText("Show");
                assertEquals("Show", updated.get().getItems().get(0).getLabel());
                assertEquals(id, updated.get().getItems().get(0).getId());
                item.setEnabled(false);
                assertFalse(updated.get().getItems().get(0).getEnabled());
                menu.select(id);
            }
            assertFalse(called.get());
        });
    }

    @Test public void sliderSubmenuUsesExistingSwingPopup() throws Exception {
        AtomicBoolean shown = new AtomicBoolean();
        SwingUtilities.invokeAndWait(() -> {
            JPopupMenu popup = new JPopupMenu();
            JMenu upload = new JMenu("Upload");
            upload.add(new JSlider());
            popup.add(upload);
            SwingTrayMenu menu = new SwingTrayMenu(popup, () -> shown.set(true), changed -> {});
            menu.select(menu.snapshot().getItems().get(0).getId());
        });
        SwingUtilities.invokeAndWait(() -> {});
        assertTrue(shown.get());
    }
}
