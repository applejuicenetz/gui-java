package de.applejuicenet.client.fassade.shared;

import java.util.Set;

/**
 * $Header:
 * /cvsroot/applejuicejava/ajcorefassade/src/de/applejuicenet/client/fassade/shared/AJSettings.java,v
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

public class AJSettings {
	private String nick;

	private long port;

	private long xmlPort;

	private long maxUpload;

	private long maxDownload;

	private long maxConnections;

	private boolean autoConnect;

	private int speedPerSlot;

	private String incomingDir;

	private String tempDir;

	private Set shareDirs;

	private long maxNewConnectionsPerTurn;

	private long maxSourcesPerFile;

	public AJSettings(String nick, long port, long xmlPort, long maxUpload,
			long maxDownload, int speedPerSlot, String incomingDir,
			String tempDir, Set shareDirs, long maxConnections,
			boolean autoConnect, long maxNewConnectionsPerTurn,
			long maxSourcesPerFile) {
		this.nick = nick;
		this.port = port;
		this.xmlPort = xmlPort;
		this.maxUpload = maxUpload;
		this.maxDownload = maxDownload;
		this.speedPerSlot = speedPerSlot;
		this.incomingDir = incomingDir;
		this.tempDir = tempDir;
		this.shareDirs = shareDirs;
		this.maxConnections = maxConnections;
		this.autoConnect = autoConnect;
		this.maxNewConnectionsPerTurn = maxNewConnectionsPerTurn;
		this.maxSourcesPerFile = maxSourcesPerFile;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public long getMaxSourcesPerFile() {
		return maxSourcesPerFile;
	}

	public void setMaxSourcesPerFile(long maxSourcesPerFile) {
		this.maxSourcesPerFile = maxSourcesPerFile;
	}

	public long getPort() {
		return port;
	}

	public void setPort(long port) {
		this.port = port;
	}

	public long getXMLPort() {
		return xmlPort;
	}

	public void setXMLPort(long xmlPort) {
		this.xmlPort = xmlPort;
	}

	public long getMaxUpload() {
		return maxUpload;
	}

	public long getMaxUploadInKB() {
		return maxUpload / 1024;
	}

	public void setMaxUpload(long maxUpload) {
		this.maxUpload = maxUpload;
	}

	public long getMaxDownload() {
		return maxDownload;
	}

	public long getMaxDownloadInKB() {
		return maxDownload / 1024;
	}

	public void setMaxDownload(long maxDownload) {
		this.maxDownload = maxDownload;
	}

	public int getSpeedPerSlot() {
		return speedPerSlot;
	}

	public void setSpeedPerSlot(int speedPerSlot) {
		this.speedPerSlot = speedPerSlot;
	}

	public String getIncomingDir() {
		return incomingDir;
	}

	public void setIncomingDir(String incomingDir) {
		this.incomingDir = incomingDir;
	}

	public String getTempDir() {
		return tempDir;
	}

	public void setTempDir(String tempDir) {
		this.tempDir = tempDir;
	}

	public Set getShareDirs() {
		return shareDirs;
	}

	public void setShareDirs(Set shareDirs) {
		this.shareDirs = shareDirs;
	}

	public long getMaxConnections() {
		return maxConnections;
	}

	public void setMaxConnections(long maxConnections) {
		this.maxConnections = maxConnections;
	}

	public boolean isAutoConnect() {
		return autoConnect;
	}

	public void setAutoConnect(boolean autoConnect) {
		this.autoConnect = autoConnect;
	}

	public long getMaxNewConnectionsPerTurn() {
		return maxNewConnectionsPerTurn;
	}

	public void setMaxNewConnectionsPerTurn(long maxNewConnectionsPerTurn)
			throws IllegalArgumentException {
		if (maxNewConnectionsPerTurn < 1 || maxNewConnectionsPerTurn > 200) {
			throw new IllegalArgumentException();
		}
		this.maxNewConnectionsPerTurn = maxNewConnectionsPerTurn;
	}
}
