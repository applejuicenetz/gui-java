/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.plugins;

import de.applejuicenet.client.facade.listener.DataUpdateListener;
import de.applejuicenet.client.gui.RegisterI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * <p>Title: AppleJuice Core-GUI</p>
 *
 * <p>Description: Official GUI for the appleJuice core developed by muhviehstarr</p>
 *
 * <p><b>Copyright: General Public License</b></p>
 *
 * <p>This class must not be changed.
 * To create a plugin, extend this class.</p>
 *
 * @author Maj0r [aj@tkl-soft.de]
 */
public abstract class PluginConnector extends JPanel implements DataUpdateListener, RegisterI
{
   private final ImageIcon               pluginIcon;
   private final Properties              properties;
   private final Map<String, Properties> languageFiles;
   private Properties                    currentLanguageFile;
   private Map<String, ImageIcon>        availableIcons;

   protected PluginConnector(Properties properties, Map<String, Properties> languageFiles, ImageIcon icon,
                             Map<String, ImageIcon> availableIcons)
   {
      if(null == properties)
      {
         throw new RuntimeException("properties not passed");
      }

      if(null == icon)
      {
         throw new RuntimeException("Icon not passed");
      }

      if(null == languageFiles)
      {
         throw new RuntimeException("Language files not passed");
      }

      if(null == availableIcons)
      {
         Logger logger = LoggerFactory.getLogger(getClass());
         logger.info("Map availableIcons not passed");
         availableIcons = new HashMap<>();
      }

      this.properties     = properties;
      this.languageFiles  = languageFiles;
      this.availableIcons = availableIcons;
      pluginIcon          = icon;
   }

   public final void setLanguage(String language)
   {
      if(languageFiles.containsKey(language))
      {
         currentLanguageFile = languageFiles.get(language);
         fireLanguageChanged();
      }
   }

   public final String getGeneralXMLAttributeByTagName(String identifier)
   {
      return properties.getProperty(identifier);
   }

   /** @return String: title shown as the tab text */
   public final String getTitle()
   {
      return getGeneralXMLAttributeByTagName("general.title");
   }

   /**
    *
    * @return String: Versions-Nr
    */
   public final String getVersion()
   {
      return getGeneralXMLAttributeByTagName("general.version");
   }

   /** @return String: name of the author */
   public final String getAuthor()
   {
      return getGeneralXMLAttributeByTagName("general.author");
   }

   /** @return boolean: true if the plugin should have a visible user interface, otherwise false */
   public final boolean isTab()
   {
      return getGeneralXMLAttributeByTagName("general.istab").toLowerCase().equals("true");
   }

   /** @return String: a short description of the plugin */
   public final String getDescription()
   {
      String result = "";

      if(currentLanguageFile != null)
      {
         result = currentLanguageFile.getProperty("language.description");
      }

      if(result.length() == 0)
      {
         return getGeneralXMLAttributeByTagName("general.description");
      }
      else
      {
         return result;
      }
   }

   /**
    * Returns the icon shown on the tab. It must be saved as icon.png in the plugin subfolder /icons so that
    * it ends up at the right place in the jar archive later (about 16x16).
    *
    * @return ImageIcon: tab icon
    */
   public final ImageIcon getIcon()
   {
      return pluginIcon;
   }

   /**
    * Returns an icon by its file name without suffix. It must be saved in the plugin subfolder /icons so that
    * it ends up at the right place in the jar archive later (about 16x16).
    *
    * @return ImageIcon: ImageIcon
    */
   public final ImageIcon getAvailableIcon(String name)
   {
      return availableIcons.get(name);
   }

   /** Called when the tab of this plugin loses the selection. */
   public void lostSelection()
   {
   }

   public final String getLanguageString(String identifier)
   {
      if(currentLanguageFile != null)
      {
         return currentLanguageFile.getProperty(identifier);
      }
      else
      {
         return "";
      }
   }

   /** Called when the tab of this plugin was selected. */
   public abstract void registerSelected();

   /**
    * Called automatically when new information has arrived from the server. It can be retrieved via
    * the DataManager. @param type int: type of the addressed listener @param content Object:
    * changed values, type depends on the addressed listener @see DataUpdateListener
    */
   public abstract void fireContentChanged(DATALISTENER_TYPE type, Object content);

   /**
    * Called automatically when the language was changed. For example, a custom xml file for
    * adapting the own panels can be evaluated here.
    */
   public abstract void fireLanguageChanged();
}
