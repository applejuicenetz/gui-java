package de.applejuicenet.client.fassade.entity;

public abstract class Information {

	public static final int VERBUNDEN = 0;
	public static final int NICHT_VERBUNDEN = 1;
	public static final int VERSUCHE_ZU_VERBINDEN = 2;
	
	public abstract long getSessionUpload();

	public abstract long getSessionDownload();

	public abstract long getCredits();

	public abstract long getUploadSpeed();

	public abstract long getDownloadSpeed();

	public abstract long getOpenConnections();
	
	public abstract String getServerName();
	
	public abstract int getVerbindungsStatus();
	
	public abstract String getExterneIP();
	
	public abstract Server getServer();
	
	public abstract long getMaxUploadPositions();
	
	public final String getCreditsAsString() {
		return " Credits: " + bytesUmrechnen(getCredits());
	}

	public final String getUpDownSessionAsString() {
		return " in: " + bytesUmrechnen(getSessionDownload()) + " out: "
				+ bytesUmrechnen(getSessionUpload());
	}

	public final String getUpDownAsString() {
		return " in: " + getBytesSpeed(getDownloadSpeed()) + " out: "
				+ getBytesSpeed(getUploadSpeed());
	}

	public final String getUpAsString() {
		return getBytesSpeed(getUploadSpeed());
	}

	public final String getDownAsString() {
		return getBytesSpeed(getDownloadSpeed());
	}

	private final String getBytesSpeed(long bytes) {
		if (bytes == 0) {
			return "0 KB/s";
		}
		String result = bytesUmrechnen(bytes) + "/s";
		return result;
	}

	public static final String bytesUmrechnen(long bytes) {
		boolean minus = false;
		if (bytes < 0) {
			minus = true;
			bytes *= -1;
		}
		if (bytes == 0) {
			return "0 MB";
		}
		long faktor = 1;
		if (bytes < 1024l) {
			faktor = 1;
		} else if (bytes / 1024l < 1024l) {
			faktor = 1024l;
		} else if (bytes / 1048576l < 1024l) {
			faktor = 1048576l;
		} else if (bytes / 1073741824l < 1024l) {
			faktor = 1073741824l;
		} else {
			faktor = 1099511627776l;
		}
		if (minus) {
			bytes *= -1;
		}
		double umgerechnet = (double) bytes / (double) faktor;
		String result = Double.toString(umgerechnet);
		int pos = result.indexOf(".");
		if (pos != -1) {
			if (pos + 2 < result.length()) {
				result = result.substring(0, pos + 3);
			}
			result = result.replace('.', ',');
		}
		if (faktor == 1) {
			result += " Bytes";
		} else if (faktor == 1024l) {
			result += " kb";
		} else if (faktor == 1048576l) {
			result += " MB";
		} else if (faktor == 1073741824l) {
			result += " GB";
		} else {
			result += " TB";
		}
		return result;
	}
	
}
