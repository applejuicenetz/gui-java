package de.applejuicenet.client.gui.controller.xmlholder;

import de.applejuicenet.client.shared.dac.DirectoryDO;
import de.applejuicenet.client.gui.trees.ApplejuiceNode;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import java.net.URLEncoder;
import de.applejuicenet.client.gui.controller.WebXMLParser;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/xmlholder/Attic/DirectoryXMLHolder.java,v 1.3 2004/01/29 10:05:02 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI f�r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: DirectoryXMLHolder.java,v $
 * Revision 1.3  2004/01/29 10:05:02  maj0r
 * Sharebaum wird jetzt wieder korrekt angezeigt.
 *
 * Revision 1.2  2004/01/06 17:32:50  maj0r
 * Es wird nun zweimal versucht den Core erneut zu erreichen, wenn die Verbindung unterbrochen wurde.
 *
 * Revision 1.1  2003/12/31 16:13:31  maj0r
 * Refactoring.
 *
 * Revision 1.9  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.8  2003/10/14 15:45:09  maj0r
 * Logger eingebaut.
 *
 * Revision 1.7  2003/10/12 15:57:55  maj0r
 * Kleinere Bugs behoben.
 * Sortiert wird nun nur noch bei Klick auf den Spaltenkopf um CPU-Zeit zu sparen.
 *
 * Revision 1.6  2003/08/28 06:57:41  maj0r
 * Plattformunabhaengigkeit wieder hergestellt.
 *
 * Revision 1.5  2003/08/24 14:59:59  maj0r
 * Version 0.14
 * Diverse Aenderungen.
 *
 * Revision 1.4  2003/08/19 12:38:47  maj0r
 * Passworteingabe und md5 korrigiert.
 *
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
    private ApplejuiceNode directoryNode;

    public DirectoryXMLHolder() {
        super("/xml/directory.xml", "", false);
        logger = Logger.getLogger(getClass());
    }

    public void update() {
        try
        {
            if (directory == null)
                directory = "";

            if (directory.length() == 0)
                reload("", false);
            else
                reload("directory=" + URLEncoder.encode(directory, "UTF-8"), false);
            Element e = null;
            NodeList nodes = document.getElementsByTagName("filesystem");
            e = (Element) nodes.item(0);
            DirectoryDO.setSeparator(e.getAttribute("seperator"));
            nodes = document.getElementsByTagName("dir");
            for (int i=0; i<nodes.getLength(); i++){
                e = (Element) nodes.item(i);
                getNodes(e, directoryNode);
            }
        }
        catch (Exception ex)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", ex);
        }
    }

    private void getNodes(Element element, ApplejuiceNode directoryNode) {
        int type;
        boolean fileSystem;
        String name;
        String path;
        name = element.getAttribute("name");
        fileSystem = Boolean.getBoolean(element.getAttribute("isfilesystem"));
        type = Integer.parseInt(element.getAttribute("type"));
        path = element.getAttribute("path");
        if (path.length() == 0)
        {
            if (directoryNode.getDO() != null)
            {
                String parentPfad = directoryNode.getDO().getPath();
                if (parentPfad.length() != 0 && parentPfad.lastIndexOf(DirectoryDO.getSeparator()) == parentPfad.length() - 1)
                {
                    path = parentPfad + name;
                }
                else
                    path = parentPfad + DirectoryDO.getSeparator() + name;
            }
            else
            {
                path = DirectoryDO.getSeparator();
            }
        }
        DirectoryDO directoryDO = new DirectoryDO(name, type, fileSystem, path);
        ApplejuiceNode newNode = directoryNode.addChild(directoryDO);
    }

    public void getDirectory(String directory, ApplejuiceNode directoryNode) {
        this.directory = directory;
        this.directoryNode = directoryNode;
        update();
    }
}
