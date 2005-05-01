package de.applejuicenet.client.gui.debug;

import java.io.StringWriter;

import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;

public class LiveAppender extends WriterAppender{
    
    private final DebugController debugController;
    
    public LiveAppender(DebugController debugController){
        this.debugController = debugController;      
        setWriter(new StringWriter());
        setLayout(new SimpleLayout());
    }

    protected void subAppend(LoggingEvent lE) {
        super.subAppend(lE);
        debugController.fireAction(DebugController.UPDATE_LOGGER, lE);
    }
}