package de.applejuicenet.client.gui.plugins.jabber;

import java.util.Map;

import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

import de.applejuicenet.client.fassade.controller.xml.XMLValueHolder;
import de.applejuicenet.client.gui.plugins.PluginConnector;

public class JabberPlugin extends PluginConnector
{
   private Logger logger;

   public JabberPlugin(XMLValueHolder pluginsPropertiesXMLHolder, Map languageFiles, ImageIcon icon)
   {
      super(pluginsPropertiesXMLHolder, languageFiles, icon);
      logger = Logger.getLogger(getClass());
   }

   @Override
   public void fireContentChanged(DATALISTENER_TYPE type, Object content)
   {
   }

   @Override
   public void fireLanguageChanged()
   {
   }

   @Override
   public void registerSelected()
   {
   }
}
