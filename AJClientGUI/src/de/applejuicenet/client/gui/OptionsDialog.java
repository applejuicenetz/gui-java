package de.applejuicenet.client.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import de.applejuicenet.client.gui.controller.*;
import de.applejuicenet.client.shared.*;
import de.applejuicenet.client.shared.exception.*;
import org.apache.log4j.Level;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/OptionsDialog.java,v 1.15 2003/08/16 17:49:56 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: OptionsDialog.java,v $
 * Revision 1.15  2003/08/16 17:49:56  maj0r
 * Diverse Farben können nun manuell eingestellt bzw. deaktiviert werden.
 * DownloaduebersichtTabelle kann deaktiviert werden.
 *
 * Revision 1.14  2003/08/15 18:31:36  maj0r
 * Farbdialog in Optionen eingebaut.
 *
 * Revision 1.13  2003/08/15 14:46:30  maj0r
 * Refactoring.
 *
 * Revision 1.12  2003/08/02 12:03:38  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.11  2003/06/24 12:06:49  maj0r
 * log4j eingefügt (inkl. Bedienung über Einstellungsdialog).
 *
 * Revision 1.10  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class OptionsDialog
    extends JDialog {
  private JTabbedPane jTabbedPane1 = new JTabbedPane();
  private ODPluginPanel pluginPanel;
  private ODStandardPanel standardPanel;
  private ODVerbindungPanel verbindungPanel;
  private ODRemotePanel remotePanel;
  private ODAnsichtPanel ansichtPanel;
  private JFrame parent;
  private JButton speichern;
  private JButton abbrechen;
  private AJSettings ajSettings;

  public OptionsDialog(JFrame parent) throws HeadlessException {
    super(parent, true);
    this.parent = parent;
    try {
      ajSettings = ApplejuiceFassade.getInstance().getAJSettings();
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
    standardPanel = new ODStandardPanel(parent, ajSettings); //Standard-Reiter
    jTabbedPane1.add(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"einstform", "standardsheet",
                                  "caption"})), standardPanel);
    verbindungPanel = new ODVerbindungPanel(ajSettings); //Verbindungs-Reiter
    jTabbedPane1.add(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"einstform", "connectionsheet",
                                  "caption"})), verbindungPanel);
    remotePanel = new ODRemotePanel(); //Fernzugriff-Reiter
    jTabbedPane1.add(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"einstform", "pwsheet",
                                  "caption"})), remotePanel);
    ansichtPanel = new ODAnsichtPanel();
    jTabbedPane1.add("Ansicht", ansichtPanel);
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
    ansichtPanel.save();
    if (standardPanel.isDirty() || verbindungPanel.isDirty()) {
      om.saveAJSettings(ajSettings);
      if (standardPanel.isDirty())
        om.setLogLevel(standardPanel.getLogLevel());
    }
    if (remotePanel.isDirty()) {
      try {
        om.saveRemote(remotePanel.getRemoteConfiguration());
      }
      catch (InvalidPasswordException ex) {
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        String titel = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(new String[] {"javagui", "eingabefehler"}));
        String nachricht = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(new String[] {"javagui", "options",
                                      "remote", "fehlertext"}));
        JOptionPane.showMessageDialog(parent, nachricht, titel,
                                      JOptionPane.OK_OPTION);
      }
    }
    dispose();
  }
}