package de.applejuicenet.client.gui.tables.dateiliste;

import de.applejuicenet.client.gui.tables.share.ShareNode;
import de.applejuicenet.client.shared.dnd.DndTargetAdapter;

import javax.swing.*;
import java.awt.dnd.*;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/dateiliste/Attic/DateiListeTable.java,v 1.1 2003/08/27 16:44:42 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DateiListeTable.java,v $
 * Revision 1.1  2003/08/27 16:44:42  maj0r
 * Unterstuetzung fuer DragNDrop teilweise eingebaut.
 *
 *
 */

public class DateiListeTable extends JTable {
    public DateiListeTable(){
        super(new DateiListeTableModel());
        setDropTarget(new DropTarget(this, new DndTargetAdapter() {
            protected Object getTarget( Point point ){
                return this;
             }

            public void drop(DropTargetDropEvent event) {
                Transferable tr = event.getTransferable();
                if (tr.isDataFlavorSupported(new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, "ShareNodesTransferer"))){
                    try{
                        event.acceptDrop(DnDConstants.ACTION_COPY);
                        Object[] transfer = (Object[])tr.getTransferData(new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, "ShareNodesTransferer"));
                        if (transfer != null && transfer.length!=0){
                            ShareNode node = null;
                            DateiListeTableModel model = (DateiListeTableModel)getModel();
                            for (int i=0; i<transfer.length; i++){
                                model.addNodes((ShareNode)transfer[i]);
                            }
                        }
                    }
                    catch(Exception e){
                        e.printStackTrace();
                        event.getDropTargetContext().dropComplete(false);
                    }
                }
                event.getDropTargetContext().dropComplete(true);
            }
        }));
    }
}
