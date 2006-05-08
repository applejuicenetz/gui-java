package de.applejuicenet.client.gui.plugins.jabber;

import java.util.Map;

import javax.swing.ImageIcon;

import de.applejuicenet.client.fassade.controller.xml.XMLValueHolder;
import de.applejuicenet.client.gui.plugincontrol.TestLoader;
import de.applejuicenet.client.gui.plugins.PluginConnector;

public class JabberTestLoader extends TestLoader
{
   @Override
   protected String getPath()
   {
      return "jabber";
   }

   @Override
   protected PluginConnector getPlugin(XMLValueHolder pluginsPropertiesXMLHolder, Map<String, XMLValueHolder> languageFiles,
      ImageIcon icon, Map<String, ImageIcon> availableIcons)
   {
      return new JabberPlugin(pluginsPropertiesXMLHolder, languageFiles, icon, availableIcons);
   }
}
