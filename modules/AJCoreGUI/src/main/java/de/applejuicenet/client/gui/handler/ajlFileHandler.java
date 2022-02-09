package de.applejuicenet.client.gui.handler;

import de.applejuicenet.client.gui.AppleJuiceDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.desktop.OpenFilesEvent;
import java.awt.desktop.OpenFilesHandler;
import java.io.File;

public class ajlFileHandler implements OpenFilesHandler {

    @Override
    public void openFiles(OpenFilesEvent e) {
        Logger logger = LoggerFactory.getLogger(getClass());

        for (File inputFile : e.getFiles()) {
            if (inputFile.getName().endsWith(".ajl")) {
                logger.debug("AJL Datei " + inputFile.getName() + " gefunden");
                new AppleJuiceDialog().importAjl(inputFile, "");
            } else {
                logger.debug("Die Datei " + inputFile.getName() + " ist keine .ajl Datei");
            }
        }
    }
}
