package de.applejuicenet.client.gui.components;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JPanel;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;

public class AJPanel extends JPanel {
    public AJPanel() {
    }

    public AJPanel(BorderLayout borderLayout) {
        super(borderLayout);
    }

    public AJPanel(FlowLayout flowLayout) {
        super(flowLayout);
    }
    public void setToolTipText(String text) {
        if (OptionsManagerImpl.getInstance().getSettings().isToolTipEnabled()) {
            super.setToolTipText(text);
        }
    }

}
