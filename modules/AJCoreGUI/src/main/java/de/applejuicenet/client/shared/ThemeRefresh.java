package de.applejuicenet.client.shared;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.awt.Window;

/**
 * Reinstalls the current look and feel on a whole window.
 * <p>
 * Swing alone is not enough: JGoodies Plastic adds its own arrow button to every combo box, and that
 * button survives the look and feel change as a child of the combo box. The new UI never fills its
 * {@code editorInsets} field, so painting it throws a NullPointerException, which aborts the UI
 * installation and leaves the root pane half installed (later switches then fail in
 * {@code FlatRootPaneUI.uninstallClientDecorations}). Dropping the children first lets the new UI
 * install its own parts.
 * <p>
 * Popup menus are not part of the window tree and are refreshed separately.
 */
public final class ThemeRefresh {
    private static final Logger logger = LoggerFactory.getLogger(ThemeRefresh.class);

    private ThemeRefresh() {
    }

    public static void refresh(Window window) {
        removeStaleComboBoxButtons(window);
        try {
            SwingUtilities.updateComponentTreeUI(window);
        } catch (RuntimeException | LinkageError failure) {
            logger.error("Cannot reinstall the look and feel on the main window", failure);
        }
        PopupMenuSupport.updateAll();
    }

    private static void removeStaleComboBoxButtons(Component component) {
        if (component instanceof JComboBox) {
            ((JComboBox<?>) component).removeAll();
        }
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                removeStaleComboBoxButtons(child);
            }
        }
    }
}
