package de.applejuicenet.client.shared;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;

import de.applejuicenet.client.gui.plugins.*;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class PluginJarClassLoader
    extends URLClassLoader {
  private URL url;
  public PluginJarClassLoader(URL url) {
    super(new URL[] {url});
    this.url = url;
  }

  public PluginConnector getPlugin() {
    Class aClass = null;
    try {
      aClass = loadClass("de.applejuicenet.client.gui.plugins.AppleJuicePlugin");
    }
    catch (ClassNotFoundException ex) {
      return null;
    }
    Object aPlugin = null;
    try {
      aPlugin = aClass.newInstance();
    }
    catch (Exception e) {
      return null;
    }
    if (! (aPlugin instanceof PluginConnector)) {
      return null;
    }
    return (PluginConnector) aPlugin;
  }
}