package de.applejuicenet.client.gui.debug;

import java.awt.BorderLayout;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.gui.components.GuiController;
import de.applejuicenet.client.gui.components.TklPanel;
import de.tklsoft.gui.controls.TKLTextArea;


public class DebugPanel extends TklPanel {
    
    private TKLTextArea debugArea = new TKLTextArea();
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss.SSS");

    public DebugPanel(GuiController guiController) {
        super(guiController);
        try {
            init();
        } catch (Exception e) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
        }
    }

    private void init() {
        debugArea.setEditable(false);
        setLayout(new BorderLayout());
        add(debugArea, BorderLayout.CENTER);
    }
    
    public void addMessage(String message){
        Date now = new Date(System.currentTimeMillis());
        debugArea.append(dateFormatter.format(now) + "\t - " + message + "\r\n");
    }
}
