package de.applejuicenet.client.gui.controller.xmlholder;

import java.io.StringReader;
import java.util.HashMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;
import de.applejuicenet.client.gui.controller.PropertiesManager;
import de.applejuicenet.client.shared.ConnectionSettings;
import de.applejuicenet.client.shared.HtmlLoader;
import de.applejuicenet.client.shared.dac.ShareDO;
import org.apache.xerces.parsers.SAXParser;
import java.util.Map;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/xmlholder/Attic/ShareXMLHolder.java,v 1.8 2004/03/03 15:33:31 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fï¿½r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: ShareXMLHolder.java,v $
 * Revision 1.8  2004/03/03 15:33:31  maj0r
 * PMD-Optimierung
 *
 * Revision 1.7  2004/02/18 20:44:37  maj0r
 * Bugs #223 und #224 behoben.
 *
 * Revision 1.6  2004/02/18 18:43:04  maj0r
 * Von DOM auf SAX umgebaut.
 *
 * Revision 1.5  2004/02/16 07:42:43  maj0r
 * alten Timestampfehler beseitig
 * Trotz Sessionumsetzung wurde immer noch der Timestamp mitgeschleppt.
 *
 * Revision 1.4  2004/02/05 23:11:28  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.3  2004/01/30 16:32:47  maj0r
 * MapSetStringKey ausgebaut.
 *
 * Revision 1.2  2004/01/06 17:32:50  maj0r
 * Es wird nun zweimal versucht den Core erneut zu erreichen, wenn die Verbindung unterbrochen wurde.
 *
 * Revision 1.1  2003/12/31 16:13:31  maj0r
 * Refactoring.
 *
 * Revision 1.16  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.15  2003/10/21 14:08:45  maj0r
 * Mittels PMD Code verschoenert, optimiert.
 *
 * Revision 1.14  2003/10/12 15:57:55  maj0r
 * Kleinere Bugs behoben.
 * Sortiert wird nun nur noch bei Klick auf den Spaltenkopf um CPU-Zeit zu sparen.
 *
 * Revision 1.13  2003/09/04 22:12:45  maj0r
 * Logger verfeinert.
 * Threadbeendigung korrigiert.
 *
 * Revision 1.12  2003/09/01 15:50:51  maj0r
 * Wo es moeglich war, DOs auf primitive Datentypen umgebaut.
 *
 * Revision 1.11  2003/08/04 14:28:55  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.10  2003/08/03 19:54:05  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.9  2003/07/04 10:35:42  maj0r
 * Lesen des Sockets geht nun wesentlich schneller.
 * Share wird daher wesentlich schneller angezeigt.
 *
 * Revision 1.8  2003/07/02 13:54:34  maj0r
 * JTreeTable komplett überarbeitet.
 *
 * Revision 1.7  2003/07/01 14:58:07  maj0r
 * Loggerüberwachung eingefügt und unnützen Kram entfernt.
 *
 * Revision 1.6  2003/07/01 06:17:16  maj0r
 * Code optimiert.
 *
 * Revision 1.5  2003/06/22 19:00:27  maj0r
 * Basisklasse umbenannt.
 *
 * Revision 1.4  2003/06/10 12:31:03  maj0r
 * Historie eingefuegt.
 *
 *
 */

public class ShareXMLHolder
    extends DefaultHandler {
    private Map shareMap;
    private Logger logger;
    private String host;
    private String password;
    private String xmlCommand;
    private XMLReader xr = null;

    public ShareXMLHolder() {
        logger = Logger.getLogger(getClass());
        try {
            xmlCommand = "/xml/share.xml?";
            ConnectionSettings rc = PropertiesManager.getOptionsManager().
                getRemoteSettings();
            host = rc.getHost();
            password = rc.getOldPassword();
            if (host == null || host.length() == 0) {
                host = "localhost";
            }
            if (host.compareToIgnoreCase("localhost") != 0 &&
                host.compareTo("127.0.0.1") != 0) {
                xmlCommand += "mode=zip&";
            }
            xmlCommand += "password=" + password;
            Class parser = SAXParser.class;
            xr = XMLReaderFactory.createXMLReader(parser.getName());
            xr.setContentHandler( this );
        }
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", ex);
            }
        }
    }

    private String getXMLString() throws
        Exception {
        String xmlData = null;
        xmlData = HtmlLoader.getHtmlXMLContent(host, HtmlLoader.GET,
                                               xmlCommand);
        if (xmlData.length() == 0) {
            throw new IllegalArgumentException();
        }
        return xmlData;
    }

    private void checkShareAttributes(Attributes attr){
        int id = -1;
        for (int i = 0; i < attr.getLength(); i++) {
            if (attr.getLocalName(i).equals("id")) {
                id = Integer.parseInt(attr.getValue(i));
                break;
            }
        }
        if (id == -1) {
            return;
        }
        ShareDO shareDO = new ShareDO(id);
        shareMap.put(Integer.toString(id), shareDO);
        for (int i = 0; i < attr.getLength(); i++) {
            if (attr.getLocalName(i).equals("filename")){
                shareDO.setFilename(attr.getValue(i));
            }
            else if (attr.getLocalName(i).equals("shortfilename")){
                shareDO.setShortfilename(attr.getValue(i));
            }
            else if (attr.getLocalName(i).equals("size")){
                shareDO.setSize(Long.parseLong(attr.getValue(i)));
            }
            else if (attr.getLocalName(i).equals("checksum")){
                shareDO.setChecksum(attr.getValue(i));
            }
            else if (attr.getLocalName(i).equals("priority")){
                shareDO.setPrioritaet(Integer.parseInt(attr.getValue(i)));
            }
        }
    }

    public void startElement(String namespaceURI,
                             String localName,
                             String qName,
                             Attributes attr) throws SAXException {
        if (localName.equals("share")){
            checkShareAttributes(attr);
        }
    }

    public void update() {
        try {
            String xmlString = getXMLString();
            if (shareMap == null) {
                shareMap = new HashMap();
            }
            xr.parse( new InputSource(
               new StringReader( xmlString )) );
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    public Map getShare() {
        update();
        return shareMap;
    }
}
