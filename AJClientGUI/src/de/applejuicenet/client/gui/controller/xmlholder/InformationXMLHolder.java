package de.applejuicenet.client.gui.controller.xmlholder;

import java.io.CharArrayWriter;
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
import de.applejuicenet.client.shared.ConnectionSettings;
import de.applejuicenet.client.shared.HtmlLoader;
import de.applejuicenet.client.shared.Version;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/xmlholder/Attic/InformationXMLHolder.java,v 1.5 2004/03/09 16:25:17 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: InformationXMLHolder.java,v $
 * Revision 1.5  2004/03/09 16:25:17  maj0r
 * PropertiesManager besser gekapselt.
 *
 * Revision 1.4  2004/03/05 15:49:39  maj0r
 * PMD-Optimierung
 *
 * Revision 1.3  2004/02/27 15:05:19  maj0r
 * Auf SAX umgebastelt.
 *
 * Revision 1.2  2004/02/05 23:11:28  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.1  2003/12/31 16:13:31  maj0r
 * Refactoring.
 *
 * Revision 1.5  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.4  2003/06/22 19:00:27  maj0r
 * Basisklasse umbenannt.
 *
 * Revision 1.3  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class InformationXMLHolder
    extends DefaultHandler {

    private Version version = null;
    private static InformationXMLHolder instance = null;
    private String host;
    private Logger logger;
    private XMLReader xr = null;
    private String xmlCommand = "/xml/information.xml?password=";
    private CharArrayWriter contents = new CharArrayWriter();
    private String coreVersion;

    private InformationXMLHolder() {
        logger = Logger.getLogger(getClass());
        try {
            ConnectionSettings rc = OptionsManagerImpl.getInstance().
                getRemoteSettings();
            host = rc.getHost();
            String password = rc.getOldPassword();
            if (host == null || host.length() == 0) {
                host = "localhost";
            }
            xmlCommand = "/xml/information.xml?password=" + password;
            Class parser = SAXParser.class;
            xr = XMLReaderFactory.createXMLReader(parser.getName());
            xr.setContentHandler(this);
        }
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)){
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
            }
        }
    }

    public static InformationXMLHolder getInstance(){
        if (instance==null){
            instance = new InformationXMLHolder();
        }
        return instance;
    }

    public Version getCoreVersion(){
        if (version==null){
            try {
                String xmlString = getXMLString();
                xr.parse(new InputSource(
                    new StringReader(xmlString)));
                version = new Version(coreVersion,
                                      Version.getOSTypByOSName( (
                    String) System.
                    getProperties().get("os.name")));
            }
            catch (Exception ex) {
                if (logger.isEnabledFor(Level.ERROR)) {
                    logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
                }
            }
        }
        return version;
    }

    public void startElement(String namespaceURI,
                             String localName,
                             String qName,
                             Attributes attr) throws SAXException {
        contents.reset();
        if (localName.equals("filesystem")){
            for (int i = 0; i < attr.getLength(); i++) {
                if (attr.getLocalName(i).equals("seperator")) {
                    ApplejuiceFassade.separator = attr.getValue(i);
                    break;
                }
            }
       }
    }

    public void characters(char[] ch, int start, int length) throws
        SAXException {
        contents.write(ch, start, length);
    }

    public void endElement(String namespaceURI,
                           String localName,
                           String qName) throws SAXException {
        if (localName.equals("version")) {
            coreVersion = contents.toString();
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
}