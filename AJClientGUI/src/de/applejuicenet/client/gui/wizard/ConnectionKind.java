package de.applejuicenet.client.gui.wizard;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/wizard/ConnectionKind.java,v 1.1 2003/09/09 12:28:15 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: ConnectionKind.java,v $
 * Revision 1.1  2003/09/09 12:28:15  maj0r
 * Wizard fertiggestellt.
 *
 *
 */

public class ConnectionKind {
    private String bezeichnung;
    private int maxUpload;
    private int maxDownload;

    public ConnectionKind(String bezeichnung, int maxUpload, int maxDownload) {
        this.bezeichnung = bezeichnung;
        this.maxUpload = maxUpload;
        this.maxDownload = maxDownload;
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

    public String toString(){
        return bezeichnung;
    }
}
