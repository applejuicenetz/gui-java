package de.applejuicenet.client.fassade.entity;

import java.util.StringTokenizer;

import de.applejuicenet.client.fassade.ApplejuiceFassade;

public abstract class Version {
	public static final int UNKNOWN = 0;
	public static final int WIN32 = 1;
	public static final int LINUX = 2;
	public static final int MACINTOSH = 3;
	public static final int SOLARIS = 4;
	public static final int OS2 = 5;
	public static final int FREEBSD = 6;
	public static final int NETWARE = 7;

	public abstract String getVersion();

	public abstract int getBetriebsSystem();

	public final String getBetriebsSystemAsString() {
		String result = "";
		if (getBetriebsSystem() == LINUX) {
			result = "Linux";
		} else if (getBetriebsSystem() == WIN32) {
			result = "Win32";
		}
		return result;
	}

	public final static int getOSTypByOSName(String OSName) {
		int result = -1;
		if (OSName.compareToIgnoreCase("Windows NT") == 0) {
			result = WIN32;
		} else if (OSName.compareToIgnoreCase("Linux") == 0) {
			result = LINUX;
		}
		return result;
	}

	public final int compareTo(String versionNr) {
		if (getVersion().compareToIgnoreCase(versionNr) == 0) {
			return 0;
		}
		StringTokenizer token1 = new StringTokenizer(getVersion(), ".");
		StringTokenizer token2 = new StringTokenizer(versionNr, ".");
		if (token1.countTokens() != 4 || token2.countTokens() != 4) {
			// alles Mist
			return 0;
		}
		String[] foundCore = new String[4];
		String[] neededCore = new String[4];
		for (int i = 0; i < 4; i++) {
			foundCore[i] = token1.nextToken();
			neededCore[i] = token2.nextToken();
		}
		boolean coreTooOld = false;
		for (int i = 0; i < 4; i++) {
			if (Integer.parseInt(foundCore[i]) < Integer
					.parseInt(neededCore[i])) {
				coreTooOld = true;
				return -1;
			}
		}
		return 1;
	}
	
	public final int checkForValidCoreVersion(){
		return compareTo(ApplejuiceFassade.MIN_NEEDED_CORE_VERSION);		
	}
}
