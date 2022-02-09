package de.applejuicenet.client.gui.wizard;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.shared.XMLDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/wizard/ConnectionXML.java,v 1.9 2005/02/28 14:58:19 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 */

public class ConnectionXML
        extends XMLDecoder {
    private static Logger logger;

    private ConnectionXML(String path) {
        super(path);
        logger = LoggerFactory.getLogger(getClass());
    }

    public static ConnectionKind[] getConnections() {
        try {
            String path = System.getProperty("user.dir") + File.separator +
                    "wizard.xml";
            ConnectionXML connectionXML = new ConnectionXML(path);

            Element e = null;
            ArrayList<ConnectionKind> connectionKinds = new ArrayList<ConnectionKind>();
            String bezeichnung;
            int maxUpload;
            int maxDownload;
            int maxNewConnectionsPro10Sek;
            NodeList nodes = connectionXML.document.getElementsByTagName(
                    "wizard");
            nodes = nodes.item(0).getChildNodes();
            int nodesSize = nodes.getLength();
            for (int y = 0; y < nodesSize; y++) {
                if (nodes.item(y).getNodeType() == Node.ELEMENT_NODE) {
                    e = (Element) nodes.item(y);
                    bezeichnung = e.getAttribute("bezeichnung");
                    maxUpload = Integer.parseInt(e.getAttribute("maxupload"));
                    maxDownload = Integer.parseInt(e.getAttribute("maxdownload"));
                    maxNewConnectionsPro10Sek = Integer.parseInt(e.getAttribute(
                            "maxnewconnections10"));
                    connectionKinds.add(new ConnectionKind(bezeichnung,
                            maxUpload, maxDownload, maxNewConnectionsPro10Sek));
                }
            }
            return connectionKinds.toArray(new
                    ConnectionKind[connectionKinds.size()]);
        } catch (Exception e) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            return null;
        }
    }
}
