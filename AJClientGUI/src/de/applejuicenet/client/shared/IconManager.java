package de.applejuicenet.client.shared;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/IconManager.java,v 1.13 2004/10/15 13:34:47 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */

public class IconManager {
    private static IconManager instance = null;
    private Map icons;
    private static Logger logger;

    private IconManager() {
        icons = new HashMap();
    }

    public static IconManager getInstance() {
        if (instance == null) {
            instance = new IconManager();
            logger = Logger.getLogger(instance.getClass());
        }
        return instance;
    }

    public ImageIcon getIcon(String key) {
        ImageIcon result = null;
        try {
            String hashtableKey = key;
            if (icons.containsKey(hashtableKey)) {
                result = (ImageIcon) icons.get(hashtableKey);
            }
            else {
                String path = System.getProperty("user.dir") + File.separator +
	            "icons" + File.separator + key + ".gif";
                Image img = Toolkit.getDefaultToolkit().getImage(path);
                result = new ImageIcon(img);
                icons.put(hashtableKey, result);
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.INFO)) {
                logger.info("Icon " + key + ".gif nicht gefunden", e);
            }
        }
        return result;
    }
}