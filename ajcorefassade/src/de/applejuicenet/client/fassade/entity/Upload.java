package de.applejuicenet.client.fassade.entity;

import de.applejuicenet.client.fassade.shared.Version;

public abstract class Upload {
	public static final int AKTIVE_UEBERTRAGUNG = 1;
	public static final int WARTESCHLANGE = 2;
	public static final int STATE_UNBEKANNT = 0;
	public static final int STATE_DIREKT_VERBUNDEN = 1;
	public static final int STATE_INDIREKT_VERBUNDEN = 2;

	public abstract int getUploadID();

	public abstract int getShareFileID();

	public abstract Version getVersion();

	public abstract int getStatus();

	public abstract int getDirectState();

	public abstract String getNick();

	public abstract long getUploadFrom();

	public abstract long getUploadTo();

	public abstract long getActualUploadPosition();

	public abstract int getSpeed();

	public abstract int getPrioritaet();

	public abstract String getDateiName();

	public abstract long getLastConnection();

	public abstract int getLoaded();

	public final String getUploadIDAsString() {
		return Integer.toString(getUploadID());
	}

	public final String getShareFileIDAsString() {
		return Integer.toString(getShareFileID());
	}

	public final String getStatusAsString() {
		return Integer.toString(getStatus());
	}
	
	public final String getDownloadPercentAsString() {
		if (getActualUploadPosition() == -1 || getUploadFrom() == -1) {
			return "0";
		}
		double temp = getActualUploadPosition() - getUploadFrom();
		if (temp == 0.0) {
			return "0";
		}
		temp = temp * 100 / getSize();
		String result = Double.toString(temp);
		if (result.indexOf(".") + 3 < result.length()) {
			result = result.substring(0, result.indexOf(".") + 3);
		}
		return result;
	}

	public final long getSize() {
		if (getUploadTo() == -1 || getUploadFrom() == -1) {
			return 0;
		}
		return getUploadTo() - getUploadFrom();
	}
	
}
