package de.applejuicenet.client.fassade.entity;

import java.text.DecimalFormat;


public abstract class DownloadSource implements IdOwner{
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
	
	private static DecimalFormat formatter = new DecimalFormat("###,##0.00");

	public abstract int getStatus();

	public abstract int getSize();

    public abstract int getHerkunft();

	public abstract int getDirectstate();

	public abstract int getDownloadFrom();

	public abstract int getDownloadTo();

	public abstract int getActualDownloadPosition();

	public abstract int getSpeed();

	public abstract Version getVersion();

	public abstract int getQueuePosition();

	public abstract int getPowerDownload();

	public abstract String getFilename();

	public abstract String getNickname();

	public abstract int getId();

	public abstract int getDownloadId();

	public final double getReadyPercent() {
		if (getActualDownloadPosition() == -1 || getDownloadFrom() == -1) {
			return 0;
		}
		return (getActualDownloadPosition() - getDownloadFrom()) * 100 / getSize();
	}

	public final String getDownloadPercentAsString() {
		try {
			if (getActualDownloadPosition() == -1 || getDownloadFrom() == -1) {
				return "0";
			}
			double temp = getActualDownloadPosition() - getDownloadFrom();
			temp = temp * 100 / getSize();
			return formatter.format(temp);
		} catch (Exception e) {
			return "";
		}

	}

	public final String getRestZeitAsString() {
		try {
			int speed = getSpeed();
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

	public final int getBereitsGeladen() {
		if (getActualDownloadPosition() == -1 || getDownloadFrom() == -1) {
			return 0;
		}
		return getActualDownloadPosition() - getDownloadFrom();
	}

	public final int getNochZuLaden() {
		if (getDownloadTo() == -1 || getActualDownloadPosition() == -1) {
			return 0;
		}
		return getDownloadTo() - getActualDownloadPosition();
	}
}
