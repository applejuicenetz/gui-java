package de.applejuicenet.client.gui.upload;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import de.applejuicenet.client.gui.components.GuiController;

public class HeaderPopupListener extends MouseAdapter {
	private GuiController guiController;
	private int actionId;

	public HeaderPopupListener(GuiController guiController, int actionId) {
		this.guiController = guiController;
		this.actionId = actionId;
		
//		columnPopupItems[0].setSelected(true);
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
		if (e.isPopupTrigger()) {
			((UploadController)guiController).fireAction(actionId, e);
		}
	}
}
