package de.applejuicenet.client.gui.controller.xmlholder;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/xmlholder/Attic/PartListXMLHolder.java,v 1.4 2004/02/18 20:44:37 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fï¿½r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: PartListXMLHolder.java,v $
 * Revision 1.4  2004/02/18 20:44:37  maj0r
 * Bugs #223 und #224 behoben.
 *
 * Revision 1.3  2004/02/18 18:57:23  maj0r
 * Von DOM auf SAX umgebaut.
 *
 * Revision 1.2  2004/02/18 18:43:04  maj0r
 * Von DOM auf SAX umgebaut.
 *
 * Revision 1.1  2004/02/18 17:25:04  maj0r
 * Von DOM auf SAX umgebaut.
 *
 * Revision 1.4  2004/02/16 07:42:43  maj0r
 * alten Timestampfehler beseitig
 * Trotz Sessionumsetzung wurde immer noch der Timestamp mitgeschleppt.
 *
 * Revision 1.3  2004/02/05 23:11:28  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.2  2004/01/06 17:32:50  maj0r
 * Es wird nun zweimal versucht den Core erneut zu erreichen, wenn die Verbindung unterbrochen wurde.
 *
 * Revision 1.1  2003/12/31 16:13:31  maj0r
 * Refactoring.
 *
 * Revision 1.9  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.8  2003/10/21 14:08:45  maj0r
 * Mittels PMD Code verschoenert, optimiert.
 *
 * Revision 1.7  2003/10/13 19:14:04  maj0r
 * Kleinen Bug beim Entfernen von Downloads gefixt.
 *
 * Revision 1.6  2003/10/12 15:57:55  maj0r
 * Kleinere Bugs behoben.
 * Sortiert wird nun nur noch bei Klick auf den Spaltenkopf um CPU-Zeit zu sparen.
 *
 * Revision 1.5  2003/10/04 15:29:12  maj0r
 * Userpartliste hinzugefuegt.
 *
 * Revision 1.4  2003/09/11 06:54:15  maj0r
 * Auf neues Sessions-Prinzip umgebaut.
 * Sprachenwechsel korrigert, geht nun wieder flott.
 *
 * Revision 1.3  2003/09/01 18:00:15  maj0r
 * Wo es ging, DO auf primitiven Datentyp umgebaut.
 * Status "geprueft" eingefuehrt.
 *
 * Revision 1.2  2003/09/01 06:27:35  maj0r
 * Ueberarbeitet.
 *
 * Revision 1.1  2003/08/11 14:10:28  maj0r
 * DownloadPartList eingefügt.
 * Diverse Änderungen.
 *
 *
 */

import java.io.CharArrayWriter;
import java.io.StringReader;

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
import de.applejuicenet.client.shared.dac.DownloadDO;
import de.applejuicenet.client.shared.dac.DownloadSourceDO;
import de.applejuicenet.client.shared.dac.PartListDO;
import de.applejuicenet.client.shared.dac.PartListDO.Part;
import org.apache.log4j.Level;
import org.apache.xerces.parsers.SAXParser;

public class PartListXMLHolder
    extends DefaultHandler {

    private Logger logger;
    private String host;
    private String password;
    private String xmlCommand;
    private XMLReader xr = null;
    private static PartListXMLHolder instance = null;
    private CharArrayWriter contents = new CharArrayWriter();
    private PartListDO partListDO;
    private String zipMode;

    private PartListXMLHolder() {
        logger = Logger.getLogger(getClass());
        try {
            ConnectionSettings rc = PropertiesManager.getOptionsManager().
                getRemoteSettings();
            host = rc.getHost();
            password = rc.getOldPassword();
            if (host == null || host.length() == 0) {
                host = "localhost";
            }
            if (host.compareToIgnoreCase("localhost") != 0 &&
                host.compareTo("127.0.0.1") != 0) {
                zipMode = "mode=zip&";
            }
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

    public static synchronized PartListXMLHolder getInstance(){
        if (instance == null){
            instance = new PartListXMLHolder();
        }
        return instance;
    }

    private void checkInformationAttributes(Attributes attr){
        for (int i = 0; i < attr.getLength(); i++) {
            if (attr.getLocalName(i).equals("filesize")){
                partListDO.setGroesse(Long.parseLong(attr.getValue(i)));
            }
        }
    }

    private void checkPartAttributes(Attributes attr){
        long startPosition = -1;
        int type = -1;
        for (int i = 0; i < attr.getLength(); i++) {
            if (attr.getLocalName(i).equals("fromposition")){
                startPosition = Long.parseLong(attr.getValue(i));
            }
            else if (attr.getLocalName(i).equals("type")){
                type = Integer.parseInt(attr.getValue(i));
            }
        }
        partListDO.addPart(partListDO.new Part(startPosition, type));
    }

    public void startElement(String namespaceURI,
                             String localName,
                             String qName,
                             Attributes attr) throws SAXException {
        contents.reset();
        if (localName.equals("fileinformation")){
            checkInformationAttributes(attr);
        }
        else if (localName.equals("part")){
            checkPartAttributes(attr);
        }
    }

    public void characters(char[] ch, int start, int length) throws
        SAXException {
        contents.write(ch, start, length);
    }

    private String getXMLString(String parameters) throws
        Exception {
        String xmlData = null;
        String command = xmlCommand + zipMode + "password=" + password + parameters;
        xmlData = HtmlLoader.getHtmlXMLContent(host, HtmlLoader.GET,
                                               command);
        if (xmlData.length() == 0) {
            throw new IllegalArgumentException();
        }
        return xmlData;
    }

    public PartListDO getPartList(Object object) {
        try {
            String xmlString;
            if (object.getClass()==DownloadSourceDO.class){
                xmlCommand = "/xml/userpartlist.xml?";
                xmlString = getXMLString("&id=" +
                                                ( (DownloadSourceDO) object).
                                                getId());
                partListDO = new PartListDO( (DownloadSourceDO)object);
            }
            else{
                xmlCommand = "/xml/downloadpartlist.xml?";
                xmlString = getXMLString("&id=" + ((DownloadDO)object).getId());
                partListDO = new PartListDO((DownloadDO)object);
            }
            xr.parse( new InputSource(
               new StringReader( xmlString )) );
            return partListDO;
        }
        catch (Exception e) {
            return null;
        }
    }
}
