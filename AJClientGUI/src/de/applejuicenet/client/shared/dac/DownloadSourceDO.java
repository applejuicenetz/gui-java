package de.applejuicenet.client.shared.dac;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import de.applejuicenet.client.gui.tables.download.DownloadColumnComponent;
import de.applejuicenet.client.gui.tables.download.DownloadColumnValue;
import de.applejuicenet.client.gui.tables.download.DownloadModel;
import de.applejuicenet.client.shared.Version;
import javax.swing.JTable;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/dac/Attic/DownloadSourceDO.java,v 1.24 2004/03/05 15:49:39 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fï¿½r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DownloadSourceDO.java,v $
 * Revision 1.24  2004/03/05 15:49:39  maj0r
 * PMD-Optimierung
 *
 * Revision 1.23  2004/02/25 16:20:01  maj0r
 * ProgressBar auf opaque=false gesetzt.
 * VersionLabel und ProgressBar wird nur bei tatsaechlicher Wertaenderung aktualisiert.
 *
 * Revision 1.22  2004/02/24 18:21:51  maj0r
 * Schrift korrigiert.
 *
 * Revision 1.21  2004/02/24 15:38:11  maj0r
 * CellRenderer optimiert indem die Komponenten in den DOs gehalten werden.
 *
 * Revision 1.20  2004/02/24 08:49:32  maj0r
 * Bug #240 gefixt (Danke an computer.ist.org)
 * Bug behoben, der im VersionChecker zu einer NoSuchElementException fuehrte.
 *
 * Revision 1.19  2004/02/18 17:24:21  maj0r
 * Von DOM auf SAX umgebaut.
 *
 * Revision 1.18  2004/02/12 16:35:41  maj0r
 * Getter fuer aktuelle runtergeladene Prozent hinzugefuegt.
 *
 * Revision 1.17  2004/01/12 07:28:10  maj0r
 * Caching, Logging eingebaut.
 * Wiedergabe der Tabellenwerte vom Model ins Node umgebaut.
 *
 * Revision 1.16  2003/12/30 13:55:20  maj0r
 * Neuen DownloadSourceStatus indirekteVerbindungAbgelehnt eingebaut.
 *
 * Revision 1.15  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.14  2003/12/16 14:52:16  maj0r
 * An Schnittstellenerweiterung angepasst.
 *
 * Revision 1.13  2003/10/18 18:44:16  maj0r
 * Neuen Userstatus "Warteschlange voll" hinzugefuegt.
 *
 * Revision 1.12  2003/09/10 15:30:48  maj0r
 * Begonnen auf neue Session-Struktur umzubauen.
 *
 * Revision 1.11  2003/09/01 15:50:51  maj0r
 * Wo es moeglich war, DOs auf primitive Datentypen umgebaut.
 *
 * Revision 1.10  2003/08/04 14:28:55  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.9  2003/07/06 20:00:19  maj0r
 * DownloadTable bearbeitet.
 *
 * Revision 1.8  2003/07/03 19:11:16  maj0r
 * DownloadTable überarbeitet.
 *
 * Revision 1.7  2003/06/30 19:46:11  maj0r
 * Sourcestil verbessert.
 *
 * Revision 1.6  2003/06/10 12:31:03  maj0r
 * Historie eingefuegt.
 *
 *
 */

public class DownloadSourceDO
    implements DownloadColumnValue, DownloadColumnComponent {

    private static Logger logger;

    static {
        logger = Logger.getLogger(DownloadDO.class);
    }

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
    public static final int VERSUCHE_INDIREKT = 12;
    public static final int PAUSIERT = 13;
    public static final int WARTESCHLANGE_VOLL = 14;
    public static final int EIGENES_LIMIT_ERREICHT = 15;
    public static final int INDIREKTE_VERBINDUNG_ABGELEHNT = 16;

    //directstate - IDs
    public static final int UNBEKANNT = 0;
    public static final int DIREKTE_VERBINDUNG = 1;
    public static final int INDIREKTE_VERBINDUNG_UNBESTAETIGT = 2;
    public static final int INDIREKTE_VERBINDUNG = 3;

    private final int id;
    private int status;
    private int directstate;
    private int downloadFrom;
    private int downloadTo;
    private int actualDownloadPosition;
    private int speed;
    private Version version = null;
    private int queuePosition;
    private int powerDownload;
    private String filename;
    private String nickname;
    private int downloadId;

    private int oldSize;
    private String sizeAsString;

    private int oldBereitsGeladen;
    private String bereitsGeladenAsString;

    private int oldNochZuLaden;
    private String nochZuLadenAsString;

    private JProgressBar progress;
    private JLabel progressbarLabel;
    private JLabel versionLabel;
    private boolean progressChanged = false;
    private boolean versionChanged = false;

    public DownloadSourceDO(int id){
        this.id = id;
        init();
    }

    public DownloadSourceDO(int id, int status, int directstate,
                            int downloadFrom, int downloadTo,
                            int actualDownloadPosition, int speed,
                            Version version, int queuePosition,
                            int powerDownload, String filename, String nickname,
                            int downloadId) {
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
        this.downloadId = downloadId;
        progressChanged = true;
        versionChanged = true;
        init();
    }

    private void init(){
        progress = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
        progress.setStringPainted(true);
        progress.setOpaque(false);
        progressbarLabel = new JLabel();
        progressbarLabel.setOpaque(true);
        versionLabel = new JLabel();
        versionLabel.setOpaque(true);
    }

    public int getStatus() {
        return status;
    }

    public int getSize() {
        if (downloadTo == -1 || downloadFrom == -1) {
            return 0;
        }
        return downloadTo - downloadFrom;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getReadyPercent(){
        if (actualDownloadPosition == -1 || downloadFrom == -1) {
            return 0;
        }
        return (actualDownloadPosition - downloadFrom) *100 / getSize();
    }

    public String getDownloadPercentAsString() {
        try {
            if (actualDownloadPosition == -1 || downloadFrom == -1) {
                return "0";
            }
            double temp = actualDownloadPosition - downloadFrom;
            temp = temp * 100 / getSize();
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

    public String getRestZeitAsString() {
        try {
            if (speed == 0 || speed == -1) {
                return "";
            }
            int restZeit = getNochZuLaden() / speed;
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
            return temp.toString();
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
            return "";
        }
    }

    public int getBereitsGeladen() {
        if (actualDownloadPosition == -1 || downloadFrom == -1) {
            return 0;
        }
        return actualDownloadPosition - downloadFrom;
    }

    public int getNochZuLaden() {
        if (downloadTo == -1 || actualDownloadPosition == -1) {
            return 0;
        }
        return downloadTo - actualDownloadPosition;
    }

    public int getDirectstate() {
        return directstate;
    }

    public void setDirectstate(int directstate) {
        this.directstate = directstate;
    }

    public int getDownloadFrom() {
        return downloadFrom;
    }

    public void setDownloadFrom(int downloadFrom) {
        this.downloadFrom = downloadFrom;
        progressChanged = true;
    }

    public int getDownloadTo() {
        return downloadTo;
    }

    public void setDownloadTo(int downloadTo) {
        this.downloadTo = downloadTo;
        progressChanged = true;
    }

    public int getActualDownloadPosition() {
        return actualDownloadPosition;
    }

    public void setActualDownloadPosition(int actualDownloadPosition) {
        this.actualDownloadPosition = actualDownloadPosition;
        progressChanged = true;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
        versionChanged = true;
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

    public int getId() {
        return id;
    }

    public int getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(int downloadId) {
        this.downloadId = downloadId;
    }

    public String getColumn0() {
        return getFilename();
    }

    public String getColumn1() {
        switch (getStatus()) {
            case DownloadSourceDO.UNGEFRAGT:
                return DownloadModel.ungefragt;
            case DownloadSourceDO.VERSUCHE_ZU_VERBINDEN:
                return DownloadModel.versucheZuVerbinden;
            case DownloadSourceDO.GEGENSTELLE_HAT_ZU_ALTE_VERSION:
                return DownloadModel.ggstZuAlteVersion;
            case DownloadSourceDO.GEGENSTELLE_KANN_DATEI_NICHT_OEFFNEN:
                return DownloadModel.kannDateiNichtOeffnen;
            case DownloadSourceDO.IN_WARTESCHLANGE: {
                String temp = DownloadModel.position;
                temp = temp.replaceFirst("%d",
                                         Integer.toString(getQueuePosition()));
                return DownloadModel.warteschlange + " " + temp;
            }
            case DownloadSourceDO.KEINE_BRAUCHBAREN_PARTS:
                return DownloadModel.keineBrauchbarenParts;
            case DownloadSourceDO.UEBERTRAGUNG:
                return DownloadModel.uebertragung;
            case DownloadSourceDO.NICHT_GENUEGEND_PLATZ_AUF_DER_PLATTE:
                return DownloadModel.nichtGenugPlatz;
            case DownloadSourceDO.FERTIGGESTELLT:
                return DownloadModel.fertiggestellt;
            case DownloadSourceDO.KEINE_VERBINDUNG_MOEGLICH:
                return DownloadModel.keineVerbindungMoeglich;
            case DownloadSourceDO.PAUSIERT:
                return DownloadModel.pausiert;
            case DownloadSourceDO.VERSUCHE_INDIREKT:
                return DownloadModel.versucheIndirekt;
            case DownloadSourceDO.WARTESCHLANGE_VOLL:
                return DownloadModel.warteschlangeVoll;
            case DownloadSourceDO.EIGENES_LIMIT_ERREICHT:
                return DownloadModel.eigenesLimitErreicht;
            case DownloadSourceDO.INDIREKTE_VERBINDUNG_ABGELEHNT:
                return DownloadModel.indirekteVerbindungAbgelehnt;

            default:
                return "";
        }
    }

    public String getColumn2() {
        int size = getSize();
        if (size != oldSize) {
            oldSize = size;
            sizeAsString = DownloadModel.parseGroesse(size);
        }
        return sizeAsString;
    }

    public String getColumn3() {
        int bereitsGeladen = getBereitsGeladen();
        if (bereitsGeladen != oldBereitsGeladen) {
            oldBereitsGeladen = bereitsGeladen;
            bereitsGeladenAsString = DownloadModel.parseGroesse(bereitsGeladen);
        }
        return bereitsGeladenAsString;
    }

    public String getColumn4() {
        if (getStatus() != DownloadSourceDO.UEBERTRAGUNG) {
            return "";
        }
        else {
            return DownloadModel.getSpeedAsString( (long) getSpeed());
        }
    }

    public String getColumn5() {
        return getRestZeitAsString();
    }

    public String getColumn6() {
        return "";
    }

    public String getColumn7() {
        int nochZuLaden = getNochZuLaden();
        if (nochZuLaden != oldNochZuLaden) {
            oldNochZuLaden = nochZuLaden;
            nochZuLadenAsString = DownloadModel.parseGroesse(nochZuLaden);
        }
        return nochZuLadenAsString;
    }

    public String getColumn8() {
        return DownloadModel.powerdownload(getPowerDownload());
    }

    public String getColumn9() {
        if (getVersion() != null) {
            return getVersion().getVersion();
        }
        else {
            return "";
        }
    }

    public Component getProgressbarComponent(JTable table, Object value) {
        if (status == DownloadSourceDO.UEBERTRAGUNG) {
            if (progressChanged){
                String prozent = getDownloadPercentAsString();
                int pos = prozent.indexOf('.');
                String balken = prozent;
                if (pos != -1) {
                    balken = balken.substring(0, pos);
                }
                progress.setValue(Integer.parseInt(balken));
                progress.setString(prozent + " %");
                progressChanged = false;
            }
            return progress;
        }
        else {
            return progressbarLabel;
        }
    }

    public Component getVersionComponent(JTable table, Object value) {
        if(versionChanged){
            if (getVersion() == null) {
                versionLabel.setIcon(null);
                versionLabel.setText("");
            }
            else {
                versionLabel.setFont(table.getFont());
                versionLabel.setIcon(getVersion().getVersionIcon());
                versionLabel.setText(getVersion().getVersion());
            }
            versionChanged = false;
        }
        return versionLabel;
    }
}
