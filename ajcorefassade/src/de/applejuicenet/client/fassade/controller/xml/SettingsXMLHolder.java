package de.applejuicenet.client.fassade.controller.xml;

import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.applejuicenet.client.fassade.controller.CoreConnectionSettingsHolder;
import de.applejuicenet.client.fassade.shared.AJSettings;
import de.applejuicenet.client.fassade.shared.ShareEntry;

/**
 * $Header:
 * /cvsroot/applejuicejava/ajcorefassade/src/de/applejuicenet/client/fassade/controller/xmlholder/SettingsXMLHolder.java,v
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

public class SettingsXMLHolder extends WebXMLParser {

	private AJSettings settings;

	public SettingsXMLHolder(CoreConnectionSettingsHolder coreHolder) {
		super(coreHolder, "/xml/settings.xml", "");
	}

	public void update() {
		try {
			reload("", false);
		} catch (Exception e1) {
			throw new RuntimeException(e1);
		}
		NodeList nodes = document.getElementsByTagName("nick");
		String nick = nodes.item(0).getFirstChild().getNodeValue();
		nodes = document.getElementsByTagName("port");
		long port = Long
				.parseLong(nodes.item(0).getFirstChild().getNodeValue());
		nodes = document.getElementsByTagName("xmlport");
		long xmlPort = Long.parseLong(nodes.item(0).getFirstChild()
				.getNodeValue());
		nodes = document.getElementsByTagName("maxupload");
		long maxUpload = Long.parseLong(nodes.item(0).getFirstChild()
				.getNodeValue());
		nodes = document.getElementsByTagName("maxdownload");
		long maxDownload = Long.parseLong(nodes.item(0).getFirstChild()
				.getNodeValue());
		nodes = document.getElementsByTagName("maxconnections");
		long maxConnections = Long.parseLong(nodes.item(0).getFirstChild()
				.getNodeValue());
		nodes = document.getElementsByTagName("maxsourcesperfile");
		long maxSourcesPerFile = Long.parseLong(nodes.item(0).getFirstChild()
				.getNodeValue());
		nodes = document.getElementsByTagName("autoconnect");
		boolean autoConnect = new Boolean(nodes.item(0).getFirstChild()
				.getNodeValue()).booleanValue();
		nodes = document.getElementsByTagName("speedperslot");
		int speedPerSlot = Integer.parseInt(nodes.item(0).getFirstChild()
				.getNodeValue());
		nodes = document.getElementsByTagName("maxnewconnectionsperturn");
		long maxNewConnectionsPerTurn = Long.parseLong(nodes.item(0)
				.getFirstChild().getNodeValue());
		nodes = document.getElementsByTagName("incomingdirectory");
		String incomingDir = nodes.item(0).getFirstChild().getNodeValue();
		nodes = document.getElementsByTagName("temporarydirectory");
		String tempDir = nodes.item(0).getFirstChild().getNodeValue();
		Set shareEntries = new HashSet();
		nodes = document.getElementsByTagName("directory");
		Element e = null;
		String dir = null;
		String shareMode = null;
		ShareEntry entry = null;
		int nodesSize = nodes.getLength();
		for (int i = 0; i < nodesSize; i++) {
			e = (Element) nodes.item(i);
			dir = e.getAttribute("name");
			shareMode = e.getAttribute("sharemode");
			entry = new ShareEntry(dir, shareMode);
			shareEntries.add(entry);
		}
		settings = new AJSettings(nick, port, xmlPort, maxUpload, maxDownload,
				speedPerSlot, incomingDir, tempDir, shareEntries,
				maxConnections, autoConnect, maxNewConnectionsPerTurn,
				maxSourcesPerFile);
	}

	public AJSettings getAJSettings() {
		update();
		return settings;
	}

	public AJSettings getCurrentAJSettings() {
		if (settings == null) {
			update();
		}
		return settings;
	}

}
