package de.applejuicenet.client.gui.upload;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import de.applejuicenet.client.gui.components.GuiController;

public class UploadTableMouseListener extends MouseAdapter{
	private GuiController guiController;
	private int actionId;
	
	public UploadTableMouseListener(GuiController guiController, int actionId){
		this.guiController = guiController;
		this.actionId = actionId;
	}
	
	public void mouseClicked(MouseEvent e) {
		super.mouseClicked(e);
		guiController.fireAction(actionId, e);
	}

}
