package de.applejuicenet.client.gui.components;

import javax.swing.JComboBox;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;

public class AJComboBox extends JComboBox {
    public AJComboBox() {
    }
    public void setToolTipText(String text) {
        if (OptionsManagerImpl.getInstance().getSettings().isToolTipEnabled()) {
            super.setToolTipText(text);
        }
    }

}
