package de.applejuicenet.client.facade.controller.xml;

import de.applejuicenet.client.facade.entity.Directory;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Copyright: General Public License
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
		return children
				.toArray(new Directory[children.size()]);
	}
}
