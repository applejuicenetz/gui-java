package de.applejuicenet.client.gui.controller.xmlholder;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/xmlholder/Attic/NetworkServerXMLHolder.java,v 1.2 2004/01/02 16:52:43 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: NetworkServerXMLHolder.java,v $
 * Revision 1.2  2004/01/02 16:52:43  maj0r
 * Debug-Aenderung entfernt.
 *
 * Revision 1.1  2004/01/02 16:49:48  maj0r
 * Serverliste holen geaendert.
 *
 *
 */

import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import de.applejuicenet.client.shared.WebsiteContentLoader;
import de.applejuicenet.client.shared.XMLDecoder;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class NetworkServerXMLHolder
    extends XMLDecoder {

    private static NetworkServerXMLHolder instance = null;
    private Logger logger;

    private NetworkServerXMLHolder() {
        logger = Logger.getLogger(getClass());
    }

    public static NetworkServerXMLHolder getInstance() {
        if (instance == null) {
            instance = new NetworkServerXMLHolder();
        }
        return instance;
    }

    public String[] getNetworkKnownServers(){
        String xmlData = null;
        try {
            xmlData = WebsiteContentLoader.getWebsiteContent(
                "http://www.applejuicenet.org", 80,
                "/serverlist/xmllist.php");
            if (xmlData == null
                || xmlData.length() == 0) {
                return null;
            }
            DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(new InputSource(new StringReader(xmlData)));
            NodeList nodes = document.getElementsByTagName("server");
            Element e = null;
            String link;
            int nodesSize = nodes.getLength();
            if (nodesSize == 0){
                return null;
            }
            String[] servers = new String[nodesSize];
            for (int i = 0; i < nodesSize; i++) {
                e = (Element) nodes.item(i);
                servers[i] = e.getAttribute("link");
            }
            return servers;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}