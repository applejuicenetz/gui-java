package de.applejuicenet.client.gui.powerdownload;

import java.awt.Frame;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.gui.download.PowerDownloadPanel;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/powerdownload/AutomaticPowerdownloadPolicy.java,v 1.18 2005/01/19 16:22:19 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */

public abstract class AutomaticPowerdownloadPolicy
    extends Thread {

    private Set threads = new HashSet();
    private Logger logger = Logger.getLogger(getClass());
    private PowerDownloadPanel parentToInformEnde = null;
    private boolean paused = false;

    //diese Variable auf false setzen, um das automatische Pausieren von Dateien zu verhindern
    protected boolean shouldPause = true;
    protected ApplejuiceFassade applejuiceFassade = AppleJuiceClient.getAjFassade();

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
            if (shouldPause){
                pauseAllDownloads();
            }
            interrupt();
        }
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
            }
        }
        if (parentToInformEnde != null){
            parentToInformEnde.autoPwdlFinished();
            parentToInformEnde = null;
        }
        if (threads != null){
            threads.clear();
        }
        applejuiceFassade = null;
        logger = null;
    }

    public final void setParentToInform(PowerDownloadPanel parentToInformEnde){
        this.parentToInformEnde = parentToInformEnde;
    }

    public final boolean shouldPause() {
        return shouldPause;
    }

    public final void interrupt() {
        try {
            Iterator it = threads.iterator();
            while (it.hasNext()) {
                Object obj = it.next();
                try{
                    if (obj != null && obj instanceof Thread) {
                        ( (Thread) obj).interrupt();
                    }
                }
                catch(Exception ex){
                    logger.error("Fehler beim Beenden eines Threads in " + toString(), ex);
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
            Map<String, Download> downloads = applejuiceFassade.getDownloadsSnapshot();
            synchronized (downloads) {
                List<Download> dos = new Vector<Download>();
                for (Download curDownload : downloads.values()) {
                    dos.add(curDownload);
                }
                applejuiceFassade.pauseDownload(dos);
                applejuiceFassade.setPowerDownload(dos, new Integer(0));
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
     *  Es wird nur EIN Durchlauf mit abschlieszender sleep(ms)-Anweisung(!!!) implementiert.
     *  Die Schleife ergibt sich durch die run()-Methode des Threads.
     **/
    public abstract void doAction() throws Exception;

    /**
     *  Alle eigenen verwendeten Threads sollten mittels dieser Methode registriert werden.
     *  So ist sichergestellt, dass diese beim Beenden des Autom. Pwdl beendet werden.
     **/
    protected final void addThreadToWatch(Thread aThread){
        if (aThread != null){
            threads.add(aThread);
        }
    }

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

    /**
     *  Diese Methode ueberschreiben, wenn bei aktivierten Autom. Pwdl ein Button
     *  fuer Einstellungen eingeblendet werden soll
     */
    public boolean hasPropertiesDialog(){
        return false;
    }

    /**
     *  Diese Methode mit dem Öffnen und Auswerten des Einstellungendialogs ueberschreiben.
     */
    public void showPropertiesDialog(Frame parent){ }
}
