package de.applejuicenet.client.gui.controller.xmlholder;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

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
import de.applejuicenet.client.shared.dac.ShareDO;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/xmlholder/Attic/ShareXMLHolder.java,v 1.12 2004/12/03 17:31:35 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
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
            ConnectionSettings rc = OptionsManagerImpl.getInstance().
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
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
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
            else if (attr.getLocalName(i).equals("lastasked")){
                shareDO.setLastAsked(Long.parseLong(attr.getValue(i)));
            }
            else if (attr.getLocalName(i).equals("askcount")){
                shareDO.setAskCount(Long.parseLong(attr.getValue(i)));
            }
            else if (attr.getLocalName(i).equals("searchcount")){
                shareDO.setSearchCount(Long.parseLong(attr.getValue(i)));
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
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
        }
    }

    public Map getShare() {
        update();
        return shareMap;
    }
}
