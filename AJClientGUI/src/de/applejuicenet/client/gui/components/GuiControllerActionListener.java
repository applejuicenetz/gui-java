package de.applejuicenet.client.gui.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class GuiControllerActionListener implements ActionListener {
	private final int controllerActionId;
	private final GuiController guiController;
	
	public GuiControllerActionListener(GuiController guiController, int controllerActionId){
		this.controllerActionId = controllerActionId;
		this.guiController = guiController;
	}

	public void actionPerformed(ActionEvent e) {
		guiController.fireAction(controllerActionId);
	}
}
