package de.applejuicenet.client.gui.components;

import javax.swing.JComponent;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.listener.DataUpdateListener;
import de.applejuicenet.client.gui.listener.LanguageListener;

public abstract class GuiController implements LanguageListener, DataUpdateListener{
	protected final Logger logger; 
	
	public GuiController(){
		logger = Logger.getLogger(getClass());
	}
	
	public abstract void fireAction(int actionId);

	public abstract JComponent getComponent();
	
	public abstract void componentSelected();

	public abstract void componentLostSelection();

	public final void fireLanguageChanged() {
		try{
			languageChanged();
		} catch (Exception e) {
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
			}
		}
	}

	public void fireContentChanged(int type, Object content) {
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
