package de.applejuicenet.client.gui.shared;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GuiControllerActionListener implements ActionListener {
	private int controllerActionId;
	private GuiController guiController;
	
	public GuiControllerActionListener(GuiController guiController, int controllerActionId){
		this.controllerActionId = controllerActionId;
		this.guiController = guiController;
	}

	public void actionPerformed(ActionEvent e) {
		guiController.fireAction(controllerActionId);
	}
}
