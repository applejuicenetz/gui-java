package de.applejuicenet.client.gui.controller;

import java.util.*;

import org.w3c.dom.*;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import de.applejuicenet.client.shared.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/Attic/SettingsXMLHolder.java,v 1.10 2003/12/29 16:04:17 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fï¿½r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: SettingsXMLHolder.java,v $
 * Revision 1.10  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.9  2003/10/12 15:57:55  maj0r
 * Kleinere Bugs behoben.
 * Sortiert wird nun nur noch bei Klick auf den Spaltenkopf um CPU-Zeit zu sparen.
 *
 * Revision 1.8  2003/09/10 13:16:28  maj0r
 * Veraltete Option "Browsen erlauben" entfernt und neue Option MaxNewConnectionsPerTurn hinzugefuegt.
 *
 * Revision 1.7  2003/08/10 21:08:18  maj0r
 * Diverse Änderungen.
 *
 * Revision 1.6  2003/07/01 06:17:16  maj0r
 * Code optimiert.
 *
 * Revision 1.5  2003/06/30 20:35:50  maj0r
 * Code optimiert.
 *
 * Revision 1.4  2003/06/22 19:00:27  maj0r
 * Basisklasse umbenannt.
 *
 * Revision 1.3  2003/06/10 12:31:03  maj0r
 * Historie eingefï¿½gt.
 *
 *
 */

public class SettingsXMLHolder
        extends WebXMLParser {
    private AJSettings settings;
    private Logger logger;

    public SettingsXMLHolder() {
        super("/xml/settings.xml", "", false);
        logger = Logger.getLogger(getClass());
    }

    public void update() {
        try {
            reload("");
            NodeList nodes = document.getElementsByTagName("nick");
            String nick = nodes.item(0).getFirstChild().getNodeValue();
            nodes = document.getElementsByTagName("port");
            long port = Long.parseLong(nodes.item(0).getFirstChild().getNodeValue());
            nodes = document.getElementsByTagName("xmlport");
            long xmlPort = Long.parseLong(nodes.item(0).getFirstChild().getNodeValue());
            nodes = document.getElementsByTagName("maxupload");
            long maxUpload = Long.parseLong(nodes.item(0).getFirstChild().getNodeValue());
            nodes = document.getElementsByTagName("maxdownload");
            long maxDownload = Long.parseLong(nodes.item(0).getFirstChild().
                    getNodeValue());
            nodes = document.getElementsByTagName("maxconnections");
            long maxConnections = Long.parseLong(nodes.item(0).getFirstChild().
                    getNodeValue());
            nodes = document.getElementsByTagName("autoconnect");
            boolean autoConnect = new Boolean(nodes.item(0).getFirstChild().
                    getNodeValue()).booleanValue();
            nodes = document.getElementsByTagName("speedperslot");
            int speedPerSlot = Integer.parseInt(nodes.item(0).getFirstChild().
                    getNodeValue());
            nodes = document.getElementsByTagName("maxnewconnectionsperturn");
            long maxNewConnectionsPerTurn = Long.parseLong(nodes.item(0).getFirstChild().
                    getNodeValue());
            nodes = document.getElementsByTagName("incomingdirectory");
            String incomingDir = nodes.item(0).getFirstChild().getNodeValue();
            nodes = document.getElementsByTagName("temporarydirectory");
            String tempDir = nodes.item(0).getFirstChild().getNodeValue();
            HashSet shareEntries = new HashSet();
            nodes = document.getElementsByTagName("directory");
            Element e = null;
            String dir = null;
            String shareMode = null;
            ShareEntry entry = null;
            int nodesSize = nodes.getLength();
            for (int i = 0; i < nodesSize; i++) {
                e = (Element) nodes.item(i);
                dir = e.getAttribute("name");
                shareMode = e.getAttribute("sharemode");
                entry = new ShareEntry(dir, shareMode);
                shareEntries.add(entry);
            }
            settings = new AJSettings(nick, port, xmlPort, maxUpload,
                    maxDownload, speedPerSlot, incomingDir, tempDir,
                    shareEntries, maxConnections, autoConnect,
                    maxNewConnectionsPerTurn);
        }
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", ex);
        }
    }

    public AJSettings getAJSettings() {
        update();
        return settings;
    }
}