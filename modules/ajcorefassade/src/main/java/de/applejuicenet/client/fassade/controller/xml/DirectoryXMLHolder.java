package de.applejuicenet.client.fassade.controller.xml;

import de.applejuicenet.client.fassade.controller.CoreConnectionSettingsHolder;
import de.applejuicenet.client.fassade.entity.Directory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.net.URLEncoder;
import java.util.List;
import java.util.Vector;

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
 * @author Maj0r <aj@tkl-soft.de>
 * 
 */

public class DirectoryXMLHolder extends WebXMLParser {

	private String directory;

	private Vector<Directory> directories;

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
				getNodes(e);
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private void getNodes(Element element) {
		int type;
		boolean fileSystem;
		String name;
		String path;
		name = element.getAttribute("name");
		if (name.equals("//")) {
			name = "/";
		}
		fileSystem = element.getAttribute("isfilesystem").equalsIgnoreCase(
				"true");
		type = Integer.parseInt(element.getAttribute("type"));
		path = element.getAttribute("path");
		if (path.length() == 0) {
			if (directory != null) {
				if (directory.length() != 0
						&& directory.lastIndexOf(
								DirectoryDO.getSeparator()) == directory
								.length() - 1) {
					path = directory + name;
				} else {
					if (directory.length() == 0){
						path = name;
					}
					else{
						path = directory + DirectoryDO.getSeparator() + name;
					}
				}
			} else {
				path = DirectoryDO.getSeparator();
			}
		}
		DirectoryDO childDO = new DirectoryDO(name, type, fileSystem, path);
		directories.add(childDO);
	}

	public List<Directory> getDirectories(String directory) {
		this.directory = directory;
		directories = new Vector<Directory>();
		update();
		return directories;
	}
}
