package de.applejuicenet.client.gui.components;

import javax.swing.JTree;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;

public class AJTree extends JTree {
    public AJTree() {
    }

    public void setToolTipText(String text) {
        if (OptionsManagerImpl.getInstance().getSettings().isToolTipEnabled()) {
            super.setToolTipText(text);
        }
    }
}
