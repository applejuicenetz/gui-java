package de.applejuicenet.client.gui.components;

import javax.swing.JCheckBox;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;

public class AJCheckBox extends JCheckBox {
    public AJCheckBox() {
    }
    public void setToolTipText(String text) {
        if (OptionsManagerImpl.getInstance().getSettings().isToolTipEnabled()) {
            super.setToolTipText(text);
        }
    }

}
