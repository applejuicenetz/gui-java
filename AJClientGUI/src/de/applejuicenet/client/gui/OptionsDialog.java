package de.applejuicenet.client.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import de.applejuicenet.client.gui.controller.*;
import de.applejuicenet.client.shared.*;
import de.applejuicenet.client.shared.exception.*;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class OptionsDialog
    extends JDialog {
  private JTabbedPane jTabbedPane1 = new JTabbedPane();
  private ODPluginPanel pluginPanel;
  private ODStandardPanel standardPanel;
  private ODPasswortPanel passwortPanel;
  private ODVerbindungPanel verbindungPanel;
  private ODRemotePanel remotePanel;
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
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    LanguageSelector languageSelector = LanguageSelector.getInstance();
    setTitle(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                               getFirstAttrbuteByTagName(new
        String[] {"einstform", "caption"})));
    standardPanel = new ODStandardPanel(parent); //Standard-Reiter
    jTabbedPane1.add(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"einstform", "standardsheet",
                                  "caption"})), standardPanel);
    passwortPanel = new ODPasswortPanel(); //Passwort-Reiter
    jTabbedPane1.add(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"einstform", "pwsheet",
                                  "caption"})), passwortPanel);
    verbindungPanel = new ODVerbindungPanel(); //Verbindungs-Reiter
    jTabbedPane1.add(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"einstform", "connectionsheet",
                                  "caption"})), verbindungPanel);
    remotePanel = new ODRemotePanel(); //Fernzugriff-Reiter
    jTabbedPane1.add(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"javagui", "options", "remote",
                                  "caption"})), remotePanel);
    proxyPanel = new ODProxyPanel(); //Proxy-Reiter
    jTabbedPane1.add(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"javagui", "options", "proxy",
                                  "caption"})), proxyPanel);
    pluginPanel = new ODPluginPanel(parent); //Plugin-Reiter
    jTabbedPane1.add(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"einstform", "TabSheet1",
                                  "caption"})), pluginPanel);
    getContentPane().add(jTabbedPane1, BorderLayout.CENTER);
    speichern = new JButton(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"einstform", "Button1",
                                  "caption"})));
    abbrechen = new JButton(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"einstform", "Button2",
                                  "caption"})));
    abbrechen.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
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
    OptionsManager om = OptionsManager.getInstance();
    om.saveProxy(proxyPanel.getProxy());
    if (remotePanel.isDirty()) {
      try {
        om.saveRemote(remotePanel.getRemoteConfiguration());
      }
      catch (InvalidPasswordException ex) {
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        String titel = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"javagui", "eingabefehler"}));
        String nachricht = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"javagui", "options", "remote", "fehlertext"}));
        JOptionPane.showMessageDialog(parent, nachricht, titel, JOptionPane.OK_OPTION);
      }
    }
    dispose();
  }
}