package de.applejuicenet.client.shared;

import java.net.*;
import java.util.*;

import java.awt.*;
import javax.swing.*;

import de.applejuicenet.client.shared.icons.*;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/IconManager.java,v 1.7 2003/11/03 15:04:27 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI f�r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: IconManager.java,v $
 * Revision 1.7  2003/11/03 15:04:27  maj0r
 * Nix wesentliches.
 *
 * Revision 1.6  2003/09/03 12:31:07  maj0r
 * Logger eingebaut.
 *
 * Revision 1.5  2003/07/01 14:50:37  maj0r
 * Inner-Class Key ausgelagert und umbenannt.
 *
 * Revision 1.4  2003/06/30 20:35:50  maj0r
 * Code optimiert.
 *
 * Revision 1.3  2003/06/10 12:31:03  maj0r
 * Historie eingef�gt.
 *
 *
 */

public class IconManager {
    private static IconManager instance = null;
    private HashMap icons;
    private static Logger logger;

    private IconManager() {
        icons = new HashMap();
    }

    public static IconManager getInstance() {
        if (instance == null)
        {
            instance = new IconManager();
            logger = Logger.getLogger(instance.getClass());
        }
        return instance;
    }

    public ImageIcon getIcon(String key) {
        ImageIcon result = null;
        try
        {
            MapSetStringKey hashtableKey = new MapSetStringKey(key);
            if (icons.containsKey(hashtableKey))
            {
                result = (ImageIcon) icons.get(hashtableKey);
            }
            else
            {
                URL url = new DummyClass().getClass().getResource(key + ".gif");
                Image img = Toolkit.getDefaultToolkit().getImage(url);
                result = new ImageIcon(img);
                icons.put(hashtableKey, result);
            }
        }
        catch(Exception e){
            if (logger.isEnabledFor(Level.INFO))
                logger.info("Icon "+ key + ".gif nicht gefunden", e);
        }
        return result;
    }
}