package de.applejuicenet.client.shared.dac;

import java.util.HashMap;
import java.util.Iterator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import de.applejuicenet.client.gui.tables.download.DownloadColumnValue;
import de.applejuicenet.client.gui.tables.download.DownloadModel;
import de.applejuicenet.client.shared.MapSetStringKey;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/dac/Attic/DownloadDO.java,v 1.18 2004/01/26 11:01:14 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: DownloadDO.java,v $
 * Revision 1.18  2004/01/26 11:01:14  maj0r
 * Neuen Downloadstatus eingebaut.
 *
 * Revision 1.17  2004/01/12 19:31:52  maj0r
 * Bug #98 gefixt
 *
 * Revision 1.16  2004/01/12 13:17:49  maj0r
 * Bug #92 gefixt (Danke an daa803)
 * Ein paar Synchronized() eingebaut.
 *
 * Revision 1.15  2004/01/12 07:23:46  maj0r
 * Caching, Logging eingebaut.
 * Wiedergabe der Tabellenwerte vom Model ins Node umgebaut.
 *
 * Revision 1.14  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.13  2003/12/16 14:52:16  maj0r
 * An Schnittstellenerweiterung angepasst.
 *
 * Revision 1.12  2003/11/30 17:00:06  maj0r
 * Prozentangabe bei Downloads nun auf zwei Nachkommastellen genau (Danke an muhviestarr)
 *
 * Revision 1.11  2003/11/03 14:29:16  maj0r
 * Speicheroptimierung.
 *
 * Revision 1.10  2003/10/21 14:08:45  maj0r
 * Mittels PMD Code verschoenert, optimiert.
 *
 * Revision 1.9  2003/10/10 15:12:26  maj0r
 * Sortieren im Downloadbereich eingefuegt.
 *
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

public class DownloadDO
    implements DownloadColumnValue {

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

    private HashMap sourcen = new HashMap();

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
                logger.error("Unbehandelte Exception", e);
            }
            return "";
        }
    }

    public double getProzentGeladen() {
        return (double) ready * 100 / groesse;
    }

    public DownloadSourceDO getSourceById(int sourceId) {
        MapSetStringKey key = new MapSetStringKey(sourceId);
        if (sourcen.containsKey(key)) {
            return (DownloadSourceDO) sourcen.get(key);
        }
        else {
            return null;
        }
    }

    public void addSource(DownloadSourceDO downloadSourceDO) {
        MapSetStringKey key = new MapSetStringKey(downloadSourceDO.getId());
        if (!sourcen.containsKey(key)) {
            sourcen.put(key, downloadSourceDO);
        }
    }

    public DownloadSourceDO[] getSources() {
        DownloadSourceDO[] sources = null;
        synchronized(sourcen){
            sources = (DownloadSourceDO[]) sourcen.values().
                toArray(new DownloadSourceDO[sourcen.size()]);
        }
        return sources;
    }

    public void removeSource(String id) {
        MapSetStringKey key = new MapSetStringKey(id);
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

    public long getRestZeit() {
        long speed = getSpeedInBytes();
        if (speed == 0){
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
                logger.error("Unbehandelte Exception", e);
            }
            return "";
        }
    }

    public long getSpeedInBytes() {
        long speed = 0;
        try {
            synchronized (sourcen){
                Iterator it = sourcen.values().iterator();
                while (it.hasNext()) {
                    speed += ( (DownloadSourceDO) it.next()).getSpeed();
                }
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
        return speed;
    }

    public long getBereitsGeladen() {
        long geladen = ready;
        try {
            synchronized (sourcen){
                Iterator it = sourcen.values().iterator();
                while (it.hasNext()) {
                    geladen += ( (DownloadSourceDO) it.next()).
                        getBereitsGeladen();
                }
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
        return geladen;
    }

    public String getColumn0() {
        return getFilename();
    }

    public String getColumn1() {
        return getStatusAsString();
    }

    public String getColumn2() {
        return DownloadModel.parseGroesse(groesse);
    }

    public String getColumn3() {
        return DownloadModel.parseGroesse(getBereitsGeladen());
    }

    public String getColumn4() {
        if (status == DownloadDO.SUCHEN_LADEN) {
            return DownloadModel.getSpeedAsString(getSpeedInBytes());
        }
        else {
            return "";
        }
    }

    public String getColumn5() {
        if (status == DownloadDO.SUCHEN_LADEN) {
            return getRestZeitAsString();
        }
        else {
            return "";
        }
    }

    public String getColumn6() {
        return "";
    }

    public String getColumn7() {
        if (status == DownloadDO.SUCHEN_LADEN || status == DownloadDO.PAUSIERT) {
            return DownloadModel.parseGroesse(groesse - getBereitsGeladen());
        }
        else {
            return "";
        }
    }

    public String getColumn8() {
        if (status == DownloadDO.SUCHEN_LADEN ||
            status == DownloadDO.PAUSIERT) {
            return DownloadModel.powerdownload(getPowerDownload());
        }
        else {
            return "";
        }
    }

    public String getColumn9() {
        return "";
    }

    public String getStatusAsString() {
        try {
            switch (getStatus()) {
                case DownloadDO.PAUSIERT:
                    return DownloadModel.pausiert;
                case DownloadDO.ABBRECHEN:
                    return DownloadModel.abbrechen;
                case DownloadDO.ABGEGROCHEN:
                    return DownloadModel.abgebrochen;
                case DownloadDO.FERTIG:
                    return DownloadModel.fertig;
                case DownloadDO.FEHLER_BEIM_FERTIGSTELLEN:
                    return DownloadModel.fehlerBeimFertigstellen;
                case DownloadDO.NICHT_GENUG_PLATZ_FEHLER:
                    return DownloadModel.keinPlatz;
                case DownloadDO.DATA_WIRD_ERSTELLT:
                    return DownloadModel.dataWirdErstellt;
                case DownloadDO.SUCHEN_LADEN: {
                    DownloadSourceDO[] sources = getSources();
                    String result = "";
                    int uebertragung = 0;
                    int warteschlange = 0;
                    int status;
                    for (int i = 0; i < sources.length; i++) {
                        status = sources[i].getStatus();
                        if (status == DownloadSourceDO.UEBERTRAGUNG) {
                            uebertragung++;
                            result = DownloadModel.laden;
                        }
                        else if (status == DownloadSourceDO.IN_WARTESCHLANGE) {
                            warteschlange++;
                        }
                    }
                    if (result.length() == 0) {
                        result = DownloadModel.suchen;
                    }
                    return result + " " + (warteschlange + uebertragung) + "/" +
                        sources.length + " (" + uebertragung + ")";
                }
                case DownloadDO.FERTIGSTELLEN:
                    return DownloadModel.fertigstellen;
                default:
                    return "";
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
            return "";
        }
    }
}