package de.applejuicenet.client.gui.wizard;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/wizard/ConnectionKind.java,v 1.6 2004/10/15 13:34:48 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 *
 */

public class ConnectionKind {
    private String bezeichnung;
    private int maxUpload;
    private int maxDownload;
    private int maxNewConnectionsPro10Sek;

    public ConnectionKind(String bezeichnung, int maxUpload, int maxDownload,
                          int maxNewConnectionsPro10Sek) {
        this.bezeichnung = bezeichnung;
        this.maxUpload = maxUpload;
        this.maxDownload = maxDownload;
        this.maxNewConnectionsPro10Sek = maxNewConnectionsPro10Sek;
    }

    public int getMaxUpload() {
        return maxUpload;
    }

    public int getMaxDownload() {
        return maxDownload;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public String toString() {
        return bezeichnung;
    }

    public int getMaxNewConnectionsPro10Sek() {
        return maxNewConnectionsPro10Sek;
    }
}
