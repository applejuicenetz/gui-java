package de.applejuicenet.client.gui.components;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import de.applejuicenet.client.gui.RegisterI;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/components/TklPanel.java,v 1.2 2004/10/15 13:34:48 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

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
