package de.applejuicenet.client.gui;

import javax.swing.*;
import java.awt.*;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.shared.exception.*;
import java.awt.event.*;
import de.applejuicenet.client.gui.controller.OptionsManager;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class OptionsDialog extends JDialog{
  private JTabbedPane jTabbedPane1 = new JTabbedPane();
  private ODPluginPanel pluginPanel;
  private ODStandardPanel standardPanel;
  private ODPasswortPanel passwortPanel;
  private ODVerbindungPanel verbindungPanel;
  private ODProxyPanel proxyPanel;
  private JFrame parent;
  private JButton speichern;
  private JButton abbrechen;

  public OptionsDialog(JFrame parent) throws HeadlessException {
    super(parent, true);
    this.parent = parent;
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    LanguageSelector languageSelector = LanguageSelector.getInstance();
    setTitle(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"einstform", "caption"})));
    standardPanel = new ODStandardPanel(parent);  //Standard-Reiter
    jTabbedPane1.add(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"einstform", "standardsheet", "caption"})), standardPanel);
    passwortPanel = new ODPasswortPanel();  //Passwort-Reiter
    jTabbedPane1.add(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"einstform", "pwsheet", "caption"})), passwortPanel);
    verbindungPanel = new ODVerbindungPanel();  //Verbindungs-Reiter
    jTabbedPane1.add(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"einstform", "connectionsheet", "caption"})), verbindungPanel);
    proxyPanel = new ODProxyPanel();  //Proxy-Reiter
    jTabbedPane1.add(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"javagui", "options", "proxy", "caption"})), proxyPanel);
    pluginPanel = new ODPluginPanel(parent);  //Plugin-Reiter
    jTabbedPane1.add(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"einstform", "TabSheet1", "caption"})), pluginPanel);
    getContentPane().add(jTabbedPane1, BorderLayout.CENTER);
    speichern = new JButton(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"einstform", "Button1", "caption"})));
    abbrechen = new JButton(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"einstform", "Button2", "caption"})));
    abbrechen.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        dispose();
      }
    });
    speichern.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        speichern();
      }
    });
    JPanel panel = new JPanel();
    FlowLayout flowL = new FlowLayout();
    flowL.setAlignment(FlowLayout.RIGHT);
    panel.setLayout(flowL);
    panel.add(speichern);
    panel.add(abbrechen);
    getContentPane().add(panel, BorderLayout.SOUTH);
    pack();
  }

  private void speichern() {
    //Proxy lokal merken
    OptionsManager.getInstance().saveProxy(proxyPanel.getProxy());
    dispose();
  }
}