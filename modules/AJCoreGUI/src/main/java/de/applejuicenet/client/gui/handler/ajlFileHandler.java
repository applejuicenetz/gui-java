package de.applejuicenet.client.gui.handler;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.gui.AppleJuiceDialog;
import org.apache.log4j.Logger;

import java.awt.desktop.OpenFilesHandler;
import java.awt.desktop.OpenFilesEvent;
import java.io.File;

public class ajlFileHandler implements OpenFilesHandler {

    private static Logger logger = Logger.getLogger(AppleJuiceClient.class);

    @Override
    public void openFiles(OpenFilesEvent e) {
        for (File inputFile : e.getFiles()) {
            if (inputFile.getName().endsWith(".ajl")) {
                new AppleJuiceDialog().importAjl(inputFile, "");
            } else {
                logger.info("Die Datei " + inputFile.getName() + " ist keine .ajl Datei");
            }
        }
    }
}
