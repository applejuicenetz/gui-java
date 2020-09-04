/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.shared;

import java.awt.Image;
import java.awt.Toolkit;

import java.io.File;
import java.io.FileInputStream;

import java.io.FileNotFoundException;
import java.net.URL;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.ImageIcon;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 */
public class IconManager {
    private static IconManager instance = null;
    private final Logger logger;
    private final Map<String, ImageIcon> icons;
    private String pluginPath;

    private IconManager() {
        logger = Logger.getLogger(getClass());
        icons = new HashMap<>();
        if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
            pluginPath = System.getProperty("user.home") + File.separator + "appleJuice" + File.separator + "gui" + File.separator + "plugins" + File.separator;
        } else {
            pluginPath = System.getProperty("user.dir") + File.separator + "plugins" + File.separator;
        }
    }

    public static IconManager getInstance() {
        if (instance == null) {
            instance = new IconManager();
        }

        return instance;
    }

    public ImageIcon getIcon(String key) {
        ImageIcon result = null;

        try {

            if (icons.containsKey(key)) {
                result = icons.get(key);
            } else {
                String path;
                String pathGif = System.getProperty("user.dir") + File.separator + "icons" + File.separator + key + ".gif";
                String pathPng = System.getProperty("user.dir") + File.separator + "icons" + File.separator + key + ".png";

                File fileGif = new File(pathGif);
                File filePng = new File(pathPng);

                if (filePng.exists() && !filePng.isDirectory()) {
                    path = pathPng;
                }
                else if (fileGif.exists() && !fileGif.isDirectory()) {
                    path = pathGif;
                }
                else {
                    throw new FileNotFoundException("No Icon for " + key + " found (.gif or .png)");
                }

                Image img = Toolkit.getDefaultToolkit().getImage(path);

                result = new ImageIcon(img);
                icons.put(key, result);
            }
        } catch (Exception e) {
            if (logger.isEnabledFor(Level.INFO)) {
                logger.info("Icon " + key + ".gif nicht gefunden", e);
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    public ImageIcon getIcon(String key, boolean isPlugin, Class referenceClass) {
        if (!isPlugin) {
            return getIcon(key);
        }

        ImageIcon result = null;

        try {
            result = icons.get(key);
            if (null == result) {
                URL url = referenceClass.getClassLoader().getResource(key + ".gif");

                Image img = Toolkit.getDefaultToolkit().getImage(url);

                result = new ImageIcon(img);
                icons.put(key, result);
            }
        } catch (Exception e) {
            if (logger.isEnabledFor(Level.INFO)) {
                logger.info("Plugin-Icon " + key + ".gif nicht gefunden", e);
            }
        }

        return result;
    }

    public Properties getIconProperties(String identifier) {
        String path = System.getProperty("user.dir") + File.separator + "icons" + File.separator + identifier + ".properties";
        File aFile = new File(path);

        if (aFile.isFile()) {
            Properties props = new Properties();

            try {
                props.load(new FileInputStream(aFile));
            } catch (Exception e) {
                return null;
            }

            return props;
        } else {
            return null;
        }
    }
}
