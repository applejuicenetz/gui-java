package de.applejuicenet.client.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/ConnectFrame.java,v 1.3 2004/03/05 15:49:38 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: ConnectFrame.java,v $
 * Revision 1.3  2004/03/05 15:49:38  maj0r
 * PMD-Optimierung
 *
 * Revision 1.2  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.1  2003/12/29 10:31:58  maj0r
 * Bug #2 gefixt (Danke an muhviestarr).
 * Wenn das Gui nicht zur Core verbinden kann, hat das GUI nun einen Taskbareintrag.
 *
 *
 */

public class ConnectFrame
    extends JFrame {

    private Logger logger;

    public ConnectFrame() {
        super();
        logger = Logger.getLogger(getClass());
        try {
            init();
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
        }
    }

    private void init() {
        setTitle("AppleJuice Client");
        IconManager im = IconManager.getInstance();
        setIconImage(im.getIcon("applejuice").getImage());
        Dimension screenSize = Toolkit.getDefaultToolkit().
            getScreenSize();
        Dimension appDimension = getSize();
        setLocation( (screenSize.width -
                      appDimension.width) / 2,
                    (screenSize.height -
                     appDimension.height) / 2);
    }
}