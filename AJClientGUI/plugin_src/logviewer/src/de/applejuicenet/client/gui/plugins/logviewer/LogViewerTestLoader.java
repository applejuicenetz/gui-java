package de.applejuicenet.client.gui.plugins.logviewer;

import java.util.Map;

import javax.swing.ImageIcon;

import de.applejuicenet.client.fassade.controller.xml.XMLValueHolder;
import de.applejuicenet.client.gui.plugincontrol.TestLoader;
import de.applejuicenet.client.gui.plugins.PluginConnector;

public class LogViewerTestLoader extends TestLoader
{
   @Override
   protected String getPath()
   {
      return "logviewer";
   }

   @Override
   protected PluginConnector getPlugin(XMLValueHolder pluginsPropertiesXMLHolder, Map<String, XMLValueHolder> languageFiles,
      ImageIcon icon, Map<String, ImageIcon> availableIcons)
   {
      return new LogViewerPlugin(pluginsPropertiesXMLHolder, languageFiles, icon, availableIcons);
   }
}
