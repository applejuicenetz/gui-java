package de.applejuicenet.client.gui.shared;

import javax.swing.JComponent;

import de.applejuicenet.client.gui.listener.LanguageListener;

public interface GuiController extends LanguageListener{
	
	void fireAction(int actionId);

	JComponent getComponent();
}
