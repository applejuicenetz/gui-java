package de.applejuice.client.gui;

import javax.swing.JTabbedPane;
import java.net.URL;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.ImageIcon;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class RegisterPanel extends JTabbedPane {
  private StartPanel startPanel;
  private DownloadPanel downloadPanel;

  public RegisterPanel(){
    init();
  }

  private void init() {
    startPanel = new StartPanel();
    downloadPanel = new DownloadPanel();
    URL url = getClass().getResource("start.gif");
    Image img=Toolkit.getDefaultToolkit().getImage(url);
    ImageIcon icon = new ImageIcon();
    icon.setImage(img);
    addTab("Start", icon, startPanel);
    url = getClass().getResource("download.gif");
    img=Toolkit.getDefaultToolkit().getImage(url);
    ImageIcon icon2 = new ImageIcon();
    icon2.setImage(img);
    addTab("Download", icon2, downloadPanel);
  }
}