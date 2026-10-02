package de.applejuicenet.client.gui.tray;

import io.github.red171.libtray.TrayMenu;
import io.github.red171.libtray.TrayMenuItem;

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
        for (TrayMenuItem item : menu.items()) {
            if (item instanceof TrayMenuItem.Standard standard) {
                MenuItem nativeItem = new MenuItem(standard.label());
                nativeItem.setEnabled(standard.enabled());
                nativeItem.addActionListener(event -> actions.select(standard.id()));
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
