package de.applejuicenet.client.shared.dac;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/dac/Attic/InformationDO.java,v 1.3 2004/10/11 18:18:52 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class InformationDO {

    private int id;
    private long sessionUpload;
    private long sessionDownload;
    private long credits;
    private long uploadSpeed;
    private long downloadSpeed;
    private long openConnections;

    public InformationDO(int id, long sessionUpload, long sessionDownload,
                         long credits,
                         long uploadSpeed, long downloadSpeed,
                         long openConnections) {
        this.id = id;
        this.sessionUpload = sessionUpload;
        this.sessionDownload = sessionDownload;
        this.credits = credits;
        this.uploadSpeed = uploadSpeed;
        this.downloadSpeed = downloadSpeed;
        this.openConnections = openConnections;
    }

    public int getId() {
        return id;
    }

    public long getSessionUpload() {
        return sessionUpload;
    }

    public long getSessionDownload() {
        return sessionDownload;
    }

    public long getCredits() {
        return credits;
    }

    public long getUploadSpeed() {
        return uploadSpeed;
    }

    public long getDownloadSpeed() {
        return downloadSpeed;
    }

    public long getOpenConnections() {
        return openConnections;
    }
}
