package de.applejuicenet.client.shared.dac;

import java.util.*;

import de.applejuicenet.client.shared.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/dac/Attic/DownloadSourceDO.java,v 1.8 2003/07/03 19:11:16 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI fï¿½r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DownloadSourceDO.java,v $
 * Revision 1.8  2003/07/03 19:11:16  maj0r
 * DownloadTable überarbeitet.
 *
 * Revision 1.7  2003/06/30 19:46:11  maj0r
 * Sourcestil verbessert.
 *
 * Revision 1.6  2003/06/10 12:31:03  maj0r
 * Historie eingefï¿½gt.
 *
 *
 */

public class DownloadSourceDO {
    //Status - IDs
    public static final int UNGEFRAGT = 1;
    public static final int VERSUCHE_ZU_VERBINDEN = 2;
    public static final int GEGENSTELLE_HAT_ZU_ALTE_VERSION = 3;
    public static final int GEGENSTELLE_KANN_DATEI_NICHT_OEFFNEN = 4;
    public static final int IN_WARTESCHLANGE = 5;
    public static final int KEINE_BRAUCHBAREN_PARTS = 6;
    public static final int UEBERTRAGUNG = 7;
    public static final int NICHT_GENUEGEND_PLATZ_AUF_DER_PLATTE = 8;
    public static final int FERTIGGESTELLT = 9;
    public static final int KEINE_VERBINDUNG_MOEGLICH = 11;
    public static final int PAUSIERT = 13;

    //directstate - IDs
    public static final int UNBEKANNT = 0;
    public static final int DIREKTE_VERBINDUNG = 1;
    public static final int INDIREKTE_VERBINDUNG = 2;


    private String id;
    private int status;
    private int directstate;
    private Integer downloadFrom;
    private Integer downloadTo;
    private Integer actualDownloadPosition;
    private Integer speed;
    private Version version;
    private int queuePosition;
    private int powerDownload;
    private String filename;
    private String nickname;

    public DownloadSourceDO(String id, int status, int directstate, Integer downloadFrom, Integer downloadTo,
                            Integer actualDownloadPosition, Integer speed, Version version, int queuePosition,
                            int powerDownload, String filename, String nickname) {
        this.id = id;
        this.status = status;
        this.directstate = directstate;
        this.downloadFrom = downloadFrom;
        this.downloadTo = downloadTo;
        this.actualDownloadPosition = actualDownloadPosition;
        this.speed = speed;
        this.version = version;
        this.queuePosition = queuePosition;
        this.powerDownload = powerDownload;
        this.filename = filename;
        this.nickname = nickname;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getDirectstate() {
        return directstate;
    }

    public void setDirectstate(int directstate) {
        this.directstate = directstate;
    }

    public Integer getDownloadFrom() {
        return downloadFrom;
    }

    public void setDownloadFrom(Integer downloadFrom) {
        this.downloadFrom = downloadFrom;
    }

    public Integer getDownloadTo() {
        return downloadTo;
    }

    public void setDownloadTo(Integer downloadTo) {
        this.downloadTo = downloadTo;
    }

    public Integer getActualDownloadPosition() {
        return actualDownloadPosition;
    }

    public void setActualDownloadPosition(Integer actualDownloadPosition) {
        this.actualDownloadPosition = actualDownloadPosition;
    }

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public int getQueuePosition() {
        return queuePosition;
    }

    public void setQueuePosition(int queuePosition) {
        this.queuePosition = queuePosition;
    }

    public int getPowerDownload() {
        return powerDownload;
    }

    public void setPowerDownload(int powerDownload) {
        this.powerDownload = powerDownload;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getId() {
        return id;
    }
}