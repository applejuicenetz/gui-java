package de.applejuicenet.client.gui.components;

import javax.swing.JLayeredPane;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;

public class AJLayeredPane extends JLayeredPane {
    public AJLayeredPane() {
    }
    public void setToolTipText(String text) {
        if (OptionsManagerImpl.getInstance().getSettings().isToolTipEnabled()) {
            super.setToolTipText(text);
        }
    }

}
