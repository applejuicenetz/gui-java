package de.applejuicenet.client.fassade.entity;

import de.applejuicenet.client.fassade.shared.FileType;

public interface FileName {

	String getDateiName();

	int getHaeufigkeit();

	FileType getFileType();	
}
