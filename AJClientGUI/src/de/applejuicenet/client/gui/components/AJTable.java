package de.applejuicenet.client.gui.components;

import javax.swing.JTable;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;

public class AJTable extends JTable {
    public AJTable() {
    }

    public void setToolTipText(String text) {
        if (OptionsManagerImpl.getInstance().getSettings().isToolTipEnabled()) {
            super.setToolTipText(text);
        }
    }
}
