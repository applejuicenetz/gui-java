/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.plugins.logviewer;

import de.applejuicenet.client.gui.plugincontrol.TestLoader;
import de.applejuicenet.client.gui.plugins.PluginConnector;

import javax.swing.*;
import java.util.Map;
import java.util.Properties;

public class LogViewerTestLoader extends TestLoader
{
   @Override
   protected String getPath()
   {
      return "logviewer";
   }

   @Override
   protected PluginConnector getPlugin(Properties pluginsProperties, Map<String, Properties> languageFiles, ImageIcon icon,
                                       Map<String, ImageIcon> availableIcons)
   {
      return new LogViewerPlugin(pluginsProperties, languageFiles, icon, availableIcons);
   }
}
