package de.applejuicenet.client.gui.components;

import javax.swing.JPopupMenu;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;

public class AJPopupMenu extends JPopupMenu {
    public AJPopupMenu() {
    }

    public void setToolTipText(String text) {
        if (OptionsManagerImpl.getInstance().getSettings().isToolTipEnabled()) {
            super.setToolTipText(text);
        }
    }
}
