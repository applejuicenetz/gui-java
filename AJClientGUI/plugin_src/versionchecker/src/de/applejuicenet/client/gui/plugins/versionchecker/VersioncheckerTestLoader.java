package de.applejuicenet.client.gui.plugins.versionchecker;

import java.util.Map;

import javax.swing.ImageIcon;

import de.applejuicenet.client.fassade.controller.xml.XMLValueHolder;
import de.applejuicenet.client.gui.plugincontrol.TestLoader;
import de.applejuicenet.client.gui.plugins.PluginConnector;

public class VersioncheckerTestLoader extends TestLoader
{
   @Override
   protected String getPath()
   {
      return "versionchecker";
   }

   @Override
   protected PluginConnector getPlugin(XMLValueHolder pluginsPropertiesXMLHolder, Map languageFiles, ImageIcon icon)
   {
      return new VersionChecker(pluginsPropertiesXMLHolder, languageFiles, icon);
   }
}
