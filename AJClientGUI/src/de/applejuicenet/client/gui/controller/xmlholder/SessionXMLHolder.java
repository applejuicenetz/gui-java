package de.applejuicenet.client.gui.controller.xmlholder;

import java.io.StringReader;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;
import de.applejuicenet.client.gui.controller.PropertiesManager;
import de.applejuicenet.client.shared.ConnectionSettings;
import de.applejuicenet.client.shared.HtmlLoader;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/xmlholder/Attic/SessionXMLHolder.java,v 1.4 2004/02/24 14:12:53 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: SessionXMLHolder.java,v $
 * Revision 1.4  2004/02/24 14:12:53  maj0r
 * DOM->SAX-Umstellung
 *
 * Revision 1.3  2004/02/16 07:42:43  maj0r
 * alten Timestampfehler beseitig
 * Trotz Sessionumsetzung wurde immer noch der Timestamp mitgeschleppt.
 *
 * Revision 1.2  2004/02/05 23:11:28  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.1  2003/12/31 16:13:31  maj0r
 * Refactoring.
 *
 * Revision 1.2  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.1  2003/09/10 15:30:48  maj0r
 * Begonnen auf neue Session-Struktur umzubauen.
 *
 * Revision 1.4  2003/06/22 19:00:27  maj0r
 * Basisklasse umbenannt.
 *
 * Revision 1.3  2003/06/10 12:31:03  maj0r
 * Historie eingefuegt.
 *
 *
 */

public class SessionXMLHolder
    extends DefaultHandler {

    private static SessionXMLHolder instance = null;

    private Logger logger;
    private String host;
    private String password;
    private String xmlCommand;
    private XMLReader xr = null;
    private String sessionId = "";

    private SessionXMLHolder() {
        logger = Logger.getLogger(getClass());
        try {
            ConnectionSettings rc = PropertiesManager.getOptionsManager().
                getRemoteSettings();
            host = rc.getHost();
            password = rc.getOldPassword();
            if (host == null || host.length() == 0) {
                host = "localhost";
            }
            xmlCommand = "/xml/getsession.xml?password=" + password;
            Class parser = SAXParser.class;
            xr = XMLReaderFactory.createXMLReader(parser.getName());
            xr.setContentHandler(this);
        }
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", ex);
            }
        }
    }

    public static synchronized SessionXMLHolder getInstance(){
        if (instance==null){
            instance = new SessionXMLHolder();
        }
        return instance;
    }

    private void checkSessionAttributes(Attributes attr){
        for (int i = 0; i < attr.getLength(); i++) {
            if (attr.getLocalName(i).equals("id")){
                sessionId = attr.getValue(i);
            }
        }
    }

    public void startElement(String namespaceURI,
                             String localName,
                             String qName,
                             Attributes attr) throws SAXException {
        if (localName.equals("session")){
            checkSessionAttributes(attr);
        }
    }

    private String getXMLString() throws
        Exception {
        String xmlData = HtmlLoader.getHtmlXMLContent(host, HtmlLoader.GET,
                                               xmlCommand);
        if (xmlData.length() == 0) {
            throw new IllegalArgumentException();
        }
        return xmlData;
    }

    public String getNewSessionId(){
        try{
            String xmlString = getXMLString();
            xr.parse(new InputSource(
                new StringReader(xmlString)));
            return sessionId;
        }
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", ex);
            }
            return "";
        }
    }
}
