package de.applejuicenet.client.fassade.controller.xml;

import de.applejuicenet.client.fassade.entity.Information;
import de.applejuicenet.client.fassade.entity.Server;

/**
 * $Header:
 * /cvsroot/applejuicejava/ajcorefassade/src/de/applejuicenet/client/fassade/controller/dac/InformationDO.java,v
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

class InformationDO extends Information{

	private int verbindungsStatus = NICHT_VERBUNDEN;

	private int id;
	private long sessionUpload;
	private long sessionDownload;
	private long credits;
	private long uploadSpeed;
	private long downloadSpeed;
	private long openConnections;
	private long maxUploadPositions;

	private String serverName;
	private String externeIP;
	private Server server;
	
	public InformationDO() {}
	
	public InformationDO(int id, long sessionUpload, long sessionDownload,
			long credits, long uploadSpeed, long downloadSpeed,
			long openConnections, long maxUploadPositions) {
		this.id = id;
		this.sessionUpload = sessionUpload;
		this.sessionDownload = sessionDownload;
		this.credits = credits;
		this.uploadSpeed = uploadSpeed;
		this.downloadSpeed = downloadSpeed;
		this.openConnections = openConnections;
		this.maxUploadPositions = maxUploadPositions;
	}

	public int getId() {
		return id;
	}

	public long getSessionUpload() {
		return sessionUpload;
	}

	public long getSessionDownload() {
		return sessionDownload;
	}

	public long getCredits() {
		return credits;
	}

	public long getUploadSpeed() {
		return uploadSpeed;
	}

	public long getDownloadSpeed() {
		return downloadSpeed;
	}

	public long getOpenConnections() {
		return openConnections;
	}

	public void setExterneIP(String externeIP) {
		this.externeIP = externeIP;
	}

	public void setSessionUpload(long sessionUpload) {
		this.sessionUpload = sessionUpload;
	}

	public void setSessionDownload(long sessionDownload) {
		this.sessionDownload = sessionDownload;
	}

	public void setCredits(long credits) {
		this.credits = credits;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public Server getServer() {
		return server;
	}

	public void setVerbindungsStatus(int verbindungsStatus) {
		this.verbindungsStatus = verbindungsStatus;
		if (verbindungsStatus == NICHT_VERBUNDEN) {
			this.serverName = "";
		}
	}

	public void setUploadSpeed(long uploadSpeed) {
		this.uploadSpeed = uploadSpeed;
	}

	public void setDownloadSpeed(long downloadSpeed) {
		this.downloadSpeed = downloadSpeed;
	}

	public void setOpenConnections(long openConnections) {
		this.openConnections = openConnections;
	}

	public void setMaxUploadPositions(long maxUploadPositions) {
		this.maxUploadPositions = maxUploadPositions;
	}

	public String getServerName() {
		return serverName;
	}

	public int getVerbindungsStatus() {
		return verbindungsStatus;
	}

	public String getExterneIP() {
		return externeIP;
	}

	public long getMaxUploadPositions() {
		return maxUploadPositions;
	}
}
