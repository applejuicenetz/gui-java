package de.applejuicenet.client.gui.powerdownload;

import java.util.HashSet;
import java.util.Iterator;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/powerdownload/AutomaticPowerdownloadPolicy.java,v 1.1 2003/11/17 07:32:30 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: AutomaticPowerdownloadPolicy.java,v $
 * Revision 1.1  2003/11/17 07:32:30  maj0r
 * Automatischen Pwdl begonnen.
 *
 *
 */

public abstract class AutomaticPowerdownloadPolicy extends Thread{

    private HashSet threads = new HashSet();

    protected boolean paused = false;

    public final void run(){
        try{
            doAction();
        }
        catch(InterruptedException iE){
            interrupt();
        }
        catch(Exception e){
            //Von fehlerhaften Policies lassen wir uns nicht stoeren...
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public final void interrupt(){
        Iterator it = threads.iterator();
        while (it.hasNext()){
            Object obj = it.next();
            if (obj instanceof Thread){
                ((Thread)obj).interrupt();
            }
        }
        super.interrupt();
    }

    public final void setPaused(boolean pause){
        paused = pause;
    }

    /**
     *  Umsetzung der Powerdownloadverarbeitung
     **/
    public abstract void doAction() throws InterruptedException;

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
