package de.applejuicenet.client.facade.entity;

public interface Share extends IdOwner{

	int getId();

	String getFilename();

	long getSize();

	String getCheckSum();

	String getShortfilename();

	int getPriority();

	long getAskCount();

	long getLastAsked();

	long getSearchCount();

	String getAjfspLink();
}
