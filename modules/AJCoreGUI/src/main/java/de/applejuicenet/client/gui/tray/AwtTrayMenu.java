package de.applejuicenet.client.gui.tray;

import dev.hivens.libtray.TrayMenu;
import dev.hivens.libtray.TrayMenuItem;

import javax.swing.JPopupMenu;
import java.awt.MenuItem;
import java.awt.PopupMenu;

final class AwtTrayMenu implements AutoCloseable {
    private final PopupMenu popup = new PopupMenu();
    private final SwingTrayMenu actions;

    AwtTrayMenu(JPopupMenu source) {
        actions = new SwingTrayMenu(source, this::update);
        update(actions.snapshot());
    }

    PopupMenu getPopupMenu() {
        return popup;
    }

    private void update(TrayMenu menu) {
        popup.removeAll();
        for (TrayMenuItem item : menu.getItems()) {
            if (item instanceof TrayMenuItem.Standard standard) {
                MenuItem nativeItem = new MenuItem(standard.getLabel());
                nativeItem.setEnabled(standard.getEnabled());
                nativeItem.addActionListener(event -> actions.select(standard.getId()));
                popup.add(nativeItem);
            } else if (item instanceof TrayMenuItem.Separator) {
                popup.addSeparator();
            }
        }
    }

    @Override
    public void close() {
        actions.close();
        popup.removeAll();
    }
}
