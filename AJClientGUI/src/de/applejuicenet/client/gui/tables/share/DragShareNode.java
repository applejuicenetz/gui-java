package de.applejuicenet.client.gui.tables.share;

import java.io.Serializable;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/share/Attic/DragShareNode.java,v 1.7 2004/10/15 13:34:48 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class DragShareNode
    implements Transferable, Serializable {

    private static final long serialVersionUID = -37424467525963520L;
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
