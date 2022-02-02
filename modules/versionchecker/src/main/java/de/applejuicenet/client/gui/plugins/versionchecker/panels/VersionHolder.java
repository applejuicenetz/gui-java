package de.applejuicenet.client.gui.plugins.versionchecker.panels;

import de.applejuicenet.client.fassade.entity.Version;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/versionchecker/src/de/applejuicenet/client/gui/plugins/versionchecker/panels/VersionHolder.java,v 1.1 2006/05/04 14:15:17 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 *
 */

public class VersionHolder {

    public String versionsNr;

    public static int countAll = 0;

    public int countWin = 0;
    public int countLinux = 0;
    public int countMac = 0;
    public int countSolaris = 0;
    public int countOS2 = 0;
    public int countFreeBSD = 0;
    public int countNetWare = 0;
    public int countSonstige = 0;

    public VersionHolder(String versionsNr) {
        this.versionsNr = versionsNr;
    }

    public void addUser(int os) {
    	countAll++;
        switch (os) {
            case Version.WIN32: {
                countWin++;
                break;
            }
            case Version.LINUX: {
                countLinux++;
                break;
            }
            case Version.MACINTOSH: {
                countMac++;
                break;
            }
            case Version.SOLARIS: {
                countSolaris++;
                break;
            }
            case Version.OS2: {
                countOS2++;
                break;
            }
            case Version.FREEBSD: {
                countFreeBSD++;
                break;
            }
            case Version.NETWARE: {
                countNetWare++;
                break;
            }
            default: {
                countSonstige++;
                break;
            }
        }
    }
    
    public int getUser(int os){
        switch (os) {
	        case Version.WIN32: {
	            return countWin;
	        }
	        case Version.LINUX: {
	        	return countLinux;
	        }
	        case Version.MACINTOSH: {
	        	return countMac;
	        }
	        case Version.SOLARIS: {
	        	return countSolaris;
	        }
	        case Version.OS2: {
	        	return countOS2;
	        }
	        case Version.FREEBSD: {
	        	return countFreeBSD;
	        }
	        case Version.NETWARE: {
	        	return countNetWare;
	        }
	        default: {
	            return countSonstige;
	        }
	    }
    }
}
