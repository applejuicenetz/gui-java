package de.applejuicenet.client.gui.components;

import de.applejuicenet.client.gui.RegisterI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * Copyright: General Public License
 */

public class TklPanel extends JPanel implements RegisterI {
	
	protected final GuiController guiController;
	protected final Logger logger;
	
	public TklPanel(GuiController guiController){
		if (null == guiController){
			throw new RuntimeException("Invalid GuiController");
		}
		logger = LoggerFactory.getLogger(getClass());
		this.guiController = guiController;
	}

	public final void registerSelected() {
		guiController.componentSelected();
	}

	public final void lostSelection() {
		guiController.componentLostSelection();
	}
}
