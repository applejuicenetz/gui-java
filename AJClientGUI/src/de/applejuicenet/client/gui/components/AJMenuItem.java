package de.applejuicenet.client.gui.components;

import javax.swing.JMenuItem;

import de.applejuicenet.client.gui.controller.OptionsManagerImpl;

public class AJMenuItem extends JMenuItem {
    public AJMenuItem() {
    }

    public void setToolTipText(String text) {
        if (OptionsManagerImpl.getInstance().getSettings().isToolTipEnabled()) {
            super.setToolTipText(text);
        }
    }

    public AJMenuItem(String string) {
        super(string);
    }
}
