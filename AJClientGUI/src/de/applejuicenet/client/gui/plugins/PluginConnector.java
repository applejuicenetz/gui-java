package de.applejuicenet.client.gui.plugins;

import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import de.applejuicenet.client.fassade.controller.xml.XMLValueHolder;
import de.applejuicenet.client.fassade.listener.DataUpdateListener;
import de.applejuicenet.client.gui.RegisterI;

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
 * @author: Maj0r [aj@tkl-soft.de]
 */
public abstract class PluginConnector extends JPanel implements DataUpdateListener, RegisterI
{
   private final ImageIcon                   pluginIcon;
   private final XMLValueHolder              xMLValueHolder;
   private final Map<String, XMLValueHolder> languageFiles;
   private XMLValueHolder                    currentLanguageFile;
   private Map<String, ImageIcon>            availableIcons;
   private final Logger                      logger = Logger.getLogger(getClass());

   protected PluginConnector(XMLValueHolder xMLValueHolder, Map<String, XMLValueHolder> languageFiles, ImageIcon icon,
      Map<String, ImageIcon> availableIcons)
   {
      if(null == xMLValueHolder)
      {
         throw new RuntimeException("XMLValueHolder nicht uebergeben");
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
         logger.info("Map availableIcons nicht uebergeben");
         availableIcons = new HashMap<String, ImageIcon>();
      }

      this.xMLValueHolder = xMLValueHolder;
      this.languageFiles = languageFiles;
      this.availableIcons = availableIcons;
      pluginIcon = icon;
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
      return xMLValueHolder.getXMLAttributeByTagName(identifier);
   }

   /**
    *
    * @return String: Titel, der als Reitertext ausgegeben wird
    */
   public final String getTitle()
   {
      return getGeneralXMLAttributeByTagName("general.title.value");
   }

   /**
    *
    * @return String: Versions-Nr
    */
   public final String getVersion()
   {
      return getGeneralXMLAttributeByTagName("general.version.value");
   }

   /**
    *
    * @return String: Name des Autors
    */
   public final String getAutor()
   {
      return getGeneralXMLAttributeByTagName("general.author.value");
   }

   /**
    *
    * @return String: Kontaktadresse
    */
   public final String getContact()
   {
      return getGeneralXMLAttributeByTagName("general.contact.value");
   }

   /**
    *
    * @return boolean: Liefert true zurueck, wenn das Plugin eine sichtbare Oberflaeche haben soll, sonst false
    */
   public final boolean istReiter()
   {
      return getGeneralXMLAttributeByTagName("general.istab.value").toLowerCase().equals("true");
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
         result = currentLanguageFile.getXMLAttributeByTagName("language.description.value");
      }

      if(result.length() == 0)
      {
         return getGeneralXMLAttributeByTagName("general.description.value");
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
    * Liefert ein Icon zurueck, welches in der Lasche angezeigt werden soll. Es muss als icon.gif im Plugin-Unterordner /icons gespeichert
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
         return currentLanguageFile.getXMLAttributeByTagName(identifier);
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
    * @see DataUpdateListener.class
    */
   public abstract void fireContentChanged(DATALISTENER_TYPE type, Object content);

   /**
    * Wird automatisch aufgerufen, wenn die Sprache geaendert wurde.
    * zB kann an dieser Stelle eine eigene xml-Datei zur Anpassung der eigenen Panels ausgewertet werden
    **/
   public abstract void fireLanguageChanged();
}
