package de.tklsoft.icon;

import java.awt.Image;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;

public class IconManager {

   private static IconManager instance = null;
   private Map icons = new HashMap();


   public static IconManager getInstance() {
      if(instance == null) {
         instance = new IconManager();
      }

      return instance;
   }

   public ImageIcon getIcon(String key) {
      ImageIcon result = null;

      try {
         if(this.icons.containsKey(key)) {
            result = (ImageIcon)this.icons.get(key);
         } else {
            String url = System.getProperty("user.dir") + File.separator + "icons" + File.separator + key + ".gif";
            Image img = (new ImageIcon(url)).getImage();
            result = new ImageIcon(img);
            this.icons.put(key, result);
         }

         return result;
      } catch (Exception var6) {
         var6.printStackTrace();
         return null;
      }
   }

}
