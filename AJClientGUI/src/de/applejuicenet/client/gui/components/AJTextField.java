package de.applejuicenet.client.gui.components;

import javax.swing.JTextField;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;

public class AJTextField extends JTextField {
    public AJTextField() {
    }

    public void setToolTipText(String text) {
        if (OptionsManagerImpl.getInstance().getSettings().isToolTipEnabled()) {
            super.setToolTipText(text);
        }
    }
}
