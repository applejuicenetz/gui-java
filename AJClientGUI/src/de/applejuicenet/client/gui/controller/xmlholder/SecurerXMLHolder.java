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
import de.applejuicenet.client.gui.controller.PropertiesManager;
import de.applejuicenet.client.gui.listener.DataUpdateListener;
import de.applejuicenet.client.shared.ConnectionSettings;
import de.applejuicenet.client.shared.HtmlLoader;
import de.applejuicenet.client.shared.Information;
import de.applejuicenet.client.shared.exception.WebSiteNotFoundException;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/xmlholder/Attic/SecurerXMLHolder.java,v 1.8 2004/03/09 16:25:17 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fï¿½r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: SecurerXMLHolder.java,v $
 * Revision 1.8  2004/03/09 16:25:17  maj0r
 * PropertiesManager besser gekapselt.
 *
 * Revision 1.7  2004/03/05 15:49:39  maj0r
 * PMD-Optimierung
 *
 * Revision 1.6  2004/03/03 15:33:31  maj0r
 * PMD-Optimierung
 *
 * Revision 1.5  2004/02/24 14:12:53  maj0r
 * DOM->SAX-Umstellung
 *
 * Revision 1.4  2004/02/05 23:11:28  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.3  2004/01/30 16:32:31  maj0r
 * Verbindung sollte nun auch bei belasteten Verbindungen aufgebaut werden.
 * MapSetStringKey ausgebaut.
 *
 * Revision 1.2  2004/01/29 13:47:57  maj0r
 * Während des ersten Holens der Quellen wird nun alle 5 Seks die Statuszeile aktualisiert.
 *
 * Revision 1.1  2004/01/28 12:34:46  maj0r
 * Session wird nun besser aufrecht erhalten.
 *
 *
 */

public class SecurerXMLHolder
    extends DefaultHandler {
    private Logger logger;
    private String host;
    private String password;
    private String xmlCommand;
    private XMLReader xr = null;
    private Information information = null;
    private static SecurerXMLHolder instance = null;

    private SecurerXMLHolder() {
        logger = Logger.getLogger(getClass());
        try {
            ConnectionSettings rc = OptionsManagerImpl.getInstance().
                getRemoteSettings();
            host = rc.getHost();
            password = rc.getOldPassword();
            if (host == null || host.length() == 0) {
                host = "localhost";
            }
            xmlCommand = "/xml/modified.xml?filter=informations&password=" + password;
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

    public static synchronized SecurerXMLHolder getInstance(){
        if (instance == null){
            instance = new SecurerXMLHolder();
        }
        return instance;
    }

    public void startElement(String namespaceURI,
                             String localName,
                             String qName,
                             Attributes attr) throws SAXException {
        if (localName.equals("information")){
            checkInformationAttributes(attr);
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

    public boolean secure(String sessionKontext, Information information) {
        try {
            if (information == null){
                return false;
            }
            this.information = information;
            String xmlString = getXMLString();
            xr.parse( new InputSource(
               new StringReader( xmlString )) );
            return true;
        }
        catch (WebSiteNotFoundException wsnfE) {
            //Verbindung zum Core ueberlastet.
            return false;
        }
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
            }
            return false;
        }
    }

    private void checkInformationAttributes(Attributes attr){
        for (int i = 0; i < attr.getLength(); i++) {
            if (attr.getLocalName(i).equals("credits")){
                information.setCredits(Long.parseLong(attr.getValue(i)));
            }
            else if (attr.getLocalName(i).equals("sessionupload")){
                information.setSessionUpload(Long.parseLong(attr.getValue(i)));
            }
            else if (attr.getLocalName(i).equals("sessiondownload")){
                information.setSessionDownload(Long.parseLong(attr.getValue(i)));
            }
            else if (attr.getLocalName(i).equals("uploadspeed")){
                information.setUploadSpeed(Long.parseLong(attr.getValue(i)));
            }
            else if (attr.getLocalName(i).equals("downloadspeed")){
                information.setDownloadSpeed(Long.parseLong(attr.getValue(i)));
            }
            else if (attr.getLocalName(i).equals("openconnections")){
                information.setOpenConnections(Long.parseLong(attr.getValue(i)));
            }
        }
        ApplejuiceFassade.getInstance().informDataUpdateListener(
            DataUpdateListener.INFORMATION_CHANGED);
    }
}