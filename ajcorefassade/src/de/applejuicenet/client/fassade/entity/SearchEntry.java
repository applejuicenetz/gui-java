package de.applejuicenet.client.fassade.entity;

import de.applejuicenet.client.fassade.shared.FileType;

public interface SearchEntry extends IdOwner{

	public int getId();

	public FileType getFileType();

	public int getSearchId();

	public String getChecksumme();

	public long getGroesse();

	public FileName[] getFileNames();
}
