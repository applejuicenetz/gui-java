package de.applejuicenet.client.fassade.controller.xml;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import de.applejuicenet.client.fassade.controller.CoreConnectionSettingsHolder;
import de.applejuicenet.client.fassade.controller.dac.ShareDO;
import de.applejuicenet.client.fassade.shared.HtmlLoader;

/**
 * $Header:
 * /cvsroot/applejuicejava/ajcorefassade/src/de/applejuicenet/client/fassade/controller/xmlholder/ShareXMLHolder.java,v
 * 1.1 2004/12/03 07:57:12 maj0r Exp $
 * 
 * <p>
 * Titel: AppleJuice Client-GUI
 * </p>
 * <p>
 * Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten
 * appleJuice-Core
 * </p>
 * <p>
 * Copyright: General Public License
 * </p>
 * 
 * @author: Maj0r <aj@tkl-soft.de>
 * 
 */

public class ShareXMLHolder extends DefaultHandler {

	private final CoreConnectionSettingsHolder coreHolder;

	private Map<String, ShareDO> shareMap;

	private String xmlCommand;

	private XMLReader xr = null;

	public ShareXMLHolder(CoreConnectionSettingsHolder coreHolder) {
		this.coreHolder = coreHolder;
		xmlCommand = "/xml/share.xml?";
		if (!coreHolder.isLocalhost()) {
			xmlCommand += "mode=zip&";
		}
		xmlCommand += "password=";
		Class parser = SAXParser.class;
		try {
			xr = XMLReaderFactory.createXMLReader(parser.getName());
			xr.setContentHandler(this);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private String getXMLString() throws Exception {
		String xmlData = null;
		xmlData = HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(),
				coreHolder.getCorePort(), HtmlLoader.GET, xmlCommand
						+ coreHolder.getCorePassword());
		if (xmlData.length() == 0) {
			throw new IllegalArgumentException();
		}
		return xmlData;
	}

	private void checkShareAttributes(Attributes attr) {
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
			if (attr.getLocalName(i).equals("filename")) {
				shareDO.setFilename(attr.getValue(i));
			} else if (attr.getLocalName(i).equals("shortfilename")) {
				shareDO.setShortfilename(attr.getValue(i));
			} else if (attr.getLocalName(i).equals("size")) {
				shareDO.setSize(Long.parseLong(attr.getValue(i)));
			} else if (attr.getLocalName(i).equals("checksum")) {
				shareDO.setChecksum(attr.getValue(i));
			} else if (attr.getLocalName(i).equals("priority")) {
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

	public void startElement(String namespaceURI, String localName,
			String qName, Attributes attr) throws SAXException {
		if (localName.equals("share")) {
			checkShareAttributes(attr);
		}
	}

	public void update() {
		try {
			String xmlString = getXMLString();
			if (shareMap == null) {
				shareMap = new HashMap<String, ShareDO>();
			}
			xr.parse(new InputSource(new StringReader(xmlString)));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Map<String, ShareDO> getShare() {
		update();
		return shareMap;
	}
}
