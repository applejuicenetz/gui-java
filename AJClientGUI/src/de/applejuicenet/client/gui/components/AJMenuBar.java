package de.applejuicenet.client.gui.components;

import javax.swing.JMenuBar;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;

public class AJMenuBar extends JMenuBar {
    public AJMenuBar() {
    }
    public void setToolTipText(String text) {
        if (OptionsManagerImpl.getInstance().getSettings().isToolTipEnabled()) {
            super.setToolTipText(text);
        }
    }

}
