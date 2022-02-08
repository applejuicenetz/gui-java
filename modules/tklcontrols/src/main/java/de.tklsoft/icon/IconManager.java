package de.tklsoft.icon;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

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

        String iconSet = "classic";

        try {
            if (icons.containsKey(key)) {
                result = (ImageIcon) icons.get(key);
            } else {
                String url = System.getProperty("user.dir") + File.separator + "icons" + File.separator + iconSet + File.separator + key + ".png";
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
