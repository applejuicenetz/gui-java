package de.applejuicenet.client.gui.connect;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/connect/ConnectFrame.java,v 1.2 2004/11/22 16:25:26 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
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