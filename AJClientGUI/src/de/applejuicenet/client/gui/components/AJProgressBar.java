package de.applejuicenet.client.gui.components;

import javax.swing.JProgressBar;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;

public class AJProgressBar extends JProgressBar {
    public AJProgressBar() {
    }

    public void setToolTipText(String text) {
        if (OptionsManagerImpl.getInstance().getSettings().isToolTipEnabled()) {
            super.setToolTipText(text);
        }
    }
}
