package de.applejuicenet.client.gui.wizard;

/**
 * Copyright: General Public License
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
