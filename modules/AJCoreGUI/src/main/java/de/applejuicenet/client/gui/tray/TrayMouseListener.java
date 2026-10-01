package de.applejuicenet.client.gui.tray;

import javax.swing.SwingUtilities;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

final class TrayMouseListener extends MouseAdapter {
    private final Runnable toggleWindow;
    private final Consumer<MouseEvent> showPopup;
    private boolean popupGesture;

    TrayMouseListener(Runnable toggleWindow, Consumer<MouseEvent> showPopup) {
        this.toggleWindow = toggleWindow;
        this.showPopup = showPopup;
    }

    @Override public void mousePressed(MouseEvent event) {
        popupGesture = false;
        handlePopup(event);
    }

    @Override public void mouseReleased(MouseEvent event) { handlePopup(event); }

    @Override public void mouseClicked(MouseEvent event) {
        if (!popupGesture && !event.isPopupTrigger() && SwingUtilities.isLeftMouseButton(event)
                && event.getClickCount() == 1) SwingUtilities.invokeLater(toggleWindow);
    }

    private void handlePopup(MouseEvent event) {
        if (!popupGesture && (event.isPopupTrigger() || SwingUtilities.isRightMouseButton(event))) {
            popupGesture = true;
            SwingUtilities.invokeLater(() -> showPopup.accept(event));
        }
    }
}
