package de.applejuicenet.client.gui.plugins;

import java.util.*;
import java.util.zip.ZipEntry;
import java.util.jar.JarFile;

import java.io.File;
import java.io.InputStream;
import javax.swing.*;

import de.applejuicenet.client.gui.*;
import de.applejuicenet.client.gui.listener.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/plugins/PluginConnector.java,v 1.12 2003/09/13 11:31:30 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: PluginConnector.java,v $
 * Revision 1.12  2003/09/13 11:31:30  maj0r
 * Verwendung vereinfacht.
 *
 * Revision 1.11  2003/08/21 09:30:40  maj0r
 * Unnoetige Imports entfernt.
 *
 * Revision 1.10  2003/08/20 20:05:47  maj0r
 * Plugin korrigiert.
 *
 * Revision 1.9  2003/08/19 19:27:12  maj0r
 * no message
 *
 * Revision 1.8  2003/06/13 22:29:38  maj0r
 * Schnittstelle korrigiert.
 *
 * Revision 1.7  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public abstract class PluginConnector
        extends JPanel
        implements LanguageListener, DataUpdateListener, RegisterI {
    /*Diese Datei sollte nicht verändert werden!
      Um ein Plugin zu erstellen, muss diese Klasse überschrieben werden.
      Die Plugin Klasse muss zwingend wie das jar-File heissen.
      Beim Pluginstart wird automatisch der Standardkonstruktor aufgerufen, alle anderen werden ignoriert.*/

    protected ImageIcon pluginIcon = null;
    private boolean initialized = false;

    //Titel, der als Reitertext ausgegeben wird
    public abstract String getTitle();

    //Versions-Nr
    public abstract String getVersion();

    //Man will sich ja schließlich auch verewigen;-)
    public abstract String getAutor();

    //true, wenn das Plugin eine sichtbare Oberfläche haben soll
    public abstract boolean istReiter();

    //Liefert eine Kurzbeschreibung des Plugins zurück.
    public abstract String getBeschreibung();

    protected void initIcon(){
        if (!initialized) {
            initialized = true;
            try {
                String classname = getClass().toString();
                String path = System.getProperty("user.dir") + File.separator + "plugins" +
                        File.separator + classname.substring(classname.lastIndexOf('.') + 1) + ".jar";
                File aJar = new File(path);
                JarFile jf = new JarFile(aJar);
                String entryName;

                for (Enumeration e = jf.entries(); e.hasMoreElements();) {
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

    /*Das Icon, welches in der Lasche angezeigt werden soll, es muss als icon.gif im package plugins gespeichert
      werden, damit es später an die richtige Stelle im jar-Archiv wandert (ca. 16x16)*/
    public ImageIcon getIcon() {
        return pluginIcon;
    }

    //Wird aufgerufen, wenn der Reiter ausgewählt wird.
    public abstract void registerSelected();

    /*Wird automatisch aufgerufen, wenn die Sprache geändert wurde.
      Ggf. kann an dieser Stelle eine eigene xml-Datei zur Anpassung der eigenen Panels ausgewertet werden*/
    public abstract void fireLanguageChanged();

    /*Wird automatisch aufgerufen, wenn neue Informationen vom Server eingegangen sind.
      Über den DataManger können diese abgerufen werden.*/
    public abstract void fireContentChanged(int type, Object content);
}