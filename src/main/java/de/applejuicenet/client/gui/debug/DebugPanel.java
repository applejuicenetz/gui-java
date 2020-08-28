package de.applejuicenet.client.gui.debug;

import java.awt.BorderLayout;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JScrollPane;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.gui.components.GuiController;
import de.applejuicenet.client.gui.components.TklPanel;
import de.tklsoft.gui.controls.TKLTextArea;


public class DebugPanel extends TklPanel {
    
    private TKLTextArea debugArea = new TKLTextArea();
    private JScrollPane sp = new JScrollPane(debugArea);
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
        add(sp, BorderLayout.CENTER);
    }
    
    public void addMessage(String message){
        Date now = new Date(System.currentTimeMillis());
        debugArea.append(dateFormatter.format(now) + "\t - " + message + "\r\n");
        debugArea.setCaretPosition(debugArea.getDocument().getLength());
    }
}
