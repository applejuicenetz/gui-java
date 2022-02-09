package de.applejuicenet.client.gui.share.table;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.Serializable;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/share/table/DragShareNode.java,v 1.2 2004/11/22 16:25:26 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 *
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
