package de.applejuicenet.client.gui.download;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import de.applejuicenet.client.gui.components.GuiController;
import de.applejuicenet.client.gui.components.treetable.JTreeTable;
import de.applejuicenet.client.gui.components.treetable.TreeTableModelAdapter;
import de.applejuicenet.client.gui.download.table.DownloadRootNode;

public class DownloadTableMouseListener extends MouseAdapter{
	
	private JTreeTable downloadTable;
	private GuiController guiController;
	private int actionId;
	
	public DownloadTableMouseListener(GuiController guiController,
			JTreeTable downloadTable, int actionId){
		this.guiController = guiController;
		this.downloadTable = downloadTable;
		this.actionId = actionId;
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
		guiController.fireAction(actionId, node);
	}
}
