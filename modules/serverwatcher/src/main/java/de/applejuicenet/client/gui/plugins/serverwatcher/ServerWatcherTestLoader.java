/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.plugins.serverwatcher;

import java.util.Map;
import java.util.Properties;

import javax.swing.ImageIcon;

import de.applejuicenet.client.gui.plugincontrol.TestLoader;
import de.applejuicenet.client.gui.plugins.PluginConnector;

public class ServerWatcherTestLoader extends TestLoader
{
   @Override
   protected String getPath()
   {
      return "serverwatcher";
   }

   @Override
   protected PluginConnector getPlugin(Properties pluginsProperties, Map<String, Properties> languageFiles, ImageIcon icon,
                                       Map<String, ImageIcon> availableIcons)
   {
      return new ServerWatcherPlugin(pluginsProperties, languageFiles, icon, availableIcons);
   }
}
