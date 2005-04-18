package de.applejuicenet.client.fassade.controller.xml;

import de.applejuicenet.client.fassade.entity.Upload;

/**
 * $Header:
 * /cvsroot/applejuicejava/ajcorefassade/src/de/applejuicenet/client/fassade/controller/dac/UploadDO.java,v
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

class UploadDO extends Upload{

	private final int uploadID;
	private String dateiName;
	private int shareFileID;
	private VersionDO version = null;
	private int status;
	private String nick;
	private long uploadFrom;
	private long uploadTo;
	private long actualUploadPosition;
	private int speed;
	private int prioritaet;
	private int directstate;
	private long lastConnection;
	private boolean progressChanged = false;
	private boolean wholeLoadedProgressChanged = false;
	private boolean versionChanged = false;
	private double loaded;

	public UploadDO(int uploadID) {
		this.uploadID = uploadID;
	}

	public UploadDO(int uploadID, int shareFileID, VersionDO version,
			String status, String nick, long uploadFrom, long uploadTo,
			long actualUploadPosition, int speed, int prioritaet,
			int directstate, long lastConnection, double loaded) {
		this.uploadID = uploadID;
		this.shareFileID = shareFileID;
		this.version = version;
		this.status = Integer.parseInt(status);
		this.nick = nick;
		this.uploadFrom = uploadFrom;
		this.uploadTo = uploadTo;
		this.actualUploadPosition = actualUploadPosition;
		this.speed = speed;
		this.prioritaet = prioritaet;
		this.directstate = directstate;
		this.lastConnection = lastConnection;
		this.setLoaded(loaded);
		progressChanged = true;
		versionChanged = true;
	}

	public int getUploadID() {
		return uploadID;
	}

	public int getShareFileID() {
		return shareFileID;
	}

	public void setShareFileID(int shareFileID) {
		this.shareFileID = shareFileID;
	}

	public VersionDO getVersion() {
		return version;
	}

	public void setVersion(VersionDO version) {
		this.version = version;
		versionChanged = true;
	}

	public int getStatus() {
		return status;
	}

	public int getDirectState() {
		return directstate;
	}

	public void setDirectState(int directstate) {
		this.directstate = directstate;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public long getUploadFrom() {
		return uploadFrom;
	}

	public void setUploadFrom(long uploadFrom) {
		this.uploadFrom = uploadFrom;
		progressChanged = true;
	}

	public long getUploadTo() {
		return uploadTo;
	}

	public void setUploadTo(long uploadTo) {
		this.uploadTo = uploadTo;
		progressChanged = true;
	}

	public long getActualUploadPosition() {
		return actualUploadPosition;
	}

	public void setActualUploadPosition(long actualUploadPosition) {
		this.actualUploadPosition = actualUploadPosition;
		progressChanged = true;
	}

	public int getSpeed() {
		return speed;
	}

	public void setPrioritaet(int prioritaet) {
		this.prioritaet = prioritaet;
	}

	public int getPrioritaet() {
		return prioritaet;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public String getDateiName() {
		return dateiName;
	}

	public long getLastConnection() {
		return lastConnection;
	}

	public double getLoaded() {
		return loaded;
	}

	public void setDateiName(String dateiName) {
		this.dateiName = dateiName;
	}

	public void setLastConnection(long lastConnection) {
		this.lastConnection = lastConnection;
	}

	public void setLoaded(double newValue) {
		if (newValue == -1) {
			if (newValue != loaded) {
				wholeLoadedProgressChanged = true;
				loaded = -1;
			}
		} else {
			double tmp = newValue * 100;
			if (tmp != loaded) {
				wholeLoadedProgressChanged = true;
				loaded = tmp;
			}
		}
	}
}
