package de.applejuicenet.client.gui;

import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Window;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;

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

    public static void alignRight(Window dialog) {
        Component reference = getReference(dialog.getOwner());
        if (reference == null) {
            center(dialog);
            return;
        }
        Rectangle screen = new Rectangle(reference.getGraphicsConfiguration().getBounds());
        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(reference.getGraphicsConfiguration());
        screen.x += insets.left;
        screen.y += insets.top;
        screen.width -= insets.left + insets.right;
        screen.height -= insets.top + insets.bottom;
        int locationX = reference.getX() + reference.getWidth() - dialog.getWidth();
        int locationY = reference.getY() + (reference.getHeight() - dialog.getHeight()) / 2;
        dialog.setLocation(Math.max(screen.x, Math.min(locationX, screen.x + screen.width - dialog.getWidth())),
                Math.max(screen.y, Math.min(locationY, screen.y + screen.height - dialog.getHeight())));
    }
}
