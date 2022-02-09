package de.applejuicenet.client.gui.components;

import de.applejuicenet.client.gui.RegisterI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/components/TklPanel.java,v 1.3 2004/11/22 16:25:26 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 *
 */

public class TklPanel extends JPanel implements RegisterI {
	
	protected final GuiController guiController;
	protected final Logger logger;
	
	public TklPanel(GuiController guiController){
		if (null == guiController){
			throw new RuntimeException("Ungueltiger GuiController");
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
