package de.applejuicenet.client.gui.connect;

import de.applejuicenet.client.gui.controller.GuiText;
import de.applejuicenet.client.facade.ApplejuiceFacade;
import de.applejuicenet.client.shared.IconManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/connect/ConnectFrame.java,v 1.3 2005/01/18 17:35:29 maj0r Exp $
 *
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: Official GUI for the appleJuice core developed by muhviehstarr</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 */

public class ConnectFrame
        extends JFrame {

    private Logger logger;

    public ConnectFrame() {
        super();
        logger = LoggerFactory.getLogger(getClass());
        try {
            init();
        } catch (Exception e) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
        }
    }

    private void init() {
        setTitle(GuiText.text("mainform.html.client"));
        IconManager im = IconManager.getInstance();
        setIconImage(im.getIcon("applejuice").getImage());
        Dimension screenSize = Toolkit.getDefaultToolkit().
                getScreenSize();
        Dimension appDimension = getSize();
        setLocation((screenSize.width -
                        appDimension.width) / 2,
                (screenSize.height -
                        appDimension.height) / 2);
    }
}
