package de.applejuicenet.client.gui.components;

import javax.swing.JSlider;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;

public class AJSlider extends JSlider {
    public AJSlider() {
    }

    public void setToolTipText(String text) {
        if (OptionsManagerImpl.getInstance().getSettings().isToolTipEnabled()) {
            super.setToolTipText(text);
        }
    }
}
