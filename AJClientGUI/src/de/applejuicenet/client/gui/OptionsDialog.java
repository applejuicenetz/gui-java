package de.applejuicenet.client.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.OptionsManager;
import de.applejuicenet.client.gui.controller.PropertiesManager;
import de.applejuicenet.client.shared.AJSettings;
import de.applejuicenet.client.shared.ConnectionSettings;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.SoundPlayer;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.shared.exception.InvalidPasswordException;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/OptionsDialog.java,v 1.37 2004/02/21 18:20:30 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: OptionsDialog.java,v $
 * Revision 1.37  2004/02/21 18:20:30  maj0r
 * LanguageSelector auf SAX umgebaut.
 *
 * Revision 1.36  2004/02/05 23:11:27  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.35  2004/01/29 15:52:33  maj0r
 * Bug #153 umgesetzt (Danke an jr17)
 * Verbindungsdialog kann nun per Option beim naechsten GUI-Start erzwungen werden.
 *
 * Revision 1.34  2004/01/27 07:12:04  maj0r
 * Muell entfernt.
 *
 * Revision 1.33  2004/01/26 19:38:36  maj0r
 * Menueleiste ueberarbeitet.
 *
 * Revision 1.32  2004/01/25 10:16:42  maj0r
 * Optionenmenue ueberarbeitet.
 *
 * Revision 1.31  2004/01/21 14:29:05  maj0r
 * Icons eingebaut.
 *
 * Revision 1.30  2004/01/05 19:17:18  maj0r
 * Bug #56 gefixt (Danke an MeineR)
 * Das Laden der Plugins beim Start kann über das Optionenmenue deaktiviert werden.
 *
 * Revision 1.29  2004/01/05 11:54:21  maj0r
 * Standardbrowser kann nun definiert werden.
 *
 * Revision 1.28  2004/01/01 15:30:21  maj0r
 * Plugins koennen nun ein JPanel zB fuer Optionen implementieren.
 * Dieses wird dann im Optionendialog angezeigt.
 *
 * Revision 1.27  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.26  2003/12/29 15:20:05  maj0r
 * Neue Versionupdatebenachrichtigung fertiggestellt.
 *
 * Revision 1.25  2003/12/29 09:39:21  maj0r
 * Alte BugIDs entfernt, da auf neuen Bugtracker auf bugs.applejuicenet.de umgestiegen wurde.
 *
 * Revision 1.24  2003/12/27 19:06:33  maj0r
 * Im Verbindungsfenster geht nun ein einfaches <Enter> (Danke an muhviestarr).
 *
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
    private JFrame parent;
    private JButton speichern;
    private JButton abbrechen;
    private AJSettings ajSettings;
    private Logger logger;
    private ConnectionSettings remote;
    private JList menuList;
    private OptionsRegister[] optionPanels;
    private CardLayout registerLayout = new CardLayout();
    private JPanel registerPanel = new JPanel(registerLayout);

    public OptionsDialog(JFrame parent) throws HeadlessException {
        super(parent, true);
        logger = Logger.getLogger(getClass());
        try {
            this.parent = parent;
            ajSettings = ApplejuiceFassade.getInstance().getAJSettings();
            init();
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    private void init() throws Exception {
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        remote = PropertiesManager.getOptionsManager().getRemoteSettings();
        IconManager im = IconManager.getInstance();

        setTitle(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.einstform.caption")));
        optionPanels = new OptionsRegister[6];
        optionPanels[0] = new ODStandardPanel(this, ajSettings, remote);
        optionPanels[1] = new ODVerbindungPanel(ajSettings);
        optionPanels[2] = new ODConnectionPanel(remote, null);
        optionPanels[3] = new ODProxyPanel();
        optionPanels[4] = new ODAnsichtPanel();
        optionPanels[5] = new ODPluginPanel(this);

        menuList = new JList(optionPanels);
        menuList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        menuList.setCellRenderer(new MenuListCellRenderer());
        for (int i = 0; i < optionPanels.length; i++) {
            registerPanel.add(optionPanels[i].getMenuText(),
                              (JPanel) optionPanels[i]);
        }
        menuList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                Object selected = menuList.getSelectedValue();
                registerLayout.show(registerPanel,
                                    ( (OptionsRegister) selected).getMenuText());
            }
        });
        menuList.setSelectedValue(optionPanels[0], true);
        speichern = new JButton(ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.
            getFirstAttrbuteByTagName(".root.einstform.Button1.caption")));
        abbrechen = new JButton(ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.
            getFirstAttrbuteByTagName(".root.einstform.Button2.caption")));
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
        getContentPane().add(registerPanel, BorderLayout.CENTER);
        getContentPane().add(panel, BorderLayout.SOUTH);
        getContentPane().add(new JScrollPane(menuList), BorderLayout.WEST);
        pack();
    }

    private void speichern() {
        try {
            OptionsManager om = PropertiesManager.getOptionsManager();
            boolean etwasGeaendert;
            etwasGeaendert = ( (ODAnsichtPanel) optionPanels[4]).save();
            if ( ( (ODStandardPanel) optionPanels[0]).isDirty() ||
                ( (ODVerbindungPanel) optionPanels[1]).isDirty()) {
                om.saveAJSettings(ajSettings);
                om.setStandardBrowser( ( (ODStandardPanel) optionPanels[0]).
                                      getBrowserPfad());
                om.loadPluginsOnStartup( ( (ODStandardPanel) optionPanels[0]).
                                        shouldLoadPluginsOnStartup());
                if ( ( (ODStandardPanel) optionPanels[0]).isDirty()) {
                    om.setLogLevel( ( (ODStandardPanel) optionPanels[0]).
                                   getLogLevel());
                    om.setVersionsinfoModus( ( (ODStandardPanel) optionPanels[0]).
                                            getVersionsinfoModus());
                }
                etwasGeaendert = true;
            }
            if ( ( (ODConnectionPanel) optionPanels[2]).isDirty() ||
                ( (ODStandardPanel) optionPanels[0]).isXmlPortDirty()) {
                try {
                    om.saveRemote(remote);
                    etwasGeaendert = true;
                }
                catch (InvalidPasswordException ex) {
                    LanguageSelector languageSelector = LanguageSelector.
                        getInstance();
                    String titel = ZeichenErsetzer.korrigiereUmlaute(
                        languageSelector.
                        getFirstAttrbuteByTagName(".root.javagui.eingabefehler"));
                    String nachricht = ZeichenErsetzer.korrigiereUmlaute(
                        languageSelector.
                        getFirstAttrbuteByTagName(".root.javagui.options.remote.fehlertext"));
                    JOptionPane.showMessageDialog(parent, nachricht, titel,
                                                  JOptionPane.OK_OPTION);
                }
            }
            if ( ( (ODProxyPanel) optionPanels[3]).isDirty()) {
                PropertiesManager.getProxyManager().saveProxySettings( ( (
                    ODProxyPanel) optionPanels[3]).getProxySettings());
                etwasGeaendert = true;
            }
            if ( ( (ODAnsichtPanel) optionPanels[4]).isDirty()) {
                PropertiesManager.getOptionsManager().
                    showConnectionDialogOnStartup( ( (ODAnsichtPanel)
                    optionPanels[4]).shouldShowStartcreen());
                etwasGeaendert = true;
            }
            if (etwasGeaendert) {
                SoundPlayer.getInstance().playSound(SoundPlayer.GESPEICHERT);
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
        dispose();
    }

    class MenuListCellRenderer
        extends JLabel
        implements ListCellRenderer {

        public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
            setText( ( (OptionsRegister) value).getMenuText() + "   ");
            setIcon( ( (OptionsRegister) value).getIcon());
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            }
            else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setOpaque(true);
            return this;
        }

        public Dimension getPreferredSize() {
            Dimension size = super.getPreferredSize();
            return new Dimension(size.width, size.height * 2);
        }
    }
}
