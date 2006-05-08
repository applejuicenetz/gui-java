package de.applejuicenet.client.gui.plugins.serverwatcher;

import java.util.Map;

import javax.swing.ImageIcon;

import de.applejuicenet.client.fassade.controller.xml.XMLValueHolder;
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
   protected PluginConnector getPlugin(XMLValueHolder pluginsPropertiesXMLHolder, Map<String, XMLValueHolder> languageFiles,
      ImageIcon icon, Map<String, ImageIcon> availableIcons)
   {
      return new ServerWatcherPlugin(pluginsPropertiesXMLHolder, languageFiles, icon, availableIcons);
   }
}
