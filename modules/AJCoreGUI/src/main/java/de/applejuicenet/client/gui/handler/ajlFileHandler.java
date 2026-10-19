package de.applejuicenet.client.gui.handler;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.gui.controller.LinkListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.desktop.OpenFilesEvent;
import java.awt.desktop.OpenFilesHandler;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class ajlFileHandler implements OpenFilesHandler {

    @Override
    public void openFiles(OpenFilesEvent e) {
        Logger logger = LoggerFactory.getLogger(getClass());

        for (File inputFile : e.getFiles()) {
            if (inputFile.getName().toLowerCase(Locale.ROOT).endsWith(".ajl")) {
                logger.debug("AJL file " + inputFile.getName() + " found");
                Thread.ofVirtual().name("AJL-Import").start(() -> {
                    try {
                        if (AppleJuiceClient.linkListener != null) {
                            AppleJuiceClient.linkListener.processAjl(inputFile, "");
                        } else {
                            LinkListener.forwardAjl(inputFile);
                        }
                    } catch (IOException ex) {
                        logger.warn("Could not import AJL file: " + inputFile, ex);
                    }
                });
            } else {
                logger.debug("The file " + inputFile.getName() + " is not an .ajl file");
            }
        }
    }
}
