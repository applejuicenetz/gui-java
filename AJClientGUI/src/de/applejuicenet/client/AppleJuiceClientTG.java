package de.applejuicenet.client;

import java.awt.Frame;
import javax.swing.JFrame;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.exception.WrongPasswordException;
import de.applejuicenet.client.gui.AppleJuiceDialog;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/AppleJuiceClientTG.java,v 1.7 2005/02/28 16:37:00 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
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
        if(e.getClass() == ArrayIndexOutOfBoundsException.class){
            if (logger.isEnabledFor(Level.DEBUG)) {
                logger.debug(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
        }
        else if(e.getClass() == WrongPasswordException.class){
            AppleJuiceDialog.getApp().informWrongPassword();
        }
        else {
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
