package de.applejuice.client.gui;

import javax.swing.JTabbedPane;

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
    addTab("Start", startPanel);
    addTab("Download", downloadPanel);
  }
}