package de.applejuicenet.client.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import de.applejuicenet.client.gui.controller.*;
import de.applejuicenet.client.shared.*;
import de.applejuicenet.client.shared.exception.*;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/OptionsDialog.java,v 1.23 2003/10/31 16:24:58 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: OptionsDialog.java,v $
 * Revision 1.23  2003/10/31 16:24:58  maj0r
 * Soundeffekte fuer diverse Ereignisse eingefuegt.
 *
 * Revision 1.22  2003/10/14 15:43:52  maj0r
 * An pflegbaren Xml-Port angepasst.
 *
 * Revision 1.21  2003/09/12 13:19:26  maj0r
 * Proxy eingebaut, so dass nun immer Infos angezeigt werden koennen.
 * Version 0.30
 *
 * Revision 1.20  2003/09/09 12:28:15  maj0r
 * Wizard fertiggestellt.
 *
 * Revision 1.19  2003/09/04 10:13:28  maj0r
 * Logger eingebaut.
 *
 * Revision 1.18  2003/08/25 18:02:10  maj0r
 * Sprachberuecksichtigung und Tooltipps eingebaut.
 *
 * Revision 1.17  2003/08/24 14:59:59  maj0r
 * Version 0.14
 * Diverse Aenderungen.
 *
 * Revision 1.16  2003/08/22 10:54:25  maj0r
 * Klassen umbenannt.
 * ConnectionSettings ueberarbeitet.
 *
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
    private ODConnectionPanel remotePanel;
    private ODAnsichtPanel ansichtPanel;
    private ODProxyPanel proxyPanel;
    private JFrame parent;
    private JButton speichern;
    private JButton abbrechen;
    private AJSettings ajSettings;
    private Logger logger;
    private ConnectionSettings remote;

    public OptionsDialog(JFrame parent) throws HeadlessException {
        super(parent, true);
        logger = Logger.getLogger(getClass());
        try
        {
            this.parent = parent;
            ajSettings = ApplejuiceFassade.getInstance().getAJSettings();
            init();
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    private void init() throws Exception {
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        remote = PropertiesManager.getOptionsManager().getRemoteSettings();

        setTitle(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                   getFirstAttrbuteByTagName(new
                                                           String[]{"einstform", "caption"})));
        standardPanel = new ODStandardPanel(this, ajSettings, remote); //Standard-Reiter
        jTabbedPane1.add(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                           getFirstAttrbuteByTagName(new String[]{"einstform", "standardsheet",
                                                                                                  "caption"})), standardPanel);
        verbindungPanel = new ODVerbindungPanel(ajSettings); //Verbindungs-Reiter
        jTabbedPane1.add(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                           getFirstAttrbuteByTagName(new String[]{"einstform", "connectionsheet",
                                                                                                  "caption"})), verbindungPanel);
        remotePanel = new ODConnectionPanel(remote); //Fernzugriff-Reiter
        jTabbedPane1.add(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                           getFirstAttrbuteByTagName(new String[]{"einstform", "pwsheet",
                                                                                                  "caption"})), remotePanel);
        proxyPanel = new ODProxyPanel(); //Proxy-Reiter
        jTabbedPane1.add(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                           getFirstAttrbuteByTagName(new String[]{"javagui", "options", "proxy",
                                                                                                  "caption"})), proxyPanel);
        ansichtPanel = new ODAnsichtPanel();
        jTabbedPane1.add(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                           getFirstAttrbuteByTagName(new String[]{"javagui", "options", "ansicht",
                                                                                                  "caption"})), ansichtPanel);
        pluginPanel = new ODPluginPanel(parent); //Plugin-Reiter
        jTabbedPane1.add(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                           getFirstAttrbuteByTagName(new String[]{"einstform", "TabSheet1",
                                                                                                  "caption"})), pluginPanel);
        getContentPane().add(jTabbedPane1, BorderLayout.CENTER);
        speichern = new JButton(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                                  getFirstAttrbuteByTagName(new String[]{"einstform", "Button1",
                                                                                                         "caption"})));
        abbrechen = new JButton(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                                  getFirstAttrbuteByTagName(new String[]{"einstform", "Button2",
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
        try{
            OptionsManager om = PropertiesManager.getOptionsManager();
            boolean etwasGeaendert;
            etwasGeaendert = ansichtPanel.save();
            if (standardPanel.isDirty() || verbindungPanel.isDirty())
            {
                om.saveAJSettings(ajSettings);
                if (standardPanel.isDirty())
                    om.setLogLevel(standardPanel.getLogLevel());
                etwasGeaendert = true;
            }
            if (remotePanel.isDirty() || standardPanel.isXmlPortDirty())
            {
                try
                {
                    om.saveRemote(remote);
                    etwasGeaendert = true;
                }
                catch (InvalidPasswordException ex)
                {
                    LanguageSelector languageSelector = LanguageSelector.getInstance();
                    String titel = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                                     getFirstAttrbuteByTagName(new String[]{"javagui", "eingabefehler"}));
                    String nachricht = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                                         getFirstAttrbuteByTagName(new String[]{"javagui", "options",
                                                                                                                "remote", "fehlertext"}));
                    JOptionPane.showMessageDialog(parent, nachricht, titel,
                                                  JOptionPane.OK_OPTION);
                }
            }
            if (proxyPanel.isDirty())
            {
                PropertiesManager.getProxyManager().saveProxySettings(proxyPanel.getProxySettings());
                etwasGeaendert = true;
            }
            if (etwasGeaendert){
                SoundPlayer.getInstance().playSound(SoundPlayer.GESPEICHERT);
            }
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
        dispose();
    }
}