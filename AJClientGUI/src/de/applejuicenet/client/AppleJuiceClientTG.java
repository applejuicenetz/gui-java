package de.applejuicenet.client;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.Frame;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/AppleJuiceClientTG.java,v 1.1 2004/01/30 16:32:47 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: AppleJuiceClientTG.java,v $
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
        if (logger.isEnabledFor(Level.ERROR)) {
            logger.error("Unbehandelte Exception", e);
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