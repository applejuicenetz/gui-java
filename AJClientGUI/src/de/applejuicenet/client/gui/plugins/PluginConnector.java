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
 * <p>Diese Klasse darf nicht verändert werden!
 * Um ein Plugin zu erstellen, muss diese Klasse überschrieben werden.
 * Die Pluginklasse muss zwingend wie das jar-File heissen.
 * Beim Pluginstart wird automatisch der Standardkonstruktor aufgerufen, alle anderen werden ignoriert.</p>
 *
 * @author: Maj0r [maj0r@applejuicenet.de]
 */

public abstract class PluginConnector
    extends JPanel
    implements LanguageListener, DataUpdateListener, RegisterI {

    protected ImageIcon pluginIcon = null;
    private boolean initialized = false;

    /**
     *
     * @return String: Titel, der als Reitertext ausgegeben wird
     */
    public abstract String getTitle();

    /**
     *
     * @return String: Versions-Nr
     */
    public abstract String getVersion();

    /**
     *
     * @return String: Name des Autors
     */
    public abstract String getAutor();

    /**
     *
     * @return boolean: Liefert true zurück, wenn das Plugin eine sichtbare Oberflaeche haben soll, sonst false
     */
    public abstract boolean istReiter();

    /**
     *
     * @return String: Liefert eine Kurzbeschreibung des Plugins zurück.
     */
    public abstract String getBeschreibung();

    /**
     *
     * @return JPanel: Liefert eine JPanel fuer den Optionsdialog oder NULL, wenn keine Optionen vorhanden sind
     */
    public JPanel getOptionPanel() {
        return null;
    }

    protected void initIcon() {
        if (!initialized) {
            initialized = true;
            try {
                String classname = getClass().toString();
                String path = System.getProperty("user.dir") + File.separator +
                    "plugins" +
                    File.separator +
                    classname.substring(classname.lastIndexOf('.') + 1) +
                    ".jar";
                File aJar = new File(path);
                JarFile jf = new JarFile(aJar);
                String entryName;

                for (Enumeration e = jf.entries(); e.hasMoreElements(); ) {
                    ZipEntry entry = (ZipEntry) e.nextElement();
                    entryName = entry.getName();
                    if (entryName.indexOf("icon.gif") != -1) {
                        InputStream is = jf.getInputStream(entry);
                        int l = (int) entry.getSize();
                        byte[] buf = new byte[l];
                        int read = 0;

                        while (read < l) {
                            int incr = is.read(buf, read, l - read);
                            read += incr;
                        }
                        pluginIcon = new ImageIcon(buf);
                        return;
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                pluginIcon = null;
            }
        }
    }

    /**
     * Liefert ein Icon zurueck, welches in der Lasche angezeigt werden soll. Es muss als icon.gif im package plugins gespeichert
     * werden, damit es spaeter an die richtige Stelle im jar-Archiv wandert (ca. 16x16)
     *
     * @return ImageIcon: LaschenIcon
     */
    public ImageIcon getIcon() {
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
