package de.applejuicenet.client.fassade.controller.dac;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.fassade.ApplejuiceFassade;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/ajcorefassade/src/de/applejuicenet/client/fassade/controller/dac/Attic/DownloadDO.java,v 1.1 2004/12/03 07:57:12 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */

public class DownloadDO {

    private static Logger logger;

    static {
        logger = Logger.getLogger(DownloadDO.class);
    }

    //Status - IDs
    public static final int SUCHEN_LADEN = 0;
    public static final int NICHT_GENUG_PLATZ_FEHLER = 1;
    public static final int FERTIGSTELLEN = 12;
    public static final int FEHLER_BEIM_FERTIGSTELLEN = 13;
    public static final int FERTIG = 14;
    public static final int ABBRECHEN = 15;
    public static final int DATA_WIRD_ERSTELLT = 16;
    public static final int ABGEGROCHEN = 17;
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

    private long oldSpeed;
    private String speedAsString;

    private Map sourcen = new HashMap();
    private Set propertyChangeListener = new HashSet();

    public DownloadDO(int id){
        this.id = id;
    }

    public DownloadDO(int id, int shareId, String hash, long groesse,
                      long ready,
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

    public String getProzentGeladenAsString() {
        try {
            double temp = getProzentGeladen();
            String result = Double.toString(temp);
            if (result.indexOf(".") + 3 < result.length()) {
                result = result.substring(0, result.indexOf(".") + 3);
            }
            return result;
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
            return "";
        }
    }

    public double getProzentGeladen() {
        return (double) ready * 100 / groesse;
    }

    public DownloadSourceDO getSourceById(int sourceId) {
        String key = Integer.toString(sourceId);
        if (sourcen.containsKey(key)) {
            return (DownloadSourceDO) sourcen.get(key);
        }
        else {
            return null;
        }
    }

    public void addSource(DownloadSourceDO downloadSourceDO) {
        String key = Integer.toString(downloadSourceDO.getId());
        if (!sourcen.containsKey(key)) {
            sourcen.put(key, downloadSourceDO);
        }
    }

    public DownloadSourceDO[] getSources() {
        DownloadSourceDO[] sources = null;
        synchronized (sourcen) {
            sources = (DownloadSourceDO[]) sourcen.values().
                toArray(new DownloadSourceDO[sourcen.size()]);
        }
        return sources;
    }

    public void removeSource(String id) {
        String key = id;
        if (sourcen.containsKey(key)) {
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

    public void setStatus(int newStatus) {
        if (status != newStatus){
            status = newStatus;
        }
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String newFilename) {
        if (filename == null || !filename.equals(newFilename)){
            filename = newFilename;
        }
    }

    public String getTargetDirectory() {
        return targetDirectory;
    }

    public void setTargetDirectory(String newTargetDirectory) {
        if (targetDirectory == null || !targetDirectory.equals(newTargetDirectory)){
            targetDirectory = newTargetDirectory;
        }
    }

    public int getPowerDownload() {
        return powerDownload;
    }

    public void setPowerDownload(int newPowerDownload) {
        if ( powerDownload != newPowerDownload ){
            powerDownload = newPowerDownload;
        }
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

    public void setReady(long newReady) {
        if ( ready != newReady ){
            ready = newReady;
        }
    }

    public long getRestZeit() {
        long speed = getSpeedInBytes();
        if (speed == 0) {
            return Long.MAX_VALUE;
        }
        return ( (groesse - ready) / speed);
    }

    public String getRestZeitAsString() {
        try {
            long speed = getSpeedInBytes();
            if (speed == 0) {
                return "";
            }
            if (speed == oldSpeed) {
                return speedAsString;
            }
            oldSpeed = speed;
            int restZeit = (int) ( (groesse - ready) / speed);
            int tage = restZeit / 86400;
            restZeit -= tage * 86400;
            int stunden = restZeit / 3600;
            restZeit -= stunden * 3600;
            int minuten = restZeit / 60;
            restZeit -= minuten * 60;

            StringBuffer temp = new StringBuffer();
            if (tage < 10) {
                temp.append('0');
            }
            temp.append(Integer.toString(tage));
            temp.append(':');
            if (stunden < 10) {
                temp.append('0');
            }
            temp.append(Integer.toString(stunden));
            temp.append(':');
            if (minuten < 10) {
                temp.append('0');
            }
            temp.append(Integer.toString(minuten));
            temp.append(':');
            if (restZeit < 10) {
                temp.append('0');
            }
            temp.append(Integer.toString(restZeit));
            speedAsString = temp.toString();
            return speedAsString;
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
            return "";
        }
    }

    public long getSpeedInBytes() {
        long speed = 0;
        try {
            synchronized (sourcen) {
                Iterator it = sourcen.values().iterator();
                while (it.hasNext()) {
                    speed += ( (DownloadSourceDO) it.next()).getSpeed();
                }
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
        }
        return speed;
    }

    public long getBereitsGeladen() {
        long geladen = ready;
        try {
            synchronized (sourcen) {
                Iterator it = sourcen.values().iterator();
                while (it.hasNext()) {
                    geladen += ( (DownloadSourceDO) it.next()).
                        getBereitsGeladen();
                }
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
        }
        return geladen;
    }
}