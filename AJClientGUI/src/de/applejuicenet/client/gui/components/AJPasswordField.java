package de.applejuicenet.client.gui.components;

import javax.swing.JPasswordField;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;

public class AJPasswordField extends JPasswordField {
    public AJPasswordField() {
    }
    public void setToolTipText(String text) {
        if (OptionsManagerImpl.getInstance().getSettings().isToolTipEnabled()) {
            super.setToolTipText(text);
        }
    }

}
