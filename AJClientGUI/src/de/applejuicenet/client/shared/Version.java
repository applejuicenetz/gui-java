package de.applejuicenet.client.shared;

import java.util.StringTokenizer;

import javax.swing.ImageIcon;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/Attic/Version.java,v 1.14 2004/06/28 15:47:42 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [Maj0r@applejuicenet.de]
 *
 */

public class Version {
    public static final int UNBEKANNT = 0;
    public static final int WIN32 = 1;
    public static final int LINUX = 2;
    public static final int MACINTOSH = 3;
    public static final int SOLARIS = 4;
    public static final int OS2 = 5;
    public static final int FREEBSD = 6;
    public static final int NETWARE = 7;

    private String versionNr;
    private int betriebsSystem;

    public Version(String versionNr, int betriebsSystem) {
        this.versionNr = versionNr;
        this.betriebsSystem = betriebsSystem;
    }

    public Version() {
    }

    public String getVersion() {
        return versionNr;
    }

    public int getBetriebsSystem() {
        return betriebsSystem;
    }

    public String getBetriebsSystemAsString() {
        String result = "";
        if (betriebsSystem == LINUX) {
            result = "Linux";
        }
        else if (betriebsSystem == WIN32) {
            result = "Win32";
        }
        return result;
    }

    public void setBetriebsSystem(int betriebsSystem) {
        this.betriebsSystem = betriebsSystem;
    }

    public void setVersion(String versionNr) {
        this.versionNr = versionNr;
    }

    public static int getOSTypByOSName(String OSName) {
        int result = -1;
        if (OSName.compareToIgnoreCase("Windows NT") == 0) {
            result = WIN32;
        }
        else if (OSName.compareToIgnoreCase("Linux") == 0) {
            result = LINUX;
        }
        return result;
    }

    public ImageIcon getVersionIcon() {
        switch (betriebsSystem) {
            case Version.WIN32: {
                return IconManager.getInstance().getIcon("winsymbol");
            }
            case Version.LINUX: {
                return IconManager.getInstance().getIcon("linuxsymbol");
            }
            case Version.FREEBSD: {
                return IconManager.getInstance().getIcon("freebsdsymbol");
            }
            case Version.MACINTOSH: {
                return IconManager.getInstance().getIcon("macsymbol");
            }
            case Version.SOLARIS: {
                return IconManager.getInstance().getIcon("sunossymbol");
            }
            case Version.NETWARE: {
                return IconManager.getInstance().getIcon("netwaresymbol");
            }
            case Version.OS2: {
                return IconManager.getInstance().getIcon("os2symbol");
            }
            default: {
                return IconManager.getInstance().getIcon("unbekanntsymbol");
            }
        }
    }
    
    public int compareTo(Version version){
    	if ( versionNr.compareToIgnoreCase(version.getVersion()) == 0 ){
    		return 0;
    	}
        StringTokenizer token1 = new StringTokenizer(versionNr, ".");
        StringTokenizer token2 = new StringTokenizer(version.getVersion(), ".");
        if (token1.countTokens() != 4 ||
            token2.countTokens() != 4) {
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
            if (Integer.parseInt(foundCore[i]) <
                Integer.parseInt(neededCore[i])) {
                coreTooOld = true;
                return -1;
            }
        }
    	return 1;
    }
}