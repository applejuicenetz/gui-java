package de.applejuicenet.client.gui.download;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import de.applejuicenet.client.gui.download.table.DownloadRootNode;
import de.applejuicenet.client.gui.tables.JTreeTable;
import de.applejuicenet.client.gui.tables.TreeTableModelAdapter;

public class DownloadTableMouseAdapter extends MouseAdapter{
	
	private JTreeTable downloadTable;
	private DownloadController downloadController;
	
	public DownloadTableMouseAdapter(DownloadController downloadController,
			JTreeTable downloadTable){
		this.downloadController = downloadController;
		this.downloadTable = downloadTable;
	}
	
	public void mouseClicked(MouseEvent e) {
		super.mouseClicked(e);
		if (!DownloadRootNode.isInitialized()) {
			return;
		}
		Point p = e.getPoint();
		int selectedRow = downloadTable.rowAtPoint(p);
		Object node = ((TreeTableModelAdapter) downloadTable.getModel())
				.nodeForRow(selectedRow);
		if (downloadTable.columnAtPoint(p) != 0) {
			if (e.getClickCount() == 2) {
				((TreeTableModelAdapter) downloadTable.getModel())
						.expandOrCollapseRow(selectedRow);
			}
		}
		downloadController.fireItemClicked(node);
	}

	public void mousePressed(MouseEvent me) {
		super.mousePressed(me);
		maybeShowPopup(me);
	}

	public void mouseReleased(MouseEvent e) {
		super.mouseReleased(e);
		maybeShowPopup(e);
	}

	private void maybeShowPopup(MouseEvent e) {
		if (!DownloadRootNode.isInitialized()) {
			return;
		}
		if (e.isPopupTrigger()) {
			Point p = e.getPoint();
			int selectedRow = downloadTable.rowAtPoint(p);
			int[] currentSelectedRows = downloadTable.getSelectedRows();
			for (int i = 0; i < currentSelectedRows.length; i++) {
				if (currentSelectedRows[i] == selectedRow) {
					selectedRow = -1;
					break;
				}
			}
			if (selectedRow != -1) {
				downloadTable.setRowSelectionInterval(selectedRow,
						selectedRow);
			}
			downloadController.fireMaybeShowPopup(e);
		}
	}
}
