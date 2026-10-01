package de.applejuicenet.client.gui.tray;

import javax.swing.JDialog;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.Frame;
import java.awt.Window;

final class SwingTrayPopup implements AutoCloseable {
    private final JPopupMenu popup;
    private JDialog owner;
    private final PopupMenuListener visibilityListener = new PopupMenuListener() {
        @Override public void popupMenuWillBecomeVisible(PopupMenuEvent event) {}
        @Override public void popupMenuWillBecomeInvisible(PopupMenuEvent event) { hideOwner(); }
        @Override public void popupMenuCanceled(PopupMenuEvent event) { hideOwner(); }
    };

    SwingTrayPopup(JPopupMenu popup) {
        this.popup = popup;
        popup.addPopupMenuListener(visibilityListener);
    }

    void show(int screenX, int screenY) {
        if (popup.isVisible()) return;
        if (owner == null) {
            owner = new JDialog((Frame) null);
            owner.setUndecorated(true);
            owner.setType(Window.Type.POPUP);
            owner.setAlwaysOnTop(true);
            owner.setSize(1, 1);
        }
        owner.setLocation(screenX, screenY);
        owner.setVisible(true);
        popup.show(owner, 0, 0);
    }

    private void hideOwner() {
        if (owner != null) owner.setVisible(false);
    }

    @Override public void close() {
        popup.setVisible(false);
        popup.removePopupMenuListener(visibilityListener);
        if (owner != null) owner.dispose();
        owner = null;
    }
}
