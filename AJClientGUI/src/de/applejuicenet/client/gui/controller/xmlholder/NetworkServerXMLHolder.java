package de.applejuicenet.client.gui.controller.xmlholder;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/xmlholder/Attic/NetworkServerXMLHolder.java,v 1.4 2004/02/18 18:57:23 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: NetworkServerXMLHolder.java,v $
 * Revision 1.4  2004/02/18 18:57:23  maj0r
 * Von DOM auf SAX umgebaut.
 *
 * Revision 1.3  2004/02/05 23:11:28  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.2  2004/01/02 16:52:43  maj0r
 * Debug-Aenderung entfernt.
 *
 * Revision 1.1  2004/01/02 16:49:48  maj0r
 * Serverliste holen geaendert.
 *
 *
 */

import java.io.StringReader;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;
import de.applejuicenet.client.shared.WebsiteContentLoader;
import org.apache.log4j.Level;

public class NetworkServerXMLHolder
    extends DefaultHandler {

    private static NetworkServerXMLHolder instance = null;
    private Logger logger;
    private XMLReader xr = null;

    private NetworkServerXMLHolder() {
        logger = Logger.getLogger(getClass());
        try {
            System.setProperty("org.xml.sax.parser",
                               "org.apache.xerces.parsers.SAXParser");
            xr = XMLReaderFactory.createXMLReader();
            xr.setContentHandler( this );
        }
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)){
                logger.error("Unbehandelte Exception", ex);
            }
        }
    }

    public static NetworkServerXMLHolder getInstance() {
        if (instance == null) {
            instance = new NetworkServerXMLHolder();
        }
        return instance;
    }

    public void startElement(String namespaceURI,
                             String localName,
                             String qName,
                             Attributes attr) throws SAXException {
        if (localName.equals("server")){
            checkServerAttributes(attr);
        }
    }

    private void checkServerAttributes(Attributes attr){
        for (int i = 0; i < attr.getLength(); i++) {
            if (attr.getLocalName(i).equals("link")){
                links.add(attr.getValue(i));
            }
        }
    }

    private ArrayList links = new ArrayList();

    public String[] getNetworkKnownServers() {
        String xmlData = null;
        try {
            xmlData = WebsiteContentLoader.getWebsiteContent(
                "http://www.applejuicenet.org", 80,
                "/serverlist/xmllist.php");
            if (xmlData == null
                || xmlData.length() == 0) {
                return null;
            }
            links.clear();
            xr.parse( new InputSource(
               new StringReader( xmlData )) );
            return (String[])links.toArray(new String[links.size()]);
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)){
                logger.error("Unbehandelte Exception", e);
            }
            return null;
        }
    }
}
