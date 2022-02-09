package de.applejuicenet.client.gui.powerdownload;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.fassade.entity.DownloadSource;
import de.applejuicenet.client.fassade.exception.IllegalArgumentException;
import de.applejuicenet.client.gui.AppleJuiceDialog;

import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * $Header:
 * /cvsroot/applejuicejava/AJClientGUI/pwdl_policy_src/standardpwdlpolicy/src/de/applejuicenet/client/gui/powerdownload/StandardAutomaticPwdlPolicy.java,v
 * 1.17 2005/02/17 22:22:21 loevenwong Exp $
 *
 * <p>
 * Titel: AppleJuice Client-GUI
 * </p>
 * <p>
 * Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten
 * appleJuice-Core
 * </p>
 * <p>
 * Copyright: General Public License
 * </p>
 *
 * @author Maj0r [aj@tkl-soft.de]
 *
 */

public class StandardAutomaticPwdlPolicy extends AutomaticPowerdownloadPolicy {

    private int pwdlValue = 12;
    private int anzahlDownloads = 2;
    private List<Download> resumedDownloads = new Vector();
    private List<Download> pausedDownloads = new Vector();
    private int sleeptime = 30000;
    private EinstellungenDialog settingsDialog = null;
    private int informedPowerdownload = pwdlValue;
    private Reihenfolge[] reihenfolge = new Reihenfolge[] {
            Reihenfolge.SOURCEN, Reihenfolge.PROZENT_GELADEN,
            Reihenfolge.GROESSE };

    public static enum Reihenfolge {
        SOURCEN, PROZENT_GELADEN, GROESSE, ID
    };

    public StandardAutomaticPwdlPolicy(ApplejuiceFassade applejuiceFassade) {
        super(applejuiceFassade);
    }

    public StandardAutomaticPwdlPolicy(ApplejuiceFassade applejuiceFassade,
            int anzahlDownloads, int pwdlValue, Reihenfolge[] reihenfolge) {
        super(applejuiceFassade);
        this.pwdlValue = pwdlValue;
        this.anzahlDownloads = anzahlDownloads;
        this.reihenfolge = reihenfolge;
    }

    public boolean initAction() {
        shouldPause = false;
        showPropertiesDialog(AppleJuiceDialog.getApp());
        return true;
    }

    public void doAction() throws Exception {
        Map downloads = applejuiceFassade.getDownloadsSnapshot();
        int downloadSize = downloads.size();
        if (downloadSize == 0) {
            return;
        }
        synchronized (downloads) {
            TreeMap<Sortierkriterium, Download> naechsteDownloads = new TreeMap(
                    new SortierkriteriumComparator());
            if (downloadSize <= anzahlDownloads) {
                // alle auf pd setzen und resuemen...
                List power = new Vector(downloads.values());
                setPowerDownload(power);
                applejuiceFassade.resumeDownload(power);
            } else {
                Iterator<Download> it = downloads.values().iterator();
                while (it.hasNext()) {
                    Download current = it.next();
                    if (current.getStatus() == Download.PAUSIERT
                            || current.getStatus() == Download.SUCHEN_LADEN) {
                        naechsteDownloads.put(new Sortierkriterium(current),
                                current);
                    }
                }
                int pos = 0;
                List<Download> downloads2Start = new Vector();
                List<Download> downloads2Stop = new Vector();
                for (Download cur : naechsteDownloads.values()) {
                    if (pos < anzahlDownloads) {
                        downloads2Start.add(cur);
                    } else {
                        downloads2Stop.add(cur);
                    }
                    pos++;
                }
                if (changed(downloads2Start, downloads2Stop)) {
                    setPowerDownload(downloads2Start);
                    applejuiceFassade.resumeDownload(downloads2Start);
                    applejuiceFassade.pauseDownload(downloads2Stop);
                } else if (informedPowerdownload != pwdlValue) {
                    setPowerDownload(downloads2Start);
                }
            }
        }
    }

    private void setPowerDownload(List<Download> downloads2Start)
            throws IllegalArgumentException {
        applejuiceFassade.setPowerDownload(downloads2Start, new Integer(
                pwdlValue));
        informedPowerdownload = pwdlValue;
    }

    private boolean changed(List<Download> downloads2Start,
            List<Download> downloads2Stop) {
        boolean changed = false;
        if (downloads2Start.size() != resumedDownloads.size()) {
            changed = true;
        }
        if (downloads2Stop.size() != pausedDownloads.size()) {
            changed = true;
        }
        if (!changed) {
            if (!listsEquals(downloads2Start, resumedDownloads)) {
                changed = true;
            }
        }
        if (!changed) {
            if (!listsEquals(downloads2Stop, pausedDownloads)) {
                changed = true;
            }
        }
        resumedDownloads = downloads2Start;
        pausedDownloads = downloads2Stop;
        return changed;
    }

    private boolean listsEquals(List<Download> erste, List<Download> zweite) {
        for (int i = 0; i < erste.size(); i++) {
            if (!downloadEquals(erste.get(i), zweite.get(i))) {
                return false;
            }
        }
        return true;
    }

    private boolean downloadEquals(Download download, Download download2) {
        if (download.getId() != download2.getId()) {
            return false;
        }
        if (download.getPowerDownload() != download2.getPowerDownload()) {
            return false;
        }
        return true;
    }

    public void informPaused() {
    }

    public String getVersion() {
        return "1.3.1";
    }

    public String getDescription() {
        String description = "Powerdownload wird fuer die prozentual weitesten Downloads aktiviert."
                + "Wenn einer fertig ist, wird der Naechste aktiviert."
                + "Der Pwdl-Wert ist frei einstellbar.";
        return description;
    }

    public String getAuthor() {
        return "Maj0r & loevenwong";
    }

    public String toString() {
        return "StandardPwdlPolicy Vers. " + getVersion();
    }

    protected int getSleeptime() {
        return this.sleeptime;
    }

    public boolean hasPropertiesDialog() {
        return true;
    }

    public void showPropertiesDialog(Frame parent) {
        double wert = 0;
        if (settingsDialog == null) {
            settingsDialog = new EinstellungenDialog(parent);
        }
        settingsDialog.setVisible(true);
        anzahlDownloads = settingsDialog.getAnzahlDownloads();
        pwdlValue = settingsDialog.getPowerDownload();
        sleeptime = settingsDialog.getSleeptime();
        reihenfolge = settingsDialog.getReihenfolge();
    }

    private class Sortierkriterium {

        private Double prozentGeladen;
        private Long groesse;
        private Integer id;
        private Integer quellenAnzahl = new Integer(0);

        public Sortierkriterium(Double prozentGeladen, Long groesse,
                Integer id, DownloadSource[] quellen) {
            this.prozentGeladen = prozentGeladen;
            this.groesse = groesse;
            this.id = id;
            this.quellenAnzahl = new Integer(quellen.length);
        }

        public Sortierkriterium(Download current) {
            this.prozentGeladen = current.getProzentGeladen();
            this.groesse = (long) current.getGroesse();
            this.id = current.getId();
            if (current.getSources() != null) {
                this.quellenAnzahl = current.getSources().length;
            }
        }

        public int compareTo(Sortierkriterium comparable) {
            int compareResult = 0;
            for (int i = 0; i < reihenfolge.length && compareResult == 0; i++) {
                if (reihenfolge[i] == Reihenfolge.SOURCEN) {
                    compareResult = this.quellenAnzahl
                            .compareTo(comparable.quellenAnzahl)
                            * -1;
                } else if (reihenfolge[i] == Reihenfolge.PROZENT_GELADEN) {
                    compareResult = this.prozentGeladen
                            .compareTo(comparable.prozentGeladen)
                            * -1;
                } else if (reihenfolge[i] == Reihenfolge.GROESSE) {
                    compareResult = this.groesse.compareTo(comparable.groesse)
                            * -1;
                } else {
                    compareResult = this.id.compareTo(comparable.id);
                }
            }
            return compareResult;
        }
    }

    private class SortierkriteriumComparator implements Comparator {

        public int compare(Object arg0, Object arg1) {
            int ret = ((Sortierkriterium) arg0)
                    .compareTo((Sortierkriterium) arg1);
            return ret;
        }
    }
}
