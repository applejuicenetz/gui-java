package de.applejuicenet.client;

import java.awt.Frame;
import javax.swing.JFrame;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/AppleJuiceClientTG.java,v 1.4 2004/03/05 15:49:39 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: AppleJuiceClientTG.java,v $
 * Revision 1.4  2004/03/05 15:49:39  maj0r
 * PMD-Optimierung
 *
 * Revision 1.3  2004/02/25 14:27:28  maj0r
 * ArrayIndexOutOfBoundsException wird nur noch im Debug gelogt.
 *
 * Revision 1.2  2004/02/05 23:11:27  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.1  2004/01/30 16:32:47  maj0r
 * MapSetStringKey ausgebaut.
 *
 *
 */

public class AppleJuiceClientTG
    extends ThreadGroup {
    private Logger logger;

    public AppleJuiceClientTG() {
        super("AppleJuiceClientThreadGroup");
        logger = Logger.getLogger(getClass());
    }

    public void uncaughtException(Thread t, Throwable e) {
        if(e.getClass()==ArrayIndexOutOfBoundsException.class){
            if (logger.isEnabledFor(Level.DEBUG)) {
                logger.debug(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
        }
        else if (logger.isEnabledFor(Level.ERROR)) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
        }
    }

    private Frame findActiveFrame() {
        Frame[] frames = JFrame.getFrames();
        for (int i = 0; i < frames.length; i++) {
            Frame frame = frames[i];
            if (frame.isVisible()) {
                return frame;
            }
        }
        return null;
    }
}
