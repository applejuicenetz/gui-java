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
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.gui.controller.ProxyManagerImpl;
import de.applejuicenet.client.shared.AJSettings;
import de.applejuicenet.client.shared.ConnectionSettings;
import de.applejuicenet.client.shared.SoundPlayer;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.shared.exception.InvalidPasswordException;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/OptionsDialog.java,v 1.41 2004/03/09 16:50:27 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
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
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
        }
    }

    private void init() throws Exception {
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        remote = OptionsManagerImpl.getInstance().getRemoteSettings();

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
            OptionsManager om = OptionsManagerImpl.getInstance();
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
                ProxyManagerImpl.getInstance().saveProxySettings( ( (
                    ODProxyPanel) optionPanels[3]).getProxySettings());
                etwasGeaendert = true;
            }
            if ( ( (ODAnsichtPanel) optionPanels[4]).isDirty()) {
                OptionsManagerImpl.getInstance().
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
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
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
