package de.applejuicenet.client.gui;

import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Window;

public final class DialogLocation {
    private DialogLocation() {
    }

    public static Component getReference(Component fallback) {
        AppleJuiceDialog mainWindow = AppleJuiceDialog.getApp();
        if (mainWindow != null && mainWindow.isShowing()) {
            return mainWindow;
        }
        Window window = fallback instanceof Window ? (Window) fallback
                : fallback == null ? null : SwingUtilities.getWindowAncestor(fallback);
        Window reference = null;
        while (window != null) {
            if (window.isShowing()) {
                reference = window;
            }
            window = window.getOwner();
        }
        return reference;
    }

    public static void center(Window dialog) {
        dialog.setLocationRelativeTo(getReference(dialog.getOwner()));
    }
}
