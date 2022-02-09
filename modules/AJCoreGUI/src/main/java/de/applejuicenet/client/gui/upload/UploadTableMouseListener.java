package de.applejuicenet.client.gui.upload;

import de.applejuicenet.client.gui.components.GuiController;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
