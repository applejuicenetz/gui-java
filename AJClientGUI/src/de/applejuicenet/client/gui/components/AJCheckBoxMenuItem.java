package de.applejuicenet.client.gui.components;

import javax.swing.JCheckBoxMenuItem;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;

public class AJCheckBoxMenuItem extends JCheckBoxMenuItem {
    public AJCheckBoxMenuItem() {
    }
    public void setToolTipText(String text) {
        if (OptionsManagerImpl.getInstance().getSettings().isToolTipEnabled()) {
            super.setToolTipText(text);
        }
    }

}
