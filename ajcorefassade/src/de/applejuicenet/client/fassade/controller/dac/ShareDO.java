package de.applejuicenet.client.fassade.controller.dac;

/**
 * $Header:
 * /cvsroot/applejuicejava/ajcorefassade/src/de/applejuicenet/client/fassade/controller/dac/ShareDO.java,v
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
 * @author: Maj0r <aj@tkl-soft.de>
 * 
 */

public class ShareDO {
	private final int id;

	private String filename;
	private String shortfilename;
	private long size;
	private String checksum;
	private int prioritaet;
    private long lastAsked;
    private long askCount;
    private long searchCount;

	public ShareDO(int id) {
		this.id = id;
	}

	public ShareDO(int id, String filename, String shortfilename, long size,
			String checksum, int prioritaet, long lastAsked, long askCount, 
			   long searchCount) {
		this.id = id;
		this.filename = filename;
		this.shortfilename = shortfilename;
		this.size = size;
		this.checksum = checksum;
		this.prioritaet = prioritaet;
        this.lastAsked = lastAsked;
        this.askCount = askCount;
        this.searchCount = searchCount;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	public int getId() {
		return id;
	}

	public String getFilename() {
		return filename;
	}

	public long getSize() {
		return size;
	}

	public String getCheckSum() {
		return checksum;
	}

	public String getShortfilename() {
		return shortfilename;
	}

	public void setShortfilename(String shortfilename) {
		this.shortfilename = shortfilename;
	}

	public int getPrioritaet() {
		return prioritaet;
	}

	public void setPrioritaet(int prioritaet) {
		this.prioritaet = prioritaet;
	}

	public long getAskCount() {
		return askCount;
	}
	
	public void setAskCount(long askCount) {
		this.askCount = askCount;
	}
	
	public long getLastAsked() {
		return lastAsked;
	}
	
	public void setLastAsked(long lastAsked) {
		this.lastAsked = lastAsked;
	}
	
	public long getSearchCount() {
		return searchCount;
	}

	public void setSearchCount(long searchCount) {
		this.searchCount = searchCount;
	}
}
