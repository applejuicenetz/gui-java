package de.applejuicenet.client.gui.components;

import javax.swing.JButton;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;

public class AJButton extends JButton {
    public AJButton() {
    }

    public AJButton(String string) {
        super(string);
    }

    public void setToolTipText(String text) {
        if (OptionsManagerImpl.getInstance().getSettings().isToolTipEnabled()) {
            super.setToolTipText(text);
        }
    }
}
