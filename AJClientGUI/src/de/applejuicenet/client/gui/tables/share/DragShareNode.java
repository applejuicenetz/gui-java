package de.applejuicenet.client.gui.tables.share;

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/share/Attic/DragShareNode.java,v 1.1 2003/08/27 16:44:42 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DragShareNode.java,v $
 * Revision 1.1  2003/08/27 16:44:42  maj0r
 * Unterstuetzung fuer DragNDrop teilweise eingebaut.
 *
 *
 */

public class DragShareNode implements Transferable {

    private Object[] nodes;

    public DragShareNode(Object[] shareNode) {
        this.nodes = shareNode;
    }

    public Object getTransferData(DataFlavor flavor) {
        return nodes;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, "ShareNodesTransferer")};
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        if (flavor.equals(new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, "ShareNodesTransferer")))
        {
            return true;
        }
        return false;
    }
}
