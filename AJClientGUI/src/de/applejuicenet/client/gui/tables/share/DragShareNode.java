package de.applejuicenet.client.gui.tables.share;

import java.io.Serializable;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/share/Attic/DragShareNode.java,v 1.4 2004/02/05 23:11:28 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DragShareNode.java,v $
 * Revision 1.4  2004/02/05 23:11:28  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.3  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.2  2003/08/28 06:11:02  maj0r
 * DragNDrop vervollstaendigt.
 *
 * Revision 1.1  2003/08/27 16:44:42  maj0r
 * Unterstuetzung fuer DragNDrop teilweise eingebaut.
 *
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
