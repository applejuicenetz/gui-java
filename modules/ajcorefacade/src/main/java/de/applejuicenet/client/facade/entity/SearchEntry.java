package de.applejuicenet.client.facade.entity;

import de.applejuicenet.client.facade.shared.FileType;

public interface SearchEntry extends IdOwner{

	public int getId();

	public FileType getFileType();

	public int getSearchId();

	public String getChecksum();

	public long getSize();

	public FileName[] getFileNames();
}
