package de.applejuicenet.client.gui.powerdownload;

import de.applejuicenet.client.facade.ApplejuiceFacade;
import de.applejuicenet.client.facade.entity.Download;
import de.applejuicenet.client.facade.exception.IllegalArgumentException;
import de.applejuicenet.client.gui.download.PowerDownloadPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Automatic powerdownload: keeps the first {@code downloadCount} downloads
 * (sorted by {@link Order}) active with the configured powerdownload value
 * and pauses all other waiting downloads.
 *
 * @author Maj0r [aj@tkl-soft.de] &amp; loevenwong
 */
public class AutomaticPowerdownload extends Thread {

    public enum Order {
        SOURCES, PERCENT_LOADED, SIZE, ID
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ApplejuiceFacade applejuiceFacade;
    private volatile PowerDownloadPanel parentToInformEnd;
    private volatile boolean paused = false;

    private volatile int powerdownloadValue = 12;
    private volatile int downloadCount = 2;
    private volatile int sleepTime = 30000;
    private volatile Order[] order = new Order[] {
            Order.SOURCES, Order.PERCENT_LOADED, Order.SIZE, Order.ID};

    private int informedPowerdownload = powerdownloadValue;
    private List<Download> resumedDownloads = new ArrayList<>();
    private List<Download> pausedDownloads = new ArrayList<>();

    public AutomaticPowerdownload(ApplejuiceFacade applejuiceFacade) {
        super("AutomaticPowerdownload");
        this.applejuiceFacade = applejuiceFacade;
    }

    public final void run() {
        logger.debug("Automatic powerdownload started (interval {} ms, {} downloads, power {})", sleepTime, downloadCount, powerdownloadValue);
        try {
            while (!isInterrupted()) {
                if (!paused) {
                    doAction();
                    sleep(sleepTime);
                }

                sleep(1000);
            }
        } catch (InterruptedException iE) {
            interrupt();
        } catch (Exception ex) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, ex);
        }

        logger.debug("Automatic powerdownload finished");
        PowerDownloadPanel parent = parentToInformEnd;

        parentToInformEnd = null;
        if (parent != null) {
            javax.swing.SwingUtilities.invokeLater(() -> parent.autoPowerdownloadFinished(this));
        }
    }

    public final void setParentToInform(PowerDownloadPanel parentToInformEnd) {
        this.parentToInformEnd = parentToInformEnd;
    }

    /** Applies new settings; takes effect from the next run. */
    public void applySettings(int downloadCount, int powerdownloadValue, int sleepTimeMillis, Order[] order) {
        this.downloadCount = downloadCount;
        this.powerdownloadValue = powerdownloadValue;
        this.sleepTime = sleepTimeMillis;
        this.order = order.clone();
    }

    public final synchronized void setPaused(boolean pause) {
        if (pause == paused) {
            return;
        }

        paused = pause;
        logger.debug("Automatic powerdownload {}", pause ? "paused" : "resumed");
        if (paused) {
            pauseAllDownloads();
        }
    }

    public final boolean isPaused() {
        return paused;
    }

    private void pauseAllDownloads() {
        try {
            Map<Integer, Download> downloads = applejuiceFacade.getDownloadsSnapshot();

            synchronized (downloads) {
                List<Download> downloadList = new ArrayList<>(downloads.values());

                applejuiceFacade.pauseDownload(downloadList);
                applejuiceFacade.setPowerDownload(downloadList, Integer.valueOf(0));
            }
        } catch (Exception ex) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, ex);
        }
    }

    private void doAction() throws Exception {
        Map<Integer, Download> downloads = applejuiceFacade.getDownloadsSnapshot();

        if (downloads.isEmpty()) {
            return;
        }

        synchronized (downloads) {
            int count = downloadCount;

            if (downloads.size() <= count) {
                List<Download> power = new ArrayList<>(downloads.values());

                setPowerDownload(power);
                applejuiceFacade.resumeDownload(power);
                return;
            }

            List<Download> nextDownloads = new ArrayList<>();

            for (Download current : downloads.values()) {
                if (current.getStatus() == Download.PAUSED || current.getStatus() == Download.SEARCHING_LOADING) {
                    nextDownloads.add(current);
                }
            }

            nextDownloads.sort(comparator(order));
            List<Download> downloads2Start = new ArrayList<>();
            List<Download> downloads2Stop = new ArrayList<>();

            for (int pos = 0; pos < nextDownloads.size(); pos++) {
                (pos < count ? downloads2Start : downloads2Stop).add(nextDownloads.get(pos));
            }

            if (changed(downloads2Start, downloads2Stop)) {
                logger.debug("Automatic powerdownload: {} downloads started, {} paused", downloads2Start.size(), downloads2Stop.size());
                setPowerDownload(downloads2Start);
                applejuiceFacade.resumeDownload(downloads2Start);
                applejuiceFacade.pauseDownload(downloads2Stop);
            } else if (informedPowerdownload != powerdownloadValue) {
                setPowerDownload(downloads2Start);
            }
        }
    }

    private void setPowerDownload(List<Download> downloads) throws IllegalArgumentException {
        int value = powerdownloadValue;

        applejuiceFacade.setPowerDownload(downloads, Integer.valueOf(value));
        informedPowerdownload = value;
    }

    private boolean changed(List<Download> downloads2Start, List<Download> downloads2Stop) {
        boolean changed = !listsEquals(downloads2Start, resumedDownloads)
                || !listsEquals(downloads2Stop, pausedDownloads);

        resumedDownloads = downloads2Start;
        pausedDownloads = downloads2Stop;
        return changed;
    }

    private static boolean listsEquals(List<Download> first, List<Download> second) {
        if (first.size() != second.size()) {
            return false;
        }

        for (int i = 0; i < first.size(); i++) {
            Download a = first.get(i);
            Download b = second.get(i);

            if (a.getId() != b.getId() || a.getPowerDownload() != b.getPowerDownload()) {
                return false;
            }
        }

        return true;
    }

    static Comparator<Download> comparator(Order[] order) {
        return (a, b) -> {
            int result = 0;

            for (int i = 0; i < order.length && result == 0; i++) {
                switch (order[i]) {
                    case SOURCES -> result = Integer.compare(sourcesText(b), sourcesText(a));
                    case PERCENT_LOADED -> result = Double.compare(b.getPercentLoaded(), a.getPercentLoaded());
                    case SIZE -> result = Long.compare(b.getSize(), a.getSize());
                    default -> result = Integer.compare(a.getId(), b.getId());
                }
            }

            return result != 0 ? result : Integer.compare(a.getId(), b.getId());
        };
    }

    private static int sourcesText(Download download) {
        return download.getSources() == null ? 0 : download.getSources().length;
    }
}
