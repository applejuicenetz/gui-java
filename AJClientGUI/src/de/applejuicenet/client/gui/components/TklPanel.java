package de.applejuicenet.client.gui.components;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import de.applejuicenet.client.gui.RegisterI;


public class TklPanel extends JPanel implements RegisterI {
	private static final long serialVersionUID = 430797676444022687L;
	
	protected final GuiController guiController;
	protected final Logger logger;
	
	public TklPanel(GuiController guiController){
		if (null == guiController){
			throw new RuntimeException("Ungueltiger GuiController");
		}
		logger = Logger.getLogger(getClass());
		this.guiController = guiController;
	}

	public final void registerSelected() {
		guiController.componentSelected();
	}

	public final void lostSelection() {
		guiController.componentLostSelection();
	}
}
