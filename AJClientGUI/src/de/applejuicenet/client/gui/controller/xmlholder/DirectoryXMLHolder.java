package de.applejuicenet.client.gui.controller.xmlholder;

import java.net.URLEncoder;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.applejuicenet.client.gui.components.tree.ApplejuiceNode;
import de.applejuicenet.client.gui.controller.WebXMLParser;
import de.applejuicenet.client.shared.dac.DirectoryDO;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/xmlholder/Attic/DirectoryXMLHolder.java,v 1.11 2004/10/29 11:58:43 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class DirectoryXMLHolder
    extends WebXMLParser {

    private String directory;
    private Logger logger;
    private ApplejuiceNode directoryNode;

    public DirectoryXMLHolder() {
        super("/xml/directory.xml", "");
        logger = Logger.getLogger(getClass());
    }

    public void update() {
        try {
            if (directory == null) {
                directory = "";
            }
            if (directory.length() == 0) {
                reload("", false);
            }
            else {
                reload("directory=" + URLEncoder.encode(directory, "UTF-8"), false);
            }
            Element e = null;
            NodeList nodes = document.getElementsByTagName("filesystem");
            e = (Element) nodes.item(0);
            DirectoryDO.setSeparator(e.getAttribute("seperator"));
            nodes = document.getElementsByTagName("dir");
            for (int i = 0; i < nodes.getLength(); i++) {
                e = (Element) nodes.item(i);
                getNodes(e, directoryNode);
            }
        }
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
            }
        }
    }

    private void getNodes(Element element, ApplejuiceNode directoryNode) {
        int type;
        boolean fileSystem;
        String name;
        String path;
        name = element.getAttribute("name");
        fileSystem = element.getAttribute("isfilesystem").equalsIgnoreCase("true");
        type = Integer.parseInt(element.getAttribute("type"));
        path = element.getAttribute("path");
        if (path.length() == 0) {
            if (directoryNode.getDO() != null) {
                String parentPfad = directoryNode.getDO().getPath();
                if (parentPfad.length() != 0 &&
                    parentPfad.lastIndexOf(DirectoryDO.getSeparator()) ==
                    parentPfad.length() - 1) {
                    path = parentPfad + name;
                }
                else {
                    path = parentPfad + DirectoryDO.getSeparator() + name;
                }
            }
            else {
                path = DirectoryDO.getSeparator();
            }
        }
        DirectoryDO directoryDO = new DirectoryDO(name, type, fileSystem, path);
        directoryNode.addChild(directoryDO);
    }

    public void getDirectory(String directory, ApplejuiceNode directoryNode) {
        this.directory = directory;
        this.directoryNode = directoryNode;
        update();
    }
}
