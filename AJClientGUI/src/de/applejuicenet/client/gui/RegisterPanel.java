package de.applejuicenet.client.gui;

import javax.swing.JTabbedPane;
import java.net.URL;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.shared.exception.LanguageSelectorNotInstanciatedException;
import java.io.File;
import java.net.MalformedURLException;
import de.applejuicenet.client.shared.PluginJarClassLoader;
import de.applejuicenet.client.gui.plugins.PluginConnector;
import de.applejuicenet.client.gui.controller.DataManager;
import de.applejuicenet.client.shared.IconManager;
import javax.swing.JFrame;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class RegisterPanel extends JTabbedPane implements LanguageListener{
  private StartPanel startPanel;
  private DownloadPanel downloadPanel;
  private SearchPanel searchPanel;
  private UploadPanel uploadPanel;
  private ServerPanel serverPanel;
  private AppleJuiceDialog parent;

  public RegisterPanel(AppleJuiceDialog parent){
    this.parent = parent;
    init();
  }

  private void init() {
    LanguageSelector.getInstance().addLanguageListener(this);
    startPanel = new StartPanel();
    downloadPanel = new DownloadPanel();
    uploadPanel = new UploadPanel();
    searchPanel = new SearchPanel();
    serverPanel = new ServerPanel();

    IconManager im = IconManager.getInstance();

    ImageIcon icon = im.getIcon("start");
    addTab("Start", icon, startPanel);

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

  private void loadPlugins(){
    String path = System.getProperty("user.dir") + File.separator + "plugins" + File.separator;
    File pluginPath = new File(path);
    String[] tempListe = pluginPath.list();
    for (int i = 0; i < tempListe.length; i++) {
      if (tempListe[i].indexOf(".jar")!=-1){
        URL url = null;
        try {
          url = new URL("file://" + path + tempListe[i]);
        }
        catch (MalformedURLException ex) {
          continue;
        }
        try{
          PluginJarClassLoader jarLoader = new PluginJarClassLoader(url);
          PluginConnector aPlugin = jarLoader.getPlugin();
          if (aPlugin != null) {
            if (aPlugin.istReiter()) {
              ImageIcon icon = aPlugin.getIcon();
              addTab(aPlugin.getTitle(), icon, aPlugin);
            }
            DataManager.getInstance().addDownloadListener(aPlugin);
            DataManager.getInstance().addServerListener(aPlugin);
            LanguageSelector.getInstance().addLanguageListener(aPlugin);
            parent.addPluginToHashSet(aPlugin);
          }
        }
        catch (Exception e){
          //Von einem Plugin lassen wir uns nicht beirren! ;-)
          e.printStackTrace();
        }
      }
    }
  }

  public void fireLanguageChanged(){
    LanguageSelector languageSelector = LanguageSelector.getInstance();
    setTitleAt(0, ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "homesheet", "caption"})));
    setTitleAt(1, ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "seachsheet", "caption"})));
    setTitleAt(2, ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "queuesheet", "caption"})));
    setTitleAt(3, ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "uploadsheet", "caption"})));
    setTitleAt(4, ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "serversheet", "caption"})));
  }
}