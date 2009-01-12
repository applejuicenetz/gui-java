/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.plugins.ircplugin;

import java.util.Map;
import java.util.Properties;

import javax.swing.ImageIcon;

import de.applejuicenet.client.gui.plugincontrol.TestLoader;
import de.applejuicenet.client.gui.plugins.PluginConnector;

public class IrcPluginTestLoader extends TestLoader
{
   public IrcPluginTestLoader()
   {
      super();
   }

   @Override
   protected String getPath()
   {
      return "ircplugin";
   }

   @Override
   protected PluginConnector getPlugin(Properties pluginProperties, Map<String, Properties> languageFiles, ImageIcon icon,
                                       Map<String, ImageIcon> availableIcons)
   {
      return new IrcPlugin(pluginProperties, languageFiles, icon, availableIcons);
   }
}
