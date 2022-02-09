/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.plugins;

import de.applejuicenet.client.fassade.listener.DataUpdateListener;
import de.applejuicenet.client.gui.RegisterI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * <p>Titel: AppleJuice Core-GUI</p>
 *
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten
 * appleJuice-Core</p>
 *
 * <p><b>Copyright: General Public License</b></p>
 *
 * <p>Diese Klasse darf nicht veraendert werden!
 * Um ein Plugin zu erstellen, muss diese Klasse ueberschrieben werden.</p>
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
         throw new RuntimeException("properties nicht uebergeben");
      }

      if(null == icon)
      {
         throw new RuntimeException("Icon nicht uebergeben");
      }

      if(null == languageFiles)
      {
         throw new RuntimeException("Sprachdateien nicht uebergeben");
      }

      if(null == availableIcons)
      {
         Logger logger = LoggerFactory.getLogger(getClass());
         logger.info("Map availableIcons nicht uebergeben");
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

   /**
    *
    * @return String: Titel, der als Reitertext ausgegeben wird
    */
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

   /**
    *
    * @return String: Name des Autors
    */
   public final String getAutor()
   {
      return getGeneralXMLAttributeByTagName("general.author");
   }

   /**
    *
    * @return String: Kontaktadresse
    */
   public final String getContact()
   {
      return getGeneralXMLAttributeByTagName("general.contact");
   }

   /**
    *
    * @return boolean: Liefert true zurueck, wenn das Plugin eine sichtbare Oberflaeche haben soll, sonst false
    */
   public final boolean istReiter()
   {
      return getGeneralXMLAttributeByTagName("general.istab").toLowerCase().equals("true");
   }

   /**
    *
    * @return String: Liefert eine Kurzbeschreibung des Plugins zurueck.
    */
   public final String getBeschreibung()
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
    *
    * @return JPanel: Liefert ein JPanel fuer den Optionsdialog oder NULL, wenn keine Optionen vorhanden sind
    */
   public JPanel getOptionPanel()
   {
      return null;
   }

   /**
    * Liefert ein Icon zurueck, welches in der Lasche angezeigt werden soll. Es muss als icon.png im Plugin-Unterordner /icons gespeichert
    * werden, damit es spaeter an die richtige Stelle im jar-Archiv wandert (ca. 16x16)
    *
    * @return ImageIcon: LaschenIcon
    */
   public final ImageIcon getIcon()
   {
      return pluginIcon;
   }

   /**
    * Liefert anhand seines Dateinamens ohne Suffix ein Icon zurueck, welches in der Lasche angezeigt werden soll. Es muss im Plugin-Unterordner /icons gespeichert
    * werden, damit es spaeter an die richtige Stelle im jar-Archiv wandert (ca. 16x16)
    *
    * @return ImageIcon: ImageIcon
    */
   public final ImageIcon getAvailableIcon(String name)
   {
      return availableIcons.get(name);
   }

   /**
    * Wird aufgerufen, wenn der Reiter fuer dieses Plugin die Selektion verliert.
    */
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

   /**
    * Wird aufgerufen, wenn der Reiter fuer dieses Plugin selektiert wurde.
    */
   public abstract void registerSelected();

   /**
    * Wird automatisch aufgerufen, wenn neue Informationen vom Server
    * eingegangen sind. Ueber den DataManger koennen diese abgerufen werden.
    *
    * @param type int: Typ des angesprochenen Listeners
    * @param content Object: Geaenderte Werte, Typ abhaengig vom
    *   angesprochenen Listener
    * @see DataUpdateListener
    */
   public abstract void fireContentChanged(DATALISTENER_TYPE type, Object content);

   /**
    * Wird automatisch aufgerufen, wenn die Sprache geaendert wurde.
    * zB kann an dieser Stelle eine eigene xml-Datei zur Anpassung der eigenen Panels ausgewertet werden
    **/
   public abstract void fireLanguageChanged();
}
