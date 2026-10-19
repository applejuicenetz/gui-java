package de.applejuicenet.client.gui.wizard;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/wizard/ConnectionKind.java,v 1.6 2004/10/15 13:34:48 maj0r Exp $
 *
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: Official GUI for the appleJuice core developed by muhviehstarr</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 *
 */

public class ConnectionKind {
    private String caption;
    private int maxUpload;
    private int maxDownload;
    private int maxNewConnectionsPer10Sec;

    public ConnectionKind(String caption, int maxUpload, int maxDownload,
                          int maxNewConnectionsPer10Sec) {
        this.caption = caption;
        this.maxUpload = maxUpload;
        this.maxDownload = maxDownload;
        this.maxNewConnectionsPer10Sec = maxNewConnectionsPer10Sec;
    }

    public int getMaxUpload() {
        return maxUpload;
    }

    public int getMaxDownload() {
        return maxDownload;
    }

    public String getLabel() {
        return caption;
    }

    public String toString() {
        return caption;
    }

    public int getMaxNewConnectionsPer10Sec() {
        return maxNewConnectionsPer10Sec;
    }
}
