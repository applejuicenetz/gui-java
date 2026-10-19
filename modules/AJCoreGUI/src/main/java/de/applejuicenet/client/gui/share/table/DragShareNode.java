package de.applejuicenet.client.gui.share.table;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.Serializable;

/**
 * Copyright: General Public License
 */

public class DragShareNode
    implements Transferable, Serializable {

	private Object[] nodes;

    public DragShareNode(Object[] shareNode) {
        this.nodes = shareNode;
    }

    public Object getTransferData(DataFlavor flavor) {
        return nodes;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] {
            new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType,
                           "ShareNodesTransferer")};
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        if (flavor.equals(new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType,
                                         "ShareNodesTransferer"))) {
            return true;
        }
        return false;
    }
}
