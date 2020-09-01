package de.applejuicenet.client.fassade.entity;

public interface Share extends IdOwner{

	int getId();

	String getFilename();

	long getSize();

	String getCheckSum();

	String getShortfilename();

	int getPrioritaet();

	long getAskCount();

	long getLastAsked();

	long getSearchCount();

	String getAjfspLink();
}
