package de.applejuicenet.client.shared;

import java.net.*;
import java.util.*;

import java.awt.*;
import javax.swing.*;

import de.applejuicenet.client.shared.icons.*;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class IconManager {
  private static IconManager instance = null;
  private HashMap icons;

  private IconManager() {
    icons = new HashMap();
  }

  public static IconManager getInstance() {
    if (instance == null) {
      instance = new IconManager();
    }
    return instance;
  }

  public ImageIcon getIcon(String key) {
    ImageIcon result;
    HashtableKey hashtableKey = new HashtableKey(key);
    if (icons.containsKey(hashtableKey)) {
      result = (ImageIcon) icons.get(hashtableKey);
    }
    else {
      URL url = new DummyClass().getClass().getResource(key + ".gif");
      Image img = Toolkit.getDefaultToolkit().getImage(url);
      result = new ImageIcon();
      result.setImage(img);
      icons.put(hashtableKey, result);
    }
    return result;
  }

  class HashtableKey {
    private String key;
    private int hashCode = -1;

    public HashtableKey(String key) {
      this.key = key;
    }

    public String getKey() {
      return key;
    }

    public int hashCode() {
      if (hashCode == -1) {
        char[] ca = key.toCharArray();
        for (int x = 0; x < ca.length; x++) {
          hashCode += ca[x];
        }
      }
      return (hashCode);
    }

    public boolean equals(Object obj) {
      if ( (obj != null) && (obj instanceof HashtableKey)) {
        return ( (HashtableKey) obj).getKey().compareToIgnoreCase(key) == 0;
      }
      return false;
    }
  }
}