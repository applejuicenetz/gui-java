package de.applejuicenet.client.gui.components;

import javax.swing.JTextPane;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;

public class AJTextPane extends JTextPane {
    public AJTextPane() {
    }

    public void setToolTipText(String text) {
        if (OptionsManagerImpl.getInstance().getSettings().isToolTipEnabled()) {
            super.setToolTipText(text);
        }
    }
}
