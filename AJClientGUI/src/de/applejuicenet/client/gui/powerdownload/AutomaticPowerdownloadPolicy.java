package de.applejuicenet.client.gui.powerdownload;

import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.shared.dac.DownloadDO;

import java.util.HashSet;
import java.util.Iterator;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/powerdownload/AutomaticPowerdownloadPolicy.java,v 1.2 2003/11/17 14:44:10 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: AutomaticPowerdownloadPolicy.java,v $
 * Revision 1.2  2003/11/17 14:44:10  maj0r
 * Erste funktionierende Version des automatischen Powerdownloads eingebaut.
 *
 * Revision 1.1  2003/11/17 07:32:30  maj0r
 * Automatischen Pwdl begonnen.
 *
 *
 */

public abstract class AutomaticPowerdownloadPolicy extends Thread{

    private HashSet threads = new HashSet();
    protected ApplejuiceFassade applejuiceFassade = ApplejuiceFassade.getInstance();
    private Logger logger = Logger.getLogger(getClass());

    protected boolean paused = false;

    public final void run(){
        try{
            while(!isInterrupted()){
                if(!paused){
                    doAction();
                }
                sleep(1000);
            }
        }
        catch(InterruptedException iE){
            pauseAllDownloads();
            interrupt();
        }
        catch (Exception ex)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", ex);
        }
    }

    public final void interrupt(){
        try{
            Iterator it = threads.iterator();
            while (it.hasNext()){
                Object obj = it.next();
                if (obj instanceof Thread){
                    ((Thread)obj).interrupt();
                }
            }
            super.interrupt();
        }
        catch (Exception ex)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", ex);
        }
    }

    public final void setPaused(boolean pause){
        paused = pause;
        if (pause){
            pauseAllDownloads();
        }
    }

    private final void pauseAllDownloads(){
        try{
            HashMap downloads = applejuiceFassade.getDownloadsSnapshot();
            synchronized(downloads){
                Iterator it = downloads.values().iterator();
                int[] ids = new int[downloads.size()];
                int temp = 0;
                DownloadDO downloadDO = null;
                while (it.hasNext())
                {
                    downloadDO = (DownloadDO) it.next();
                    ids[temp] = downloadDO.getId();
                    temp++;
                }
                applejuiceFassade.pauseDownload(ids);
                applejuiceFassade.setPowerDownload(ids, 0);
            }
        }
        catch (Exception ex)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", ex);
        }
    }

    public final boolean isPaused(){
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
     *  Umsetzung der Powerdownloadverarbeitung
     *  Es wird nur EIN Durchlauf mit abschließender sleep(ms)-Anweisung(!!!) implementiert.
     *  Die Schleife ergibt sich durch die run()-Methode des Threads.
     **/
    public abstract void doAction() throws Exception;

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
