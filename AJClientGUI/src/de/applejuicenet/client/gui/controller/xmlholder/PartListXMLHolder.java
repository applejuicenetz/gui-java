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
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.shared.ConnectionSettings;
import de.applejuicenet.client.shared.HtmlLoader;
import de.applejuicenet.client.shared.dac.DownloadDO;
import de.applejuicenet.client.shared.dac.DownloadSourceDO;
import de.applejuicenet.client.shared.dac.PartListDO;
import de.applejuicenet.client.shared.dac.PartListDO.Part;
import de.applejuicenet.client.shared.exception.WebSiteNotFoundException;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/xmlholder/Attic/PartListXMLHolder.java,v 1.10 2004/04/14 10:25:22 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

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
    private String zipMode = "";

    private PartListXMLHolder() {
        logger = Logger.getLogger(getClass());
        try {
            ConnectionSettings rc = OptionsManagerImpl.getInstance().
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
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
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
        WebSiteNotFoundException {
        String xmlData = null;
        String command = xmlCommand + zipMode + "password=" + password + parameters;
        xmlData = HtmlLoader.getHtmlXMLContent(host, HtmlLoader.GET,
                                               command);
        if (xmlData.length() == 0) {
            throw new IllegalArgumentException();
        }
        return xmlData;
    }

    public PartListDO getPartList(Object object) throws WebSiteNotFoundException{
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
            PartListDO resultPartList = partListDO;
            partListDO = null;
            return resultPartList;
        }
        catch (Exception e) {
            return null;
        }
    }
}
