package de.applejuicenet.client.gui.plugins;

import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import de.applejuicenet.client.gui.RegisterI;
import de.applejuicenet.client.gui.listener.DataUpdateListener;
import de.applejuicenet.client.gui.listener.LanguageListener;

/**
 * <p>Titel: AppleJuice Core-GUI</p>
 *
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten
 * appleJuice-Core</p>
 *
 * <p><b>Copyright: General Public License</b></p>
 *
 * <p>Diese Klasse darf nicht veraendert werden!
 * Um ein Plugin zu erstellen, muss diese Klasse überschrieben werden.
 * Die Pluginklasse muss zwingend wie das jar-File heissen.
 * Beim Pluginstart wird automatisch der Standardkonstruktor aufgerufen, alle anderen werden ignoriert.</p>
 *
 * @author: Maj0r [maj0r@applejuicenet.de]
 */

public abstract class PluginConnector
    extends JPanel
    implements LanguageListener, DataUpdateListener, RegisterI {

    private final ImageIcon pluginIcon;
    private final PluginsPropertiesXMLHolder pluginsPropertiesXMLHolder;

    protected PluginConnector(PluginsPropertiesXMLHolder pluginsPropertiesXMLHolder, ImageIcon icon){
        if (pluginsPropertiesXMLHolder == null || icon == null){
            throw new RuntimeException("Plugin nicht richtig implementiert");
        }
        this.pluginsPropertiesXMLHolder = pluginsPropertiesXMLHolder;
        pluginIcon = icon;
    }

    public final String getXMLAttributeByTagName(String identifier){
        return pluginsPropertiesXMLHolder.getXMLAttributeByTagName(identifier);
    }

    /**
     *
     * @return String: Titel, der als Reitertext ausgegeben wird
     */
    public final String getTitle(){
        return getXMLAttributeByTagName(".root.general.title.value");
    }

    /**
     *
     * @return String: Versions-Nr
     */
    public final String getVersion(){
        return getXMLAttributeByTagName(".root.general.version.value");
    }

    /**
     *
     * @return String: Name des Autors
     */
    public final String getAutor(){
        return getXMLAttributeByTagName(".root.general.author.value");
    }

    /**
     *
     * @return boolean: Liefert true zurück, wenn das Plugin eine sichtbare Oberflaeche haben soll, sonst false
     */
    public final boolean istReiter(){
        return getXMLAttributeByTagName(".root.general.istab.value").toLowerCase().equals("true");
    }

    /**
     *
     * @return String: Liefert eine Kurzbeschreibung des Plugins zurück.
     */
    public final String getBeschreibung(){
        return getXMLAttributeByTagName(".root.general.description.value");
    }

    /**
     *
     * @return JPanel: Liefert eine JPanel fuer den Optionsdialog oder NULL, wenn keine Optionen vorhanden sind
     */
    public JPanel getOptionPanel() {
        return null;
    }

    /**
     * Liefert ein Icon zurueck, welches in der Lasche angezeigt werden soll. Es muss als icon.gif im package plugins gespeichert
     * werden, damit es spaeter an die richtige Stelle im jar-Archiv wandert (ca. 16x16)
     *
     * @return ImageIcon: LaschenIcon
     */
    public final ImageIcon getIcon() {
        return pluginIcon;
    }

    /**
     * Wird aufgerufen, wenn der Reiter fuer dieses Plugin selektiert wurde.
     */
    public abstract void registerSelected();

    /**
     * Wird aufgerufen, wenn der Reiter fuer dieses Plugin die Selektion verliert.
     */
    public void lostSelection() {};

    /**
     * Wird automatisch aufgerufen, wenn die Sprache geändert wurde.
     * zB kann an dieser Stelle eine eigene xml-Datei zur Anpassung der eigenen Panels ausgewertet werden
     **/
    public abstract void fireLanguageChanged();

    /**
     * Wird automatisch aufgerufen, wenn neue Informationen vom Server
     * eingegangen sind. Über den DataManger können diese abgerufen werden.
     *
     * @param type int: Typ des angesprochenen Listeners
     * @param content Object: Geaenderte Werte, Typ abhaengig vom
     *   angesprochenen Listener
     * @see DataUpdateListener.class
     */
    public abstract void fireContentChanged(int type, Object content);
}
