package de.applejuicenet.client.gui.controller;

import de.applejuicenet.client.shared.AJSettings;
import de.applejuicenet.client.shared.ShareEntry;
import de.applejuicenet.client.shared.LoggerUtils;
import de.applejuicenet.client.shared.dac.DirectoryDO;
import de.applejuicenet.client.gui.tables.download.DownloadNode;
import de.applejuicenet.client.gui.trees.share.DirectoryNode;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.apache.log4j.Logger;

import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/Attic/DirectoryXMLHolder.java,v 1.3 2003/08/17 16:13:11 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DirectoryXMLHolder.java,v $
 * Revision 1.3  2003/08/17 16:13:11  maj0r
 * Erstellen des DirectoryNode-Baumes korrigiert.
 *
 * Revision 1.2  2003/08/16 20:53:40  maj0r
 * Kleinen Fehler korrigiert
 *
 * Revision 1.1  2003/08/15 14:46:30  maj0r
 * Refactoring.
 *
 *
 */

public class DirectoryXMLHolder extends WebXMLParser {

    private String directory;
    private Logger logger;
    private DirectoryNode directoryNode;

    public DirectoryXMLHolder() {
        super("/xml/directory.xml", "", false);
        logger = Logger.getLogger(getClass());
    }

    public void update() {
        if (directory == null)
            directory = "";

        try {
            if (directory.length()==0)
                reload("");
            else
                reload("directory=" + URLEncoder.encode(directory, "UTF-8"));
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
        Element e = null;
        NodeList nodes = document.getElementsByTagName("applejuice");
        e = (Element) nodes.item(0);
        nodes = e.getChildNodes();
        String name;
        int nodesSize = nodes.getLength();
        for (int i = 0; i < nodesSize; i++)
        {
            e = (Element) nodes.item(i);
            name = e.getNodeName();
            if (name.compareToIgnoreCase("filesystem")==0){
                DirectoryDO.setSeparator(e.getAttribute("seperator"));
            }
            else if (name.compareToIgnoreCase("dir")==0){
                getNodes(e, directoryNode);
            }
        }
    }

    private void getNodes(Element element, DirectoryNode directoryNode){
        int type;
        boolean fileSystem;
        String name;
        String path;
        name = element.getAttribute("name");
        fileSystem = Boolean.getBoolean(element.getAttribute("isfilesystem"));
        type = Integer.parseInt(element.getAttribute("type"));
        path = element.getAttribute("path");
        if (path.length()==0){
            if (directoryNode.getDO()!=null){
                String parentPfad = directoryNode.getDO().getPath();
                if (parentPfad.length()!=0 && parentPfad.lastIndexOf('\\')==parentPfad.length()-1){
                    path = parentPfad + name;
                }
                else
                    path = parentPfad + DirectoryDO.getSeparator() + name;
            }
        }
        DirectoryDO directoryDO = new DirectoryDO(name, type, fileSystem, path);
        DirectoryNode newNode = directoryNode.addChild(directoryDO);
        NodeList nodes = element.getChildNodes();
        int nodesSize = nodes.getLength();
        Element e = null;
        for (int i = 0; i < nodesSize; i++)
        {
            e = (Element) nodes.item(i);
            name = e.getNodeName();
            if (name.compareToIgnoreCase("dir")==0){
                getNodes(e, newNode);
            }
/*            e = (Element) nodes.item(i);
            name = e.getAttribute("name");
            fileSystem = Boolean.getBoolean(e.getAttribute("isfilesystem"));
            type = Integer.parseInt(e.getAttribute("type"));
            path = e.getAttribute("path");
            if (path.length()==0){
                if (directory.length()!=0 && directory.lastIndexOf('\\')==directory.length()-1){
                    path = directory + name;
                }
                else
                    path = directory + separator + name;
            }
            directoryDO = new DirectoryDO(name, type, fileSystem, path);
            DirectoryNode childNode = new DirectoryNode(directoryNode, directoryDO);     */
        }
    }

    public void getDirectory(String directory, DirectoryNode directoryNode) {
        this.directory = directory;
        this.directoryNode = directoryNode;
        update();
    }
}
