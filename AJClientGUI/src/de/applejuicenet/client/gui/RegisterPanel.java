package de.applejuicenet.client.gui;

import java.io.*;
import java.net.*;

import javax.swing.*;

import de.applejuicenet.client.gui.controller.*;
import de.applejuicenet.client.gui.listener.*;
import de.applejuicenet.client.gui.plugins.*;
import de.applejuicenet.client.shared.*;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/RegisterPanel.java,v 1.22 2003/08/27 16:44:42 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: RegisterPanel.java,v $
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
    this.parent = parent;
    init();
  }

  private void init() {
    logger = Logger.getLogger(getClass());
    LanguageSelector.getInstance().addLanguageListener(this);
    startPanel = new StartPanel(parent);
    sharePanel = new SharePanel(parent);
    downloadPanel = new DownloadPanel();
    uploadPanel = new UploadPanel();
    searchPanel = new SearchPanel();
    serverPanel = new ServerPanel();

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

    loadPlugins();
  }

  private void loadPlugins() {
    String path = System.getProperty("user.dir") + File.separator + "plugins" +
        File.separator;
    File pluginPath = new File(path);
    if (!pluginPath.isDirectory()){
      System.out.println("Warnung: Kein Verzeichnis 'plugins' vorhanden!");
      return;
    }
    String[] tempListe = pluginPath.list();
    PluginJarClassLoader jarLoader = null;
    for (int i = 0; i < tempListe.length; i++) {
      int pos = tempListe[i].indexOf(".jar");
      if (pos != -1) {
        URL url = null;
        try {
          url = new URL("file://" + path + tempListe[i]);
          String className = tempListe[i].substring(0, pos);
          jarLoader = new PluginJarClassLoader(url);
          PluginConnector aPlugin = jarLoader.getPlugin(path + tempListe[i]);
          if (aPlugin != null) {
            if (aPlugin.istReiter()) {
              ImageIcon icon = aPlugin.getIcon();
              addTab(aPlugin.getTitle(), icon, aPlugin);
            }
            parent.addPluginToHashSet(aPlugin);
          }
          String nachricht = "Plugin " + aPlugin.getTitle() + " geladen...";
          if (logger.isEnabledFor(Level.INFO))
              logger.info(nachricht);
          System.out.println(nachricht);
        }
        catch (Exception e) {
          //Von einem Plugin lassen wir uns nicht beirren! ;-)
          if (logger.isEnabledFor(Level.ERROR))
              logger.error("Ein Plugin konnte nicht instanziert werden", e);
          e.printStackTrace();
          continue;
        }
      }
    }
  }

  public void fireLanguageChanged() {
    LanguageSelector languageSelector = LanguageSelector.getInstance();
    setTitleAt(0,
               ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                 getFirstAttrbuteByTagName(new
        String[] {
        "mainform", "homesheet", "caption"})));
    setTitleAt(1,
               ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                 getFirstAttrbuteByTagName(new
        String[] {
        "mainform", "sharesheet", "caption"})));
    setTitleAt(2,
               ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                 getFirstAttrbuteByTagName(new
        String[] {
        "mainform", "seachsheet", "caption"})));
    setTitleAt(3,
               ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                 getFirstAttrbuteByTagName(new
        String[] {
        "mainform", "queuesheet", "caption"})));
    setTitleAt(4,
               ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                 getFirstAttrbuteByTagName(new
        String[] {
        "mainform", "uploadsheet", "caption"})));
    setTitleAt(5,
               ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                 getFirstAttrbuteByTagName(new
        String[] {
        "mainform", "serversheet", "caption"})));
  }
}