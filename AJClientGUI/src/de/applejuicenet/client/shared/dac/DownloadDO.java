package de.applejuicenet.client.shared.dac;

import de.applejuicenet.client.shared.MapSetStringKey;
import de.applejuicenet.client.gui.tables.download.DownloadNode;

import java.util.HashMap;
import java.util.Iterator;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/dac/Attic/DownloadDO.java,v 1.4 2003/08/09 10:57:54 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI f?r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DownloadDO.java,v $
 * Revision 1.4  2003/08/09 10:57:54  maj0r
 * Upload- und DownloadTabelle weitergeführt.
 *
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
    private Long groesse;
    private Long ready;
    private int status;
    private String filename;
    private String targetDirectory;
    private int powerDownload;
    private int temporaryFileNumber;

    private HashMap sourcen = new HashMap();

    public DownloadDO(String id, String shareId, String hash, Long groesse, Long ready,
                      int status, String filename, String targetDirectory,
                      int powerDownload, int temporaryFileNumber) {
        this.id = id;
        this.shareId = shareId;
        this.hash = hash;
        this.groesse = groesse;
        this.ready = ready;
        this.status = status;
        this.filename = filename;
        this.targetDirectory = targetDirectory;
        this.powerDownload = powerDownload;
        this.temporaryFileNumber = temporaryFileNumber;
    }

    public void alterDownload(String shareId, String hash, Long groesse, Long ready,
                      int status, String filename, String targetDirectory,
                      int powerDownload, int temporaryFileNumber){
        this.shareId = shareId;
        this.hash = hash;
        this.groesse = groesse;
        this.ready = ready;
        this.status = status;
        this.filename = filename;
        this.targetDirectory = targetDirectory;
        this.powerDownload = powerDownload;
        this.temporaryFileNumber = temporaryFileNumber;
    }

    public String getProzentGeladenAsString(){
        double temp = ready.longValue() * 100 / groesse.longValue();
        String result = Double.toString(temp);
        if (result.indexOf(".") + 3 < result.length())
        {
            result = result.substring(0, result.indexOf(".") + 3);
        }
        return result;
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

    public Long getGroesse() {
        return groesse;
    }

    public void setGroesse(Long groesse) {
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

    public Long getReady() {
        return ready;
    }

    public void setReady(Long ready) {
        this.ready = ready;
    }

    public String getRestZeitAsString(){
        long speed = getSpeedInBytes();
        if (speed == 0)
            return "";
        int restZeit =(int)((groesse.longValue()-ready.longValue()) / speed);
        int tage = restZeit / 86400;
        restZeit -= tage * 86400;
        int stunden = restZeit / 3600;
        restZeit -= stunden * 3600;
        int minuten = restZeit / 60;
        restZeit -= minuten * 60;

        StringBuffer temp = new StringBuffer();
        if (tage<10)
            temp.append('0');
        temp.append(Integer.toString(tage));
        temp.append(':');
        if (stunden<10)
            temp.append('0');
        temp.append(Integer.toString(stunden));
        temp.append(':');
        if (minuten<10)
            temp.append('0');
        temp.append(Integer.toString(minuten));
        temp.append(':');
        if (restZeit<10)
            temp.append('0');
        temp.append(Integer.toString(restZeit));
        return temp.toString();
    }

    public long getSpeedInBytes(){
        long speed = 0;
        Iterator it = sourcen.values().iterator();
        while (it.hasNext()){
            speed += (long)((DownloadSourceDO)it.next()).getSpeed().intValue();
        }
        return speed;
    }
}
