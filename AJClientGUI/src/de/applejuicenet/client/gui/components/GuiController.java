package de.applejuicenet.client.gui.components;

import javax.swing.JComponent;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.gui.components.util.Value;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.listener.DataUpdateListener;
import de.applejuicenet.client.gui.listener.LanguageListener;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/components/GuiController.java,v 1.5 2004/10/29 13:59:51 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public abstract class GuiController implements LanguageListener, DataUpdateListener{
	protected final Logger logger; 
	
	public GuiController(){
		logger = Logger.getLogger(getClass());
	}
	
	public abstract void fireAction(int actionId, Object source);

	public abstract JComponent getComponent();
	
	public abstract void componentSelected();

	public abstract void componentLostSelection();
	
	public abstract Value[] getCustomizedValues();
	
	public final String getName(){
		return getClass().getSimpleName();
	}

	public final void fireLanguageChanged() {
		try{
			languageChanged();
		} catch (Exception e) {
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
			}
		}
	}

	public final void fireContentChanged(int type, Object content) {
		try{
			contentChanged(type, content);
		} catch (Exception e) {
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
			}
		}
	}
	
	protected abstract void languageChanged();

	protected abstract void contentChanged(int type, Object content);
}
