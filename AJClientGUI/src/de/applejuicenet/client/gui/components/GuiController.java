package de.applejuicenet.client.gui.components;

import javax.swing.JComponent;

import de.applejuicenet.client.gui.listener.LanguageListener;

public abstract class GuiController implements LanguageListener{
	
	public abstract void fireAction(int actionId);

	public abstract JComponent getComponent();
	
	public abstract void componentSelected();

	public abstract void componentLostSelection();
}
