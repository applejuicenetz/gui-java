package de.applejuicenet.client.gui.controller.xmlholder;

import java.util.HashSet;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import de.applejuicenet.client.gui.controller.WebXMLParser;
import de.applejuicenet.client.shared.AJSettings;
import de.applejuicenet.client.shared.ShareEntry;
import java.util.Set;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/xmlholder/Attic/SettingsXMLHolder.java,v 1.9 2004/10/11 18:18:51 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 */

public class SettingsXMLHolder
    extends WebXMLParser {
    private AJSettings settings;
    private Logger logger;

    public SettingsXMLHolder() {
        super("/xml/settings.xml", "");
        logger = Logger.getLogger(getClass());
    }

    public void update() {
        try {
            reload("", false);
            NodeList nodes = document.getElementsByTagName("nick");
            String nick = nodes.item(0).getFirstChild().getNodeValue();
            nodes = document.getElementsByTagName("port");
            long port = Long.parseLong(nodes.item(0).getFirstChild().
                                       getNodeValue());
            nodes = document.getElementsByTagName("xmlport");
            long xmlPort = Long.parseLong(nodes.item(0).getFirstChild().
                                          getNodeValue());
            nodes = document.getElementsByTagName("maxupload");
            long maxUpload = Long.parseLong(nodes.item(0).getFirstChild().
                                            getNodeValue());
            nodes = document.getElementsByTagName("maxdownload");
            long maxDownload = Long.parseLong(nodes.item(0).getFirstChild().
                                              getNodeValue());
            nodes = document.getElementsByTagName("maxconnections");
            long maxConnections = Long.parseLong(nodes.item(0).getFirstChild().
                                                 getNodeValue());
            nodes = document.getElementsByTagName("maxsourcesperfile");
            long maxSourcesPerFile = Long.parseLong(nodes.item(0).getFirstChild().
                                                 getNodeValue());
            nodes = document.getElementsByTagName("autoconnect");
            boolean autoConnect = new Boolean(nodes.item(0).getFirstChild().
                                              getNodeValue()).booleanValue();
            nodes = document.getElementsByTagName("speedperslot");
            int speedPerSlot = Integer.parseInt(nodes.item(0).getFirstChild().
                                                getNodeValue());
            nodes = document.getElementsByTagName("maxnewconnectionsperturn");
            long maxNewConnectionsPerTurn = Long.parseLong(nodes.item(0).
                getFirstChild().
                getNodeValue());
            nodes = document.getElementsByTagName("incomingdirectory");
            String incomingDir = nodes.item(0).getFirstChild().getNodeValue();
            nodes = document.getElementsByTagName("temporarydirectory");
            String tempDir = nodes.item(0).getFirstChild().getNodeValue();
            Set shareEntries = new HashSet();
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
                                      maxDownload, speedPerSlot, incomingDir,
                                      tempDir,
                                      shareEntries, maxConnections, autoConnect,
                                      maxNewConnectionsPerTurn, maxSourcesPerFile);
        }
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
            }
        }
    }

    public AJSettings getAJSettings() {
        update();
        return settings;
    }

    public AJSettings getCurrentAJSettings() {
        if (settings == null) {
            update();
        }
        return settings;
    }

}
