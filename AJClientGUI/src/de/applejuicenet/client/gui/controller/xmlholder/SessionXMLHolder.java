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
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.shared.ConnectionSettings;
import de.applejuicenet.client.shared.HtmlLoader;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/xmlholder/Attic/SessionXMLHolder.java,v 1.8 2004/05/23 17:58:29 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
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
            ConnectionSettings rc = OptionsManagerImpl.getInstance().
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
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
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
            if (logger.isEnabledFor(Level.DEBUG)) {
                logger.debug(ApplejuiceFassade.ERROR_MESSAGE, ex);
            }
            return "";
        }
    }
}
