package de.applejuicenet.client.gui.download;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import de.applejuicenet.client.gui.components.GuiController;

public class DownloadDOPropertyChangeListener implements PropertyChangeListener{
    
    private GuiController guiController;
    private int actionId;
    
    public DownloadDOPropertyChangeListener(GuiController guiController, int actionId) {
        this.guiController = guiController;
        this.actionId = actionId;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        guiController.fireAction(actionId, evt);        
    }

}
