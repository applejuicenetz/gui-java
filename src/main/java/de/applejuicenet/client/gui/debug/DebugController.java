package de.applejuicenet.client.gui.debug;

import javax.swing.JComponent;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.gui.components.GuiController;
import de.applejuicenet.client.gui.components.util.Value;


public class DebugController extends GuiController {
    
    private static DebugController instance = null;
    
    public static final int UPDATE_LOGGER = 1;
    
    private DebugPanel debugPanel;

    private DebugController() {
        super();
        debugPanel = new DebugPanel(this);
        Logger.getRootLogger().addAppender(new LiveAppender(this));
        try{
            init();
        } catch (Exception e) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
        }
    }

    public static synchronized DebugController getInstance() {
        if (null == instance) {
            instance = new DebugController();
        }
        return instance;
    }

    private void init() {
    }
    
    public Value[] getCustomizedValues(){
        return new Value[0];
    }
    
    public void fireAction(int actionId, Object source) {
        switch (actionId){
            case UPDATE_LOGGER:{
                updateLogger((LoggingEvent)source);
                break;
            }
            default:{
                return;
            }
        }
    }

    private void updateLogger(LoggingEvent event) {
        debugPanel.addMessage(event.getRenderedMessage());
    }

    public JComponent getComponent() {
        return debugPanel;
    }

    public void componentSelected() {
    }

    public void componentLostSelection() {
    }

    protected void languageChanged() {
    }

    protected void contentChanged(DATALISTENER_TYPE type, Object content) {
    }
}
