package de.applejuicenet.client.gui;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.DefaultSingleSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.gui.download.DownloadController;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.gui.plugins.PluginConnector;
import de.applejuicenet.client.gui.search.SearchPanel;
import de.applejuicenet.client.gui.server.ServerPanel;
import de.applejuicenet.client.gui.share.ShareController;
import de.applejuicenet.client.gui.start.StartController;
import de.applejuicenet.client.gui.upload.UploadController;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.PluginJarClassLoader;
import de.applejuicenet.client.shared.ZeichenErsetzer;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/RegisterPanel.java,v 1.51 2004/11/22 16:25:25 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */

public class RegisterPanel
    extends JTabbedPane
    implements LanguageListener {
	  
	private StartController startController;
    private DownloadController downloadController;
    private SearchPanel searchPanel;
    private UploadController uploadController;
    private ServerPanel serverPanel;
    private ShareController shareController;
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
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
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
        startController = StartController.getInstance();
        if (AppleJuiceClient.splash != null) {
            AppleJuiceClient.splash.setProgress(40, "Lade Sharepanel...");
        }
        shareController = ShareController.getInstance();
        if (AppleJuiceClient.splash != null) {
            AppleJuiceClient.splash.setProgress(50, "Lade Downloadpanel...");
        }
        downloadController = DownloadController.getInstance();
        if (AppleJuiceClient.splash != null) {
            AppleJuiceClient.splash.setProgress(60, "Lade Uploadpanel...");
        }
        uploadController = UploadController.getInstance();
        if (AppleJuiceClient.splash != null) {
            AppleJuiceClient.splash.setProgress(70, "Lade Searchpanel...");
        }
        searchPanel = SearchPanel.getInstance();
        if (AppleJuiceClient.splash != null) {
            AppleJuiceClient.splash.setProgress(80, "Lade Serverpanel...");
        }
        serverPanel = ServerPanel.getInstance();

        IconManager im = IconManager.getInstance();

        ImageIcon icon = im.getIcon("start");
        addTab("Start", icon, startController.getComponent());

        ImageIcon icon6 = im.getIcon("meinshare");
        addTab("Share", icon6, shareController.getComponent());

        ImageIcon icon2 = im.getIcon("suchen");
        addTab("Suchen", icon2, searchPanel);

        ImageIcon icon3 = im.getIcon("download");
        addTab("Download", icon3, downloadController.getComponent());

        ImageIcon icon4 = im.getIcon("upload");
        addTab("Upload", icon4, uploadController.getComponent());

        ImageIcon icon5 = im.getIcon("server");
        addTab("Server", icon5, serverPanel);

        if (AppleJuiceClient.splash != null) {
            AppleJuiceClient.splash.setProgress(90, "Lade Plugins...");
        }
        if (OptionsManagerImpl.getInstance().shouldLoadPluginsOnStartup()) {
            loadPlugins();
        }
    }

    private void loadPlugins() {
        String path;
        if (System.getProperty("os.name").toLowerCase().indexOf("windows")==-1) {
            path = System.getProperty("user.home") + File.separator +
                "appleJuice" + File.separator +
                "gui" + File.separator + "plugins" + File.separator;
        }
        else {
            path = System.getProperty("user.dir") + File.separator +
                "plugins" + File.separator;
        }
        File delFile = new File(path + "ajIrcPlugin_1_3.jar");
        if (delFile.isFile()){
            delFile.delete();
        }
        delFile = new File(path + "ajIrcPlugin_1_31.jar");
        if (delFile.isFile()) {
            delFile.delete();
        }
        delFile = new File(path + "IrcPlugin_1_31.jar");
        if (delFile.isFile()) {
            delFile.delete();
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
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
        }
    }
}
