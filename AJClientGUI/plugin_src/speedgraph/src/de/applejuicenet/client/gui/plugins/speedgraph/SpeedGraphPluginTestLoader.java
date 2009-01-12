/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.plugins.speedgraph;

import java.util.Map;
import java.util.Properties;

import javax.swing.ImageIcon;

import de.applejuicenet.client.gui.plugincontrol.TestLoader;
import de.applejuicenet.client.gui.plugins.PluginConnector;

public class SpeedGraphPluginTestLoader extends TestLoader
{
   public SpeedGraphPluginTestLoader()
   {
      super();
   }

   @Override
   protected String getPath()
   {
      return "speedgraph";
   }

   @Override
   protected PluginConnector getPlugin(Properties properties, Map<String, Properties> languageFiles,
                                       ImageIcon icon, Map<String, ImageIcon> availableIcons)
   {
      return new SpeedGraphPlugin(properties, languageFiles, icon, availableIcons);
   }
}
