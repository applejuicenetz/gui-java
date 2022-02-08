package de.applejuicenet.client.fassade.controller.xml;

import de.applejuicenet.client.fassade.entity.Directory;

import java.util.ArrayList;
import java.util.List;

/**
 * $Header:
 * /cvsroot/applejuicejava/ajcorefassade/src/de/applejuicenet/client/fassade/controller/dac/DirectoryDO.java,v
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

class DirectoryDO extends Directory {

	private String name;
	private int type;
	private boolean fileSystem;
	private String path;
	private List<Directory> children = null;

	//public DirectoryDO() {}

	public DirectoryDO(String name, int type, boolean fileSystem, String path) {
		this.name = name;
		this.type = type;
		this.fileSystem = fileSystem;
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isFileSystem() {
		return fileSystem;
	}

	public void setFileSystem(boolean fileSystem) {
		this.fileSystem = fileSystem;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void addChild(Directory directory) {
		if (directory == null) {
			return;
		}
		if (children == null) {
			children = new ArrayList<Directory>();
		}
		children.add(directory);
	}

	public Directory[] getChildren() {
		if (children == null) {
			return null;
		}
		return (Directory[]) children
				.toArray(new Directory[children.size()]);
	}
}
