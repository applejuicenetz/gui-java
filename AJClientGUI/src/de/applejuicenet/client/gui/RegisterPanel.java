package de.applejuicenet.client.gui;

import java.io.File;
import java.net.URL;

import javax.swing.DefaultSingleSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.PropertiesManager;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.gui.plugins.PluginConnector;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.PluginJarClassLoader;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.util.HashSet;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/RegisterPanel.java,v 1.34 2004/03/03 12:36:07 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: RegisterPanel.java,v $
 * Revision 1.34  2004/03/03 12:36:07  maj0r
 * Modifizierbare und potenziell modifizierbare Dateien bei Nicht-Windows-System verschoben.
 *
 * Revision 1.33  2004/03/03 11:56:53  maj0r
 * Sprachunterstuetzung fuer Plugins eingebaut.
 *
 * Revision 1.32  2004/03/02 17:37:10  maj0r
 * Pluginverwendung vereinfacht.
 *
 * Revision 1.31  2004/02/21 18:20:30  maj0r
 * LanguageSelector auf SAX umgebaut.
 *
 * Revision 1.30  2004/02/05 23:11:27  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.29  2004/02/04 14:26:05  maj0r
 * Bug #185 gefixt (Danke an muhviestarr)
 * Einstellungen des GUIs werden beim Schliessen des Core gesichert.
 *
 * Revision 1.28  2004/01/14 15:19:59  maj0r
 * Laden von Plugins verbessert.
 * Muell oder nicht standardkonforme Plugins im Plugin-Ordner werden nun korrekt behandelt.
 *
 * Revision 1.27  2004/01/05 19:17:18  maj0r
 * Bug #56 gefixt (Danke an MeineR)
 * Das Laden der Plugins beim Start kann über das Optionenmenue deaktiviert werden.
 *
 * Revision 1.26  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.25  2003/12/17 11:06:29  maj0r
 * RegisterI erweitert, um auf Verlassen eines Tabs reagieren zu koennen.
 *
 * Revision 1.24  2003/10/21 14:08:45  maj0r
 * Mittels PMD Code verschoenert, optimiert.
 *
 * Revision 1.23  2003/09/04 10:13:28  maj0r
 * Logger eingebaut.
 *
 * Revision 1.22  2003/08/27 16:44:42  maj0r
 * Unterstuetzung fuer DragNDrop teilweise eingebaut.
 *
 * Revision 1.21  2003/08/20 10:52:51  maj0r
 * JarClassloader korrigiert.
 *
 * Revision 1.20  2003/08/16 17:49:56  maj0r
 * Diverse Farben können nun manuell eingestellt bzw. deaktiviert werden.
 * DownloaduebersichtTabelle kann deaktiviert werden.
 *
 * Revision 1.19  2003/07/08 20:28:23  maj0r
 * Logger eingefügt.
 *
 * Revision 1.18  2003/07/01 14:53:12  maj0r
 * Unnützen Krimskram entfernt.
 *
 * Revision 1.17  2003/06/24 14:32:27  maj0r
 * Klassen zum Sortieren von Tabellen eingefügt.
 * Servertabelle kann nun spaltenweise sortiert werden.
 *
 * Revision 1.16  2003/06/22 19:54:45  maj0r
 * Behandlung von fehlenden Verzeichnissen und fehlenden xml-Dateien hinzugefügt.
 *
 * Revision 1.15  2003/06/13 15:07:30  maj0r
 * Versionsanzeige hinzugefügt.
 * Da der Controllerteil refactort werden kann, haben Controller und GUI separate Versionsnummern.
 *
 * Revision 1.14  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class RegisterPanel
    extends JTabbedPane
    implements LanguageListener {
    private StartPanel startPanel;
    private DownloadPanel downloadPanel;
    private SearchPanel searchPanel;
    private UploadPanel uploadPanel;
    private ServerPanel serverPanel;
    private SharePanel sharePanel;
    private AppleJuiceDialog parent;
    private Logger logger;

    public RegisterPanel(AppleJuiceDialog parent) {
        logger = Logger.getLogger(getClass());
        try {
            this.parent = parent;
            init();
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    private void tabFocusLost(int index) {
        RegisterI register = (RegisterI) getComponentAt(index);
        if (register != null) {
            register.lostSelection();
        }
    }

    private void init() {
        LanguageSelector.getInstance().addLanguageListener(this);
        setModel(new DefaultSingleSelectionModel() {
            public void setSelectedIndex(int index) {
                int oldIndex = getSelectedIndex();
                if (oldIndex != -1) {
                    tabFocusLost(oldIndex);
                }
                super.setSelectedIndex(index);
            }
        });
        startPanel = StartPanel.getInstance();
        sharePanel = SharePanel.getInstance();
        downloadPanel = DownloadPanel.getInstance();
        uploadPanel = UploadPanel.getInstance();
        searchPanel = SearchPanel.getInstance();
        serverPanel = ServerPanel.getInstance();

        IconManager im = IconManager.getInstance();

        ImageIcon icon = im.getIcon("start");
        addTab("Start", icon, startPanel);

        ImageIcon icon6 = im.getIcon("meinshare");
        addTab("Share", icon6, sharePanel);

        ImageIcon icon2 = im.getIcon("suchen");
        addTab("Suchen", icon2, searchPanel);

        ImageIcon icon3 = im.getIcon("download");
        addTab("Download", icon3, downloadPanel);

        ImageIcon icon4 = im.getIcon("upload");
        addTab("Upload", icon4, uploadPanel);

        ImageIcon icon5 = im.getIcon("server");
        addTab("Server", icon5, serverPanel);

        if (PropertiesManager.getOptionsManager().shouldLoadPluginsOnStartup()) {
            loadPlugins();
        }
    }

    private void loadPlugins() {
        String path;
        String test =System.getProperty("os.name");
        if (System.getProperty("os.name").toLowerCase().indexOf("windows")==-1) {
            path = System.getProperty("user.home") + File.separator +
                "appleJuice" + File.separator +
                "gui" + File.separator + "plugins" + File.separator;
        }
        else {
            path = System.getProperty("user.dir") + File.separator +
                "plugins" + File.separator;
        }
        File pluginPath = new File(path);
        if (!pluginPath.isDirectory()) {
            pluginPath.mkdir();
            return;
        }
        String[] tempListe = pluginPath.list();
        PluginJarClassLoader jarLoader = null;
        HashSet plugins = new HashSet();
        for (int i = 0; i < tempListe.length; i++) {
            if (tempListe[i].toLowerCase().endsWith(".jar")) {
                URL url = null;
                try {
                    File pluginFile = new File(path + tempListe[i]);
                    if (pluginFile.isFile()) {
                        //testen, ob es wirklich ein skinfile ist
                        ZipFile jf = new ZipFile(pluginFile);
                        ZipEntry entry = jf.getEntry("plugin_properties.xml");
                        if (entry==null){
                            continue;
                        }
                    }
                    else{
                        continue;
                    }
                    url = new URL("file://" + path + tempListe[i]);
                    jarLoader = new PluginJarClassLoader(url);
                    PluginConnector aPlugin = jarLoader.getPlugin(path +
                        tempListe[i]);
                    if (aPlugin != null) {
                        if (aPlugin.istReiter()) {
                            ImageIcon icon = aPlugin.getIcon();
                            addTab(aPlugin.getTitle(), icon, aPlugin);
                        }
                        plugins.add(aPlugin);
                        parent.addPluginToHashSet(aPlugin);
                        String nachricht = "Plugin " + aPlugin.getTitle() +
                            " geladen...";
                        if (logger.isEnabledFor(Level.INFO)) {
                            logger.info(nachricht);
                        }
                        System.out.println(nachricht);
                    }
                }
                catch (Exception e) {
                    //Von einem Plugin lassen wir uns nicht beirren! ;-)
                    if (logger.isEnabledFor(Level.ERROR)) {
                        logger.error(
                            "Ein Plugin konnte nicht instanziert werden", e);
                    }
                    continue;
                }
            }
        }
        LanguageSelector.getInstance().addPluginsToWatch(plugins);
    }

    public void fireLanguageChanged() {
        try {
            LanguageSelector languageSelector = LanguageSelector.getInstance();
            setTitleAt(0, ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.homesheet.caption")));
            setTitleAt(1, ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.sharesheet.caption")));
            setTitleAt(2, ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.seachsheet.caption")));
            setTitleAt(3, ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.queuesheet.caption")));
            setTitleAt(4, ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.uploadsheet.caption")));
            setTitleAt(5, ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.serversheet.caption")));
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }
}
