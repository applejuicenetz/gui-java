package de.applejuicenet.client.gui.powerdownload;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.shared.dac.DownloadDO;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/powerdownload/AutomaticPowerdownloadPolicy.java,v 1.8 2004/05/30 17:27:34 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [Maj0r@applejuicenet.de]
 *
 */

public abstract class AutomaticPowerdownloadPolicy
    extends Thread {

    private Set threads = new HashSet();
    protected ApplejuiceFassade applejuiceFassade = ApplejuiceFassade.
        getInstance();
    private Logger logger = Logger.getLogger(getClass());

    private boolean paused = true;

    //diese Variable auf false setzen, um das automatische Pausieren von Dateien zu verhindern
    protected boolean shouldPause = true;

    public final void run() {
        try {
            if (initAction()) {
                while (!isInterrupted()) {
                    if (!paused) {
                        doAction();
                    }
                    sleep(1000);
                }
            }
        }
        catch (InterruptedException iE) {
            pauseAllDownloads();
            interrupt();
        }
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
            }
        }
    }

    public final boolean shouldPause() {
        return shouldPause;
    }

    public final void interrupt() {
        try {
            Iterator it = threads.iterator();
            while (it.hasNext()) {
                Object obj = it.next();
                if (obj instanceof Thread) {
                    ( (Thread) obj).interrupt();
                }
            }
            super.interrupt();
        }
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
            }
        }
    }

    public final void setPaused(boolean pause) {
        paused = pause;
        if (pause) {
            if (shouldPause) {
                pauseAllDownloads();
            }
            try {
                informPaused();
            }
            catch (Exception ex) {
                if (logger.isEnabledFor(Level.ERROR)) {
                    logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
                }
            }
        }
    }

    private final void pauseAllDownloads() {
        try {
            Map downloads = applejuiceFassade.getDownloadsSnapshot();
            synchronized (downloads) {
                Iterator it = downloads.values().iterator();
                int[] ids = new int[downloads.size()];
                int temp = 0;
                DownloadDO downloadDO = null;
                while (it.hasNext()) {
                    downloadDO = (DownloadDO) it.next();
                    ids[temp] = downloadDO.getId();
                    temp++;
                }
                applejuiceFassade.pauseDownload(ids);
                applejuiceFassade.setPowerDownload(ids, 0);
            }
        }
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
            }
        }
    }

    public final boolean isPaused() {
        return paused;
    }

    /**
     *  Zur Instanzierung muss der Standardkonstruktor vorhanden sein!!!!!
     *  Ansonsten funktioniert der ganze Krams nicht!!!!!
     *  Laesst sich natuerlich nicht in einer abstrakten Klasse vorbauen, folglich
     *  muss der Entwickler dran denken!!!!!
     **/
    // public AutomaticPowerdownloadPolicy();

    /**
     *  Initialisierung
     *  Hier koennen Initialisierungen fuer die doAction()-Methode vorgenommen werden,
     *  sie gehoeren nicht in den Konstruktor.
     *
     **/
    public abstract boolean initAction() throws Exception;

    /**
     *  Umsetzung der Powerdownloadverarbeitung
     *  Es wird nur EIN Durchlauf mit abschlieﬂender sleep(ms)-Anweisung(!!!) implementiert.
     *  Die Schleife ergibt sich durch die run()-Methode des Threads.
     **/
    public abstract void doAction() throws Exception;

    /**
     *
     *  Wird aufgerufen um zu informieren, dass aktuell nicht mehr genug Credits
     *  vorhanden sind.
     *  So kann falls noetig darauf reagiert werden.
     *
     **/
    public abstract void informPaused() throws Exception;

    /**
     *  Versions-String
     */
    public abstract String getVersion();

    /**
     *  kurze Beschreibung der PowerdownloadPolicy
     */
    public abstract String getDescription();

    /**
     *  Name des Autors
     */
    public abstract String getAuthor();

    /**
     *  toString wird fuer die Ausgabe in der Combobox verwendet
     */
    public abstract String toString();
}
