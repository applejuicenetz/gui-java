package de.applejuicenet.client.gui.plugins.panels;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/versionchecker/src/de/applejuicenet/client/gui/plugins/panels/Attic/VersionHolder.java,v 1.2 2004/01/27 15:49:17 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: VersionHolder.java,v $
 * Revision 1.2  2004/01/27 15:49:17  maj0r
 * CVS-Header eingefuegt.
 *
 *
 */

public class VersionHolder {

    public String versionsNr;
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
        switch (os) {
            case 1: {
                countWin++;
                break;
            }
            case 2: {
                countLinux++;
                break;
            }
            case 3: {
                countMac++;
                break;
            }
            case 4: {
                countSolaris++;
                break;
            }
            case 5: {
                countOS2++;
                break;
            }
            case 6: {
                countFreeBSD++;
                break;
            }
            case 7: {
                countNetWare++;
                break;
            }
            default: {
                countSonstige++;
                break;
            }
        }
    }
}