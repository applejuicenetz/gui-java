package de.applejuicenet.client.gui.components;

import javax.swing.JList;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;

public class AJList extends JList {
    public AJList() {
    }
    public void setToolTipText(String text) {
        if (OptionsManagerImpl.getInstance().getSettings().isToolTipEnabled()) {
            super.setToolTipText(text);
        }
    }

}
