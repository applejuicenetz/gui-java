package de.applejuicenet.client.shared.dac;

import de.applejuicenet.client.shared.MapSetStringKey;

import java.util.HashMap;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/dac/Attic/DownloadDO.java,v 1.3 2003/08/05 20:47:06 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI f?r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DownloadDO.java,v $
 * Revision 1.3  2003/08/05 20:47:06  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.2  2003/07/06 20:00:19  maj0r
 * DownloadTable bearbeitet.
 *
 * Revision 1.1  2003/07/03 19:15:06  maj0r
 * DownloadTable überarbeitet.
 *
 *
 */

public class DownloadDO {

    //Status - IDs
    public static final int SUCHEN_LADEN = 0;
    public static final int NICHT_GENUG_PLATZ_FEHLER = 1;
    public static final int FERTIGSTELLEN = 12;
    public static final int FERTIG = 14;
    public static final int ABBRECHEN = 15;
    public static final int AGBEGROCHEN = 17;
    public static final int PAUSIERT = 18;

    private String id;
    private String shareId;

    private String hash;
    private String groesse;
    private int status;
    private String filename;
    private String targetDirectory;
    private int powerDownload;
    private int temporaryFileNumber;

    private HashMap sourcen = new HashMap();

    public DownloadDO(String id, String shareId, String hash, String groesse,
                      int status, String filename, String targetDirectory,
                      int powerDownload, int temporaryFileNumber) {
        this.id = id;
        this.shareId = shareId;
        this.hash = hash;
        this.groesse = groesse;
        this.status = status;
        this.filename = filename;
        this.targetDirectory = targetDirectory;
        this.powerDownload = powerDownload;
        this.temporaryFileNumber = temporaryFileNumber;
    }

    public void alterDownload(String shareId, String hash, String groesse,
                      int status, String filename, String targetDirectory,
                      int powerDownload, int temporaryFileNumber){
        this.shareId = shareId;
        this.hash = hash;
        this.groesse = groesse;
        this.status = status;
        this.filename = filename;
        this.targetDirectory = targetDirectory;
        this.powerDownload = powerDownload;
        this.temporaryFileNumber = temporaryFileNumber;
    }

    public String getProzentGeladen(){
        //todo
        return "0";
    }

    public void addOrAlterSource(DownloadSourceDO downloadSourceDO){
        MapSetStringKey key = new MapSetStringKey(downloadSourceDO.getId());
        if (sourcen.containsKey(key)){
            DownloadSourceDO alteSource = (DownloadSourceDO)sourcen.get(key);
            alteSource.setActualDownloadPosition(downloadSourceDO.getActualDownloadPosition());
            alteSource.setDirectstate(downloadSourceDO.getDirectstate());
            alteSource.setDownloadFrom(downloadSourceDO.getDownloadFrom());
            alteSource.setDownloadTo(downloadSourceDO.getDownloadTo());
            alteSource.setFilename(downloadSourceDO.getFilename());
            alteSource.setNickname(downloadSourceDO.getNickname());
            alteSource.setPowerDownload(downloadSourceDO.getPowerDownload());
            alteSource.setQueuePosition(downloadSourceDO.getQueuePosition());
            alteSource.setSpeed(downloadSourceDO.getSpeed());
            alteSource.setStatus(downloadSourceDO.getStatus());
            alteSource.setVersion(downloadSourceDO.getVersion());
        }
        else{
            sourcen.put(key, downloadSourceDO);
        }
    }

    public DownloadSourceDO[] getSources(){
        return (DownloadSourceDO[])sourcen.values().toArray(new DownloadSourceDO[sourcen.size()]);
    }

    public void removeSource(String id){
        MapSetStringKey key = new MapSetStringKey(id);
        sourcen.remove(key);
    }

    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getGroesse() {
        return groesse;
    }

    public void setGroesse(String groesse) {
        this.groesse = groesse;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getTargetDirectory() {
        return targetDirectory;
    }

    public void setTargetDirectory(String targetDirectory) {
        this.targetDirectory = targetDirectory;
    }

    public int getPowerDownload() {
        return powerDownload;
    }

    public void setPowerDownload(int powerDownload) {
        this.powerDownload = powerDownload;
    }

    public String getId() {
        return id;
    }

    public int getTemporaryFileNumber() {
        return temporaryFileNumber;
    }

    public void setTemporaryFileNumber(int temporaryFileNumber) {
        this.temporaryFileNumber = temporaryFileNumber;
    }
}
