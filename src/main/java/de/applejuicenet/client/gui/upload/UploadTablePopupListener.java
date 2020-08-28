package de.applejuicenet.client.gui.upload;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import de.applejuicenet.client.gui.components.GuiController;

public class UploadTablePopupListener extends MouseAdapter{
	private GuiController guiController;
	private int actionId;
	
	public UploadTablePopupListener(GuiController guiController, int actionId){
		this.guiController = guiController;
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
		if (e.isPopupTrigger()) {
			guiController.fireAction(actionId, e);
		}
	}
}
