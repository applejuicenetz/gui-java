package de.applejuicenet.client.fassade.entity;

public interface SearchEntry {

	public int getId();

	public String getFileType();

	public int getSearchId();

	public String getChecksumme();

	public long getGroesse();

	public FileName[] getFileNames();
}
