package de.tklsoft.icon;

import de.applejuicenet.client.gui.controller.OptionsManagerImpl;

import java.awt.Image;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;

public class IconManager {

    private static IconManager instance = null;
    private final Map<Object, Object> icons = new HashMap<>();

    public static IconManager getInstance() {
        if (instance == null) {
            instance = new IconManager();
        }

        return instance;
    }

    public ImageIcon getIcon(String key) {
        ImageIcon result;

        String iconSet = OptionsManagerImpl.getInstance().getIconSetName();

        try {
            if (icons.containsKey(key)) {
                result = (ImageIcon) icons.get(key);
            } else {
                String url = System.getProperty("user.dir") + File.separator + "icons"  + File.separator + iconSet + File.separator + key + ".png";
                Image img = (new ImageIcon(url)).getImage();
                result = new ImageIcon(img);
                icons.put(key, result);
            }

            return result;
        } catch (Exception var6) {
            var6.printStackTrace();
            return null;
        }
    }

}
