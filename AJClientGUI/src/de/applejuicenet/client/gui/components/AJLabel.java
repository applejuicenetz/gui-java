package de.applejuicenet.client.gui.components;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;

public class AJLabel extends JLabel {
    public AJLabel() {
    }

    public AJLabel(ImageIcon imageIcon) {
        super(imageIcon);
    }
    public void setToolTipText(String text) {
        if (OptionsManagerImpl.getInstance().getSettings().isToolTipEnabled()) {
            super.setToolTipText(text);
        }
    }

}
