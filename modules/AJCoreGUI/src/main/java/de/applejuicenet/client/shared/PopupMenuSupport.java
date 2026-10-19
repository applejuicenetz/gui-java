package de.applejuicenet.client.shared;

import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Creates popup menus that are refreshed when the look and feel changes.
 * <p>
 * Context menus are not part of a window's component tree, so
 * {@link SwingUtilities#updateComponentTreeUI(java.awt.Component)} on the frame leaves their UI
 * untouched. Every popup menu must be created through {@link #create()} and the caller must run
 * {@link #updateAll()} after installing a new look and feel.
 * <p>
 * Swing is single threaded: create popups and call {@link #updateAll()} on the event dispatch
 * thread only.
 */
public final class PopupMenuSupport {
    private static final Set<JPopupMenu> MENUS =
            Collections.newSetFromMap(new WeakHashMap<JPopupMenu, Boolean>());

    private PopupMenuSupport() {
    }

    public static JPopupMenu create() {
        JPopupMenu menu = new JPopupMenu();
        MENUS.add(menu);
        return menu;
    }

    /**
     * Reinstalls the current look and feel on all popup menus created through {@link #create()},
     * including their menu items.
     */
    public static void updateAll() {
        for (JPopupMenu menu : MENUS) {
            SwingUtilities.updateComponentTreeUI(menu);
        }
    }
}
