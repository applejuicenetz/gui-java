package de.applejuicenet.client.gui.controller;

import de.applejuicenet.client.shared.AJSettings;
import de.applejuicenet.client.shared.ShareEntry;
import de.applejuicenet.client.shared.LoggerUtils;
import de.applejuicenet.client.shared.dac.DirectoryDO;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.apache.log4j.Logger;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/Attic/DirectoryXMLHolder.java,v 1.1 2003/08/15 14:46:30 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DirectoryXMLHolder.java,v $
 * Revision 1.1  2003/08/15 14:46:30  maj0r
 * Refactoring.
 *
 *
 */

public class DirectoryXMLHolder extends WebXMLParser {

    private String directory;
    private DirectoryDO[] directoryDO;
    private Logger logger;

    public DirectoryXMLHolder() {
        super("/xml/directory.xml", "", false);
        logger = Logger.getLogger(getClass());
    }

    public void update() {
        if (directory == null)
            directory = "";

        reload("dir=" + directory);
        Element e = null;
        NodeList nodes = document.getElementsByTagName("dir");
        int nodesSize = nodes.getLength();
        directoryDO = new DirectoryDO[nodesSize];
        int type;
        boolean fileSystem;
        String name;
        String path;
        for (int i = 0; i < nodesSize; i++)
        {
            e = (Element) nodes.item(i);
            name = e.getAttribute("name");
            fileSystem = Boolean.getBoolean(e.getAttribute("isfilesystem"));
            type = Integer.parseInt(e.getAttribute("type"));
            path = e.getAttribute("path");
            directoryDO[i] = new DirectoryDO(name, type, fileSystem, path);
        }
        nodes = document.getElementsByTagName("filesystem");
        e = (Element) nodes.item(0);
        String separator = e.getAttribute("seperator");
        directoryDO[0].setSeparator(separator);
    }

    public DirectoryDO[] getDirectory(String directory) {
        this.directory = directory;
        update();
        return directoryDO;
    }
}
