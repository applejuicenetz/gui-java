package de.applejuicenet.client.gui.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/components/GuiControllerActionListener.java,v 1.2 2004/10/15 13:34:48 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

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
