package de.applejuicenet.client.gui.components;

import javax.swing.JScrollPane;

import de.applejuicenet.client.gui.tables.share.ShareTable;
import de.applejuicenet.client.gui.trees.share.DirectoryTree;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;

public class AJScrollPane extends JScrollPane {
    public AJScrollPane() {
    }

    public AJScrollPane(ShareTable shareTable) {
        super(shareTable);
    }

    public AJScrollPane(DirectoryTree directoryTree) {
        super(directoryTree);
    }

    public void setToolTipText(String text) {
        if (OptionsManagerImpl.getInstance().getSettings().isToolTipEnabled()) {
            super.setToolTipText(text);
        }
    }
}
