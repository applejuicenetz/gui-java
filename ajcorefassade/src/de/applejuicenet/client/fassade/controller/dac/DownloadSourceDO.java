package de.applejuicenet.client.fassade.controller.dac;

import de.applejuicenet.client.fassade.shared.Version;

/**
 * $Header:
 * /cvsroot/applejuicejava/ajcorefassade/src/de/applejuicenet/client/fassade/controller/dac/DownloadSourceDO.java,v
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
 * @author: Maj0r [aj@tkl-soft.de]
 * 
 */

public class DownloadSourceDO {

	// Status - IDs
	public static final int UNGEFRAGT = 1;
	public static final int VERSUCHE_ZU_VERBINDEN = 2;
	public static final int GEGENSTELLE_HAT_ZU_ALTE_VERSION = 3;
	public static final int GEGENSTELLE_KANN_DATEI_NICHT_OEFFNEN = 4;
	public static final int IN_WARTESCHLANGE = 5;
	public static final int KEINE_BRAUCHBAREN_PARTS = 6;
	public static final int UEBERTRAGUNG = 7;
	public static final int NICHT_GENUEGEND_PLATZ_AUF_DER_PLATTE = 8;
	public static final int FERTIGGESTELLT = 9;
	public static final int KEINE_VERBINDUNG_MOEGLICH = 11;
	public static final int VERSUCHE_INDIREKT = 12;
	public static final int PAUSIERT = 13;
	public static final int WARTESCHLANGE_VOLL = 14;
	public static final int EIGENES_LIMIT_ERREICHT = 15;
	public static final int INDIREKTE_VERBINDUNG_ABGELEHNT = 16;

	// directstate - IDs
	public static final int UNBEKANNT = 0;
	public static final int DIREKTE_VERBINDUNG = 1;
	public static final int INDIREKTE_VERBINDUNG_UNBESTAETIGT = 2;
	public static final int INDIREKTE_VERBINDUNG = 3;

    // herkunft
    public static final int SOURCE_AJFSP = 1;
    public static final int SOURCE_CLIENT2CLIENT = 2;
    public static final int SOURCE_UPLOADLIST = 3;
    public static final int SOURCE_LAST_START = 4;
    public static final int SOURCE_TEXTSEARCH = 5;
    public static final int SOURCE_SERVER_SOURCE_SEARCH = 6;

    private final int id;

	private int status;
	private int directstate;
	private int downloadFrom;
	private int downloadTo;
	private int actualDownloadPosition;
	private int speed;
	private Version version = null;
	private int queuePosition;
	private int powerDownload;
	private String filename;
	private String nickname;
	private int downloadId;
	private int oldSize;
	private String sizeAsString;
	private int oldBereitsGeladen;
	private String bereitsGeladenAsString;
	private int oldNochZuLaden;
	private String nochZuLadenAsString;
	private boolean progressChanged = false;
	private boolean versionChanged = false;
    private int herkunft;

	public DownloadSourceDO(int id) {
		this.id = id;
	}

	public DownloadSourceDO(int id, int status, int directstate,
			int downloadFrom, int downloadTo, int actualDownloadPosition,
			int speed, Version version, int queuePosition, int powerDownload,
			String filename, String nickname, int downloadId, int herkunft) {
		this.id = id;
		this.status = status;
		this.directstate = directstate;
		this.downloadFrom = downloadFrom;
		this.downloadTo = downloadTo;
		this.actualDownloadPosition = actualDownloadPosition;
		this.speed = speed;
		this.version = version;
		this.queuePosition = queuePosition;
		this.powerDownload = powerDownload;
		this.filename = filename;
		this.nickname = nickname;
		this.downloadId = downloadId;
		progressChanged = true;
		versionChanged = true;
		this.herkunft = herkunft;
	}

	public int getStatus() {
		return status;
	}

	public int getSize() {
		if (downloadTo == -1 || downloadFrom == -1) {
			return 0;
		}
		return downloadTo - downloadFrom;
	}

	public void setStatus(int status) {
		this.status = status;
	}

    public int getHerkunft() {
        return herkunft;
    }

    public void setHerkunft(int herkunft) {
        this.herkunft = herkunft;
    }
	
	public double getReadyPercent() {
		if (actualDownloadPosition == -1 || downloadFrom == -1) {
			return 0;
		}
		return (actualDownloadPosition - downloadFrom) * 100 / getSize();
	}

	public String getDownloadPercentAsString() {
		try {
			if (actualDownloadPosition == -1 || downloadFrom == -1) {
				return "0";
			}
			double temp = actualDownloadPosition - downloadFrom;
			temp = temp * 100 / getSize();
			String result = Double.toString(temp);
			if (result.indexOf(".") + 3 < result.length()) {
				result = result.substring(0, result.indexOf(".") + 3);
			}
			return result;
		} catch (Exception e) {
			return "";
		}

	}

	public String getRestZeitAsString() {
		try {
			if (speed == 0 || speed == -1) {
				return "";
			}
			int restZeit = getNochZuLaden() / speed;
			int tage = restZeit / 86400;
			restZeit -= tage * 86400;
			int stunden = restZeit / 3600;
			restZeit -= stunden * 3600;
			int minuten = restZeit / 60;
			restZeit -= minuten * 60;

			StringBuffer temp = new StringBuffer();
			if (tage < 10) {
				temp.append('0');
			}
			temp.append(Integer.toString(tage));
			temp.append(':');
			if (stunden < 10) {
				temp.append('0');
			}
			temp.append(Integer.toString(stunden));
			temp.append(':');
			if (minuten < 10) {
				temp.append('0');
			}
			temp.append(Integer.toString(minuten));
			temp.append(':');
			if (restZeit < 10) {
				temp.append('0');
			}
			temp.append(Integer.toString(restZeit));
			return temp.toString();
		} catch (Exception e) {
			return "";
		}
	}

	public int getBereitsGeladen() {
		if (actualDownloadPosition == -1 || downloadFrom == -1) {
			return 0;
		}
		return actualDownloadPosition - downloadFrom;
	}

	public int getNochZuLaden() {
		if (downloadTo == -1 || actualDownloadPosition == -1) {
			return 0;
		}
		return downloadTo - actualDownloadPosition;
	}

	public int getDirectstate() {
		return directstate;
	}

	public void setDirectstate(int directstate) {
		this.directstate = directstate;
	}

	public int getDownloadFrom() {
		return downloadFrom;
	}

	public void setDownloadFrom(int downloadFrom) {
		this.downloadFrom = downloadFrom;
		progressChanged = true;
	}

	public int getDownloadTo() {
		return downloadTo;
	}

	public void setDownloadTo(int downloadTo) {
		this.downloadTo = downloadTo;
		progressChanged = true;
	}

	public int getActualDownloadPosition() {
		return actualDownloadPosition;
	}

	public void setActualDownloadPosition(int actualDownloadPosition) {
		this.actualDownloadPosition = actualDownloadPosition;
		progressChanged = true;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public Version getVersion() {
		return version;
	}

	public void setVersion(Version version) {
		this.version = version;
		versionChanged = true;
	}

	public int getQueuePosition() {
		return queuePosition;
	}

	public void setQueuePosition(int queuePosition) {
		this.queuePosition = queuePosition;
	}

	public int getPowerDownload() {
		return powerDownload;
	}

	public void setPowerDownload(int powerDownload) {
		this.powerDownload = powerDownload;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public int getId() {
		return id;
	}

	public int getDownloadId() {
		return downloadId;
	}

	public void setDownloadId(int downloadId) {
		this.downloadId = downloadId;
	}
}
