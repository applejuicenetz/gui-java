package de.applejuicenet.client.facade.entity;

import de.applejuicenet.client.facade.shared.FileType;

public interface FileName {

	String getFileName();

	int getFrequency();

	FileType getFileType();	
}
