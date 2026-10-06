package de.applejuicenet.client.gui.powerdownload;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.fassade.exception.IllegalArgumentException;
import de.applejuicenet.client.gui.download.PowerDownloadPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Automatischer Powerdownload: haelt die ersten {@code anzahlDownloads} Downloads
 * (nach {@link Reihenfolge} sortiert) mit dem eingestellten Powerdownload-Wert aktiv
 * und pausiert alle uebrigen wartenden Downloads.
 *
 * @author Maj0r [aj@tkl-soft.de] &amp; loevenwong
 */
public class AutomaticPowerdownload extends Thread {

    public enum Reihenfolge {
        SOURCEN, PROZENT_GELADEN, GROESSE, ID
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ApplejuiceFassade applejuiceFassade;
    private volatile PowerDownloadPanel parentToInformEnde;
    private volatile boolean paused = false;

    private volatile int pwdlValue = 12;
    private volatile int anzahlDownloads = 2;
    private volatile int sleeptime = 30000;
    private volatile Reihenfolge[] reihenfolge = new Reihenfolge[] {
            Reihenfolge.SOURCEN, Reihenfolge.PROZENT_GELADEN, Reihenfolge.GROESSE, Reihenfolge.ID};

    private int informedPowerdownload = pwdlValue;
    private List<Download> resumedDownloads = new ArrayList<>();
    private List<Download> pausedDownloads = new ArrayList<>();

    public AutomaticPowerdownload(ApplejuiceFassade applejuiceFassade) {
        super("AutomaticPowerdownload");
        this.applejuiceFassade = applejuiceFassade;
    }

    public final void run() {
        try {
            while (!isInterrupted()) {
                if (!paused) {
                    doAction();
                    sleep(sleeptime);
                }

                sleep(1000);
            }
        } catch (InterruptedException iE) {
            interrupt();
        } catch (Exception ex) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
        }

        PowerDownloadPanel parent = parentToInformEnde;

        parentToInformEnde = null;
        if (parent != null) {
            javax.swing.SwingUtilities.invokeLater(() -> parent.autoPwdlFinished(this));
        }
    }

    public final void setParentToInform(PowerDownloadPanel parentToInformEnde) {
        this.parentToInformEnde = parentToInformEnde;
    }

    /** Uebernimmt neue Einstellungen; wirkt ab dem naechsten Durchlauf. */
    public void applySettings(int anzahlDownloads, int pwdlValue, int sleeptimeMillis, Reihenfolge[] reihenfolge) {
        this.anzahlDownloads = anzahlDownloads;
        this.pwdlValue = pwdlValue;
        this.sleeptime = sleeptimeMillis;
        this.reihenfolge = reihenfolge.clone();
    }

    public final synchronized void setPaused(boolean pause) {
        if (pause == paused) {
            return;
        }

        paused = pause;
        if (paused) {
            pauseAllDownloads();
        }
    }

    public final boolean isPaused() {
        return paused;
    }

    private void pauseAllDownloads() {
        try {
            Map<Integer, Download> downloads = applejuiceFassade.getDownloadsSnapshot();

            synchronized (downloads) {
                List<Download> dos = new ArrayList<>(downloads.values());

                applejuiceFassade.pauseDownload(dos);
                applejuiceFassade.setPowerDownload(dos, Integer.valueOf(0));
            }
        } catch (Exception ex) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
        }
    }

    private void doAction() throws Exception {
        Map<Integer, Download> downloads = applejuiceFassade.getDownloadsSnapshot();

        if (downloads.isEmpty()) {
            return;
        }

        synchronized (downloads) {
            int anzahl = anzahlDownloads;

            if (downloads.size() <= anzahl) {
                List<Download> power = new ArrayList<>(downloads.values());

                setPowerDownload(power);
                applejuiceFassade.resumeDownload(power);
                return;
            }

            List<Download> naechste = new ArrayList<>();

            for (Download current : downloads.values()) {
                if (current.getStatus() == Download.PAUSIERT || current.getStatus() == Download.SUCHEN_LADEN) {
                    naechste.add(current);
                }
            }

            naechste.sort(comparator(reihenfolge));
            List<Download> downloads2Start = new ArrayList<>();
            List<Download> downloads2Stop = new ArrayList<>();

            for (int pos = 0; pos < naechste.size(); pos++) {
                (pos < anzahl ? downloads2Start : downloads2Stop).add(naechste.get(pos));
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

    private void setPowerDownload(List<Download> downloads) throws IllegalArgumentException {
        int wert = pwdlValue;

        applejuiceFassade.setPowerDownload(downloads, Integer.valueOf(wert));
        informedPowerdownload = wert;
    }

    private boolean changed(List<Download> downloads2Start, List<Download> downloads2Stop) {
        boolean changed = !listsEquals(downloads2Start, resumedDownloads)
                || !listsEquals(downloads2Stop, pausedDownloads);

        resumedDownloads = downloads2Start;
        pausedDownloads = downloads2Stop;
        return changed;
    }

    private static boolean listsEquals(List<Download> erste, List<Download> zweite) {
        if (erste.size() != zweite.size()) {
            return false;
        }

        for (int i = 0; i < erste.size(); i++) {
            Download a = erste.get(i);
            Download b = zweite.get(i);

            if (a.getId() != b.getId() || a.getPowerDownload() != b.getPowerDownload()) {
                return false;
            }
        }

        return true;
    }

    static Comparator<Download> comparator(Reihenfolge[] reihenfolge) {
        return (a, b) -> {
            int result = 0;

            for (int i = 0; i < reihenfolge.length && result == 0; i++) {
                switch (reihenfolge[i]) {
                    case SOURCEN -> result = Integer.compare(quellen(b), quellen(a));
                    case PROZENT_GELADEN -> result = Double.compare(b.getProzentGeladen(), a.getProzentGeladen());
                    case GROESSE -> result = Long.compare(b.getGroesse(), a.getGroesse());
                    default -> result = Integer.compare(a.getId(), b.getId());
                }
            }

            return result != 0 ? result : Integer.compare(a.getId(), b.getId());
        };
    }

    private static int quellen(Download download) {
        return download.getSources() == null ? 0 : download.getSources().length;
    }
}
