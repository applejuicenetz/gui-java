package de.applejuicenet.client.shared.dac;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/dac/Attic/InformationDO.java,v 1.2 2004/02/05 23:11:28 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: InformationDO.java,v $
 * Revision 1.2  2004/02/05 23:11:28  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.1  2004/01/01 14:27:25  maj0r
 * Es koennen nun auch Objekte nach Id vom Core abgefragt werden.
 *
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
