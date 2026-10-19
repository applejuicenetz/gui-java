package de.applejuicenet.client.gui.share;

import de.applejuicenet.client.gui.share.table.ShareNode;
import de.applejuicenet.client.gui.share.table.ShareTable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Copyright: General Public License
 */

public class ShareTableMouseAdapter extends MouseAdapter {
	
	private ShareTable shareTable;
	private JPopupMenu popup;
	private JPopupMenu folderPopup;
	
	public ShareTableMouseAdapter(ShareTable shareTable, JPopupMenu popup, JPopupMenu folderPopup){
		this.shareTable = shareTable;
		this.popup = popup;
		this.folderPopup = folderPopup;
	}
	
    public void mousePressed(MouseEvent me) {
        if (SwingUtilities.isRightMouseButton(me)) {
            Point p = me.getPoint();
            int iRow = shareTable.rowAtPoint(p);
            int iCol = shareTable.columnAtPoint(p);
            if (iRow == -1 || iCol == -1) {
                return;
            }
            shareTable.setRowSelectionInterval(iRow, iRow);
            shareTable.setColumnSelectionInterval(iCol, iCol);
        }
        maybeShowPopup(me);
    }

    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        maybeShowPopup(e);
    }

    private void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger() && shareTable.getSelectedRowCount() == 1) {
            Object[] obj = shareTable.getSelectedItems();
            if ( ( (ShareNode) obj[0]).isLeaf()) {
                popup.show(shareTable, e.getX(), e.getY());
            } else {
                folderPopup.show(shareTable, e.getX(), e.getY());
            }
        }
    }
}
