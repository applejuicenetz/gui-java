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

  public RegisterPanel(){
    init();
  }

  private void init() {
    try {
      LanguageSelector.getInstance().addLanguageListener(this);
    }
    catch (LanguageSelectorNotInstanciatedException ex) {
    }
    startPanel = new StartPanel();
    downloadPanel = new DownloadPanel();
    uploadPanel = new UploadPanel();
    searchPanel = new SearchPanel();
    serverPanel = new ServerPanel();
    URL url = getClass().getResource("start.GIF");
    Image img=Toolkit.getDefaultToolkit().getImage(url);
    ImageIcon icon = new ImageIcon();
    icon.setImage(img);
    addTab("Start", icon, startPanel);

    url = getClass().getResource("suchen.gif");
    img = Toolkit.getDefaultToolkit().getImage(url);
    ImageIcon icon2 = new ImageIcon();
    icon2.setImage(img);
    addTab("Suchen", icon2, searchPanel);


    url = getClass().getResource("download.GIF");
    img = Toolkit.getDefaultToolkit().getImage(url);
    ImageIcon icon3 = new ImageIcon();
    icon3.setImage(img);
    addTab("Download", icon3, downloadPanel);

    url = getClass().getResource("upload.gif");
    img = Toolkit.getDefaultToolkit().getImage(url);
    ImageIcon icon4 = new ImageIcon();
    icon4.setImage(img);
    addTab("Upload", icon4, uploadPanel);

    url = getClass().getResource("server.gif");
    img = Toolkit.getDefaultToolkit().getImage(url);
    ImageIcon icon5 = new ImageIcon();
    icon5.setImage(img);
    addTab("Server", icon5, serverPanel);
  }

  public void fireLanguageChanged(){
      try {
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        setTitleAt(0, ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName("mainform", "homesheet", "caption")));
        setTitleAt(1, ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName("mainform", "seachsheet", "caption")));
        setTitleAt(2, ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName("mainform", "queuesheet", "caption")));
        setTitleAt(3, ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName("mainform", "uploadsheet", "caption")));
        setTitleAt(4, ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName("mainform", "serversheet", "caption")));
      }
      catch (LanguageSelectorNotInstanciatedException ex) {
        ex.printStackTrace();
      }
  }
}