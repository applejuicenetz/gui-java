package de.applejuicenet.client.gui.handler;

import de.applejuicenet.client.AppleJuiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.desktop.OpenURIEvent;
import java.awt.desktop.OpenURIHandler;

public class ajfspURIHandler implements OpenURIHandler {

    @Override
    public void openURI(OpenURIEvent e) {
        String ajfsp = e.getURI().toString();
        Logger logger = LoggerFactory.getLogger(getClass());
        logger.debug("ajfsp Link gefunden: " + ajfsp);

        AppleJuiceClient.linkListener.processLink(ajfsp, "");
    }
}
