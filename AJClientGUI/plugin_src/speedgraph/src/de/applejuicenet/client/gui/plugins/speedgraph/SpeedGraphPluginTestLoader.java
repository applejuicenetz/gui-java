/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.plugins.speedgraph;

import java.util.Map;

import javax.swing.ImageIcon;

import de.applejuicenet.client.fassade.controller.xml.XMLValueHolder;
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
   protected PluginConnector getPlugin(XMLValueHolder pluginsPropertiesXMLHolder, Map<String, XMLValueHolder> languageFiles,
                                       ImageIcon icon, Map<String, ImageIcon> availableIcons)
   {
      return new SpeedGraphPlugin(pluginsPropertiesXMLHolder, languageFiles, icon, availableIcons);
   }
}
