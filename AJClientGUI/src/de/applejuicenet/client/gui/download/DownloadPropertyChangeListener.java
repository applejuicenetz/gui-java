package de.applejuicenet.client.gui.download;

import de.applejuicenet.client.fassade.event.DataPropertyChangeEvent;
import de.applejuicenet.client.fassade.listener.DataPropertyChangeListener;
import de.applejuicenet.client.gui.components.GuiController;

public class DownloadPropertyChangeListener implements DataPropertyChangeListener{
    
    private GuiController guiController;
    private int actionId;
    
    public DownloadPropertyChangeListener(GuiController guiController, int actionId) {
        this.guiController = guiController;
        this.actionId = actionId;
    }

	public void propertyChanged(DataPropertyChangeEvent dataPropertyChangeEvent) {
        guiController.fireAction(actionId, dataPropertyChangeEvent);        
	}

}
