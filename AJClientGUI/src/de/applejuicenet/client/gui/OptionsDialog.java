package de.applejuicenet.client.gui;

import javax.swing.*;
import java.awt.*;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.shared.exception.*;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class OptionsDialog extends JDialog implements LanguageListener{
  private JTabbedPane jTabbedPane1 = new JTabbedPane();
  private ODPluginPanel pluginPanel;
  private JFrame parent;

  public OptionsDialog(JFrame parent) throws HeadlessException {
    super(parent, true);
    this.parent = parent;
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    try {
      LanguageSelector.getInstance().addLanguageListener(this);
    }
    catch (LanguageSelectorNotInstanciatedException ex) {
    }
  }

  private void jbInit() throws Exception {
    LanguageSelector languageSelector = LanguageSelector.getInstance();
    setTitle(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"einstform", "caption"})));
    pluginPanel = new ODPluginPanel(parent);
    jTabbedPane1.add("Plugins", pluginPanel);
    getContentPane().add(jTabbedPane1, BorderLayout.CENTER);
    pack();
  }

  public void fireLanguageChanged(){

  }
}