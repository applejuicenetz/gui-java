package de.applejuicenet.client.gui.download;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import de.applejuicenet.client.gui.components.GuiController;
import de.applejuicenet.client.gui.components.treetable.JTreeTable;
import de.applejuicenet.client.gui.download.table.DownloadNode;

public class DownloadTablePopupListener extends MouseAdapter{
	
	private JTreeTable downloadTable;
	private GuiController guiController;
	private int actionId;

	public DownloadTablePopupListener(GuiController guiController,
			JTreeTable downloadTable, int actionId){
		this.guiController = guiController;
		this.downloadTable = downloadTable;
		this.actionId = actionId;
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
		if (!DownloadNode.isInitialized()) {
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
			guiController.fireAction(actionId, e);
		}
	}	
}
