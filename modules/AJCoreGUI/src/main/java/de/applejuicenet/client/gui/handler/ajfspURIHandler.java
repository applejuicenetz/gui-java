package de.applejuicenet.client.gui.handler;

import de.applejuicenet.client.AppleJuiceClient;
import org.apache.log4j.Logger;

import java.awt.desktop.OpenURIHandler;
import java.awt.desktop.OpenURIEvent;
import java.io.File;

public class ajfspURIHandler implements OpenURIHandler {

    private static Logger logger = Logger.getLogger(AppleJuiceClient.class);

    @Override
    public void openURI(OpenURIEvent e) {
        String ajfsp = e.getURI().toString();
        logger.info("ajfsp Link gefunden: " + ajfsp);

        AppleJuiceClient.linkListener.processLink(ajfsp, "");
    }
}
