package de.applejuicenet.client.gui;

import java.io.*;
import java.net.*;

import javax.swing.*;

import de.applejuicenet.client.gui.controller.*;
import de.applejuicenet.client.gui.listener.*;
import de.applejuicenet.client.gui.plugins.*;
import de.applejuicenet.client.shared.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/RegisterPanel.java,v 1.15 2003/06/13 15:07:30 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: RegisterPanel.java,v $
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

  public RegisterPanel(AppleJuiceDialog parent) {
    this.parent = parent;
    init();
  }

  private void init() {
    LanguageSelector.getInstance().addLanguageListener(this);
    startPanel = new StartPanel(parent);
    sharePanel = new SharePanel();
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
    String[] tempListe = pluginPath.list();
    for (int i = 0; i < tempListe.length; i++) {
      if (tempListe[i].indexOf(".jar") != -1) {
        URL url = null;
        try {
          url = new URL("file://" + path + tempListe[i]);
        }
        catch (MalformedURLException ex) {
          continue;
        }
        try {
          PluginJarClassLoader jarLoader = new PluginJarClassLoader(url);
          PluginConnector aPlugin = jarLoader.getPlugin();
          if (aPlugin != null) {
            if (aPlugin.istReiter()) {
              ImageIcon icon = aPlugin.getIcon();
              addTab(aPlugin.getTitle(), icon, aPlugin);
            }
            parent.addPluginToHashSet(aPlugin);
          }
        }
        catch (Exception e) {
          //Von einem Plugin lassen wir uns nicht beirren! ;-)
          e.printStackTrace();
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