package de.applejuicenet.client.facade.controller.xml;

import de.applejuicenet.client.facade.controller.CoreConnectionSettingsHolder;
import de.applejuicenet.client.facade.entity.Directory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Vector;

/**
 *
 * Copyright: General Public License
 *
 */

public class DirectoryXMLHolder extends WebXMLParser {

	private String directory;

	private Vector<Directory> directories;

	public DirectoryXMLHolder(CoreConnectionSettingsHolder coreHolder) {
		super(coreHolder, "/xml/directory.xml", "");
	}

	public synchronized void update() {
		try {
			if (directory == null) {
				directory = "";
			}
			if (directory.length() == 0) {
				reload("", false);
			} else {
				reload("directory=" + URLEncoder.encode(directory, StandardCharsets.UTF_8),
						false);
			}
			Element e = null;
			NodeList nodes = document.getElementsByTagName("filesystem");
			if (nodes.getLength() == 0) {
				throw new IllegalStateException("The core answered without a <filesystem> element for directory '"
						+ directory + "'");
			}
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

	/**
	 * Loads one folder listing. The holder keeps the requested folder and the result in fields while it
	 * parses, so concurrent callers (share tree and folder chooser) would mix their results; calls are
	 * therefore serialized and every call returns its own list.
	 */
	public synchronized List<Directory> getDirectories(String directory) {
		this.directory = directory;
		directories = new Vector<Directory>();
		update();
		return directories;
	}
}
