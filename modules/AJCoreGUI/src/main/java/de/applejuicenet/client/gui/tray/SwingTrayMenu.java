package de.applejuicenet.client.gui.tray;

import io.github.red171.libtray.TrayMenu;
import io.github.red171.libtray.TrayMenuItem;

import javax.swing.*;
import java.awt.Component;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/** Keeps the native menu in sync with existing Swing actions and translations. */
final class SwingTrayMenu implements AutoCloseable {
    private final JPopupMenu popup;
    private final Consumer<TrayMenu> changed;
    private final Map<JMenuItem, String> ids = new IdentityHashMap<>();
    private final Map<String, JMenuItem> actions = new LinkedHashMap<>();
    private int nextId;
    private boolean closed;
    private final PropertyChangeListener propertyListener = event -> {
        if ("text".equals(event.getPropertyName()) || "enabled".equals(event.getPropertyName())) refresh();
    };
    private final ContainerAdapter containerListener = new ContainerAdapter() {
        @Override public void componentAdded(ContainerEvent event) { watch(event.getChild()); refresh(); }
        @Override public void componentRemoved(ContainerEvent event) {
            if (event.getChild() instanceof JMenuItem item) {
                item.removePropertyChangeListener(propertyListener);
                ids.remove(item);
            }
            refresh();
        }
    };

    SwingTrayMenu(JPopupMenu popup, Consumer<TrayMenu> changed) {
        this.popup = popup;
        this.changed = changed;
        for (Component component : popup.getComponents()) watch(component);
        popup.addContainerListener(containerListener);
    }

    private void watch(Component component) {
        if (component instanceof JMenuItem item) {
            ids.put(item, "item-" + nextId++);
            item.addPropertyChangeListener(propertyListener);
        }
    }

    TrayMenu snapshot() {
        ArrayList<TrayMenuItem> items = new ArrayList<>();
        actions.clear();
        for (Component component : popup.getComponents()) {
            if (component instanceof JMenuItem item) {
                String id = ids.get(item);
                actions.put(id, item);
                items.add(new TrayMenuItem.Standard(id, item.getText(), item.isEnabled()));
            } else if (component instanceof JSeparator) {
                items.add(TrayMenuItem.Separator.INSTANCE);
            }
        }
        return new TrayMenu(items);
    }

    void select(String id) {
        SwingUtilities.invokeLater(() -> {
            if (closed) return;
            JMenuItem item = actions.get(id);
            if (item == null || !item.isEnabled()) return;
            item.doClick();
        });
    }

    private void refresh() {
        if (closed) return;
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::refresh);
            return;
        }
        changed.accept(snapshot());
    }

    @Override public void close() {
        closed = true;
        popup.removeContainerListener(containerListener);
        for (JMenuItem item : ids.keySet()) item.removePropertyChangeListener(propertyListener);
        ids.clear();
        actions.clear();
    }
}
