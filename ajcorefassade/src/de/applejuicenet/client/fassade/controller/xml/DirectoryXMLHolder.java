package de.applejuicenet.client.fassade.controller.xml;

import java.net.URLEncoder;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.applejuicenet.client.fassade.controller.CoreConnectionSettingsHolder;

/**
 * $Header:
 * /cvsroot/applejuicejava/ajcorefassade/src/de/applejuicenet/client/fassade/controller/xmlholder/DirectoryXMLHolder.java,v
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

public class DirectoryXMLHolder extends WebXMLParser {

	private String directory;

	private DirectoryDO directoryDO;

	public DirectoryXMLHolder(CoreConnectionSettingsHolder coreHolder) {
		super(coreHolder, "/xml/directory.xml", "");
	}

	public void update() {
		try {
			if (directory == null) {
				directory = "";
			}
			if (directory.length() == 0) {
				reload("", false);
			} else {
				reload("directory=" + URLEncoder.encode(directory, "UTF-8"),
						false);
			}
			Element e = null;
			NodeList nodes = document.getElementsByTagName("filesystem");
			e = (Element) nodes.item(0);
			DirectoryDO.setSeparator(e.getAttribute("seperator"));
			nodes = document.getElementsByTagName("dir");
			for (int i = 0; i < nodes.getLength(); i++) {
				e = (Element) nodes.item(i);
				getNodes(e, directoryDO);
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private void getNodes(Element element, DirectoryDO directoryDO) {
		int type;
		boolean fileSystem;
		String name;
		String path;
		name = element.getAttribute("name");
		fileSystem = element.getAttribute("isfilesystem").equalsIgnoreCase(
				"true");
		type = Integer.parseInt(element.getAttribute("type"));
		path = element.getAttribute("path");
		if (path.length() == 0) {
			if (directoryDO != null) {
				String parentPfad = directoryDO.getPath();
				if (parentPfad.length() != 0
						&& parentPfad.lastIndexOf(DirectoryDO.getSeparator()) == parentPfad
								.length() - 1) {
					path = parentPfad + name;
				} else {
					path = parentPfad + DirectoryDO.getSeparator() + name;
				}
			} else {
				path = DirectoryDO.getSeparator();
			}
		}
		DirectoryDO childDO = new DirectoryDO(name, type, fileSystem, path);
		directoryDO.addChild(directoryDO);
	}

	public void getDirectory(String directory, DirectoryDO directoryDO) {
		this.directory = directory;
		this.directoryDO = directoryDO;
		update();
	}
}
