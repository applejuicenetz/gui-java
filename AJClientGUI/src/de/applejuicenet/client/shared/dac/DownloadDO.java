package de.applejuicenet.client.shared.dac;

import de.applejuicenet.client.shared.MapSetStringKey;
import de.applejuicenet.client.gui.tables.download.DownloadNode;

import java.util.HashMap;
import java.util.Iterator;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/dac/Attic/DownloadDO.java,v 1.8 2003/09/11 06:54:15 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI f?r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DownloadDO.java,v $
 * Revision 1.8  2003/09/11 06:54:15  maj0r
 * Auf neues Sessions-Prinzip umgebaut.
 * Sprachenwechsel korrigert, geht nun wieder flott.
 *
 * Revision 1.7  2003/09/10 15:30:48  maj0r
 * Begonnen auf neue Session-Struktur umzubauen.
 *
 * Revision 1.6  2003/09/01 15:50:51  maj0r
 * Wo es moeglich war, DOs auf primitive Datentypen umgebaut.
 *
 * Revision 1.5  2003/08/10 21:08:18  maj0r
 * Diverse Änderungen.
 *
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

    private final int id;
    private int shareId;

    private String hash;
    private long groesse;
    private long ready;
    private int status;
    private String filename;
    private String targetDirectory;
    private int powerDownload;
    private int temporaryFileNumber;

    private HashMap sourcen = new HashMap();

    public DownloadDO(int id, int shareId, String hash, long groesse, long ready,
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

    public String getProzentGeladenAsString(){
        double temp = ready * 100 / groesse;
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
            alteSource.setDownloadId(downloadSourceDO.getDownloadId());
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
        if (sourcen.containsKey(key)){
            sourcen.remove(key);
        }
    }

    public int getShareId() {
        return shareId;
    }

    public void setShareId(int shareId) {
        this.shareId = shareId;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public long getGroesse() {
        return groesse;
    }

    public void setGroesse(long groesse) {
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

    public int getId() {
        return id;
    }

    public int getTemporaryFileNumber() {
        return temporaryFileNumber;
    }

    public void setTemporaryFileNumber(int temporaryFileNumber) {
        this.temporaryFileNumber = temporaryFileNumber;
    }

    public long getReady() {
        return ready;
    }

    public void setReady(long ready) {
        this.ready = ready;
    }

    public String getRestZeitAsString(){
        long speed = getSpeedInBytes();
        if (speed == 0)
            return "";
        int restZeit =(int)((groesse-ready) / speed);
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
            speed += ((DownloadSourceDO)it.next()).getSpeed();
        }
        return speed;
    }

    public long getBereitsGeladen(){
        long geladen = ready;
        Iterator it = sourcen.values().iterator();
        while (it.hasNext()){
            geladen += ((DownloadSourceDO)it.next()).getBereitsGeladen();
        }
        return geladen;
    }
}
