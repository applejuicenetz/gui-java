package de.applejuicenet.client.gui.components;

import javax.swing.JSplitPane;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;

public class AJSplitPane extends JSplitPane {
    public AJSplitPane() {
    }

    public void setToolTipText(String text) {
        if (OptionsManagerImpl.getInstance().getSettings().isToolTipEnabled()) {
            super.setToolTipText(text);
        }
    }
}
