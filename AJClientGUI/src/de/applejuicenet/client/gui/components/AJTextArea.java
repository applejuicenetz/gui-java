package de.applejuicenet.client.gui.components;

import javax.swing.JTextArea;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;

public class AJTextArea extends JTextArea {
    public AJTextArea() {
    }

    public void setToolTipText(String text) {
        if (OptionsManagerImpl.getInstance().getSettings().isToolTipEnabled()) {
            super.setToolTipText(text);
        }
    }
}
