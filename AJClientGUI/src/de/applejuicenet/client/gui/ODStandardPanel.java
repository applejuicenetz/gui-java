package de.applejuicenet.client.gui;

import java.io.File;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolTip;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.OptionsManager;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.shared.AJSettings;
import de.applejuicenet.client.shared.ConnectionSettings;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.MultiLineToolTip;
import de.applejuicenet.client.shared.NumberInputVerifier;
import de.applejuicenet.client.shared.ZeichenErsetzer;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/ODStandardPanel.java,v 1.37 2004/10/14 14:55:07 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class ODStandardPanel
    extends JPanel
    implements OptionsRegister {
	
    private static final long serialVersionUID = 565406484667301350L;
    
	private boolean dirty = false;
    private boolean xmlPortDirty = false;
    private JLabel label1 = new JLabel();
    private JLabel label2 = new JLabel();
    private JLabel label3 = new JLabel();
    private JLabel label4 = new JLabel();
    private JLabel label6 = new JLabel();
    private JLabel label7 = new JLabel();
    private JLabel selectStandardBrowser;
    private JLabel openTemp;
    private JLabel openIncoming;
    private JTextField temp = new JTextField();
    private JTextField incoming = new JTextField();
    private JTextField port = new JTextField();
    private JTextField xmlPort = new JTextField();
    private JTextField nick = new JTextField();
    private JTextField browser = new JTextField();
    private JLabel hint1;
    private JLabel hint2;
    private JLabel hint3;
    private JLabel hint4;
    private JLabel hint5;
    private JLabel hint6;
    private JDialog parent;
    private AJSettings ajSettings;
    private JComboBox cmbLog;
    private JComboBox updateInfoModus;
    private JCheckBox loadPlugins = new JCheckBox();
    private Logger logger;
    private ConnectionSettings remote;
    private Icon menuIcon;
    private String menuText;

    public ODStandardPanel(JDialog parent, AJSettings ajSettings,
                           ConnectionSettings remote) {
        logger = Logger.getLogger(getClass());
        try {
            this.remote = remote;
            this.parent = parent;
            this.ajSettings = ajSettings;
            init();
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
        }
    }

    public boolean isDirty() {
        return dirty;
    }

    public Level getLogLevel() {
        return ( (LevelItem) cmbLog.getSelectedItem()).getLevel();
    }

    public int getVersionsinfoModus() {
        if (updateInfoModus.getSelectedIndex() == -1) {
            return 1;
        }
        else {
            UpdateInfoItem selectedItem = (UpdateInfoItem) updateInfoModus.
                getSelectedItem();
            return selectedItem.getModus();
        }
    }

    public String getBrowserPfad() {
        return browser.getText();
    }

    public boolean shouldLoadPluginsOnStartup() {
        return loadPlugins.isSelected();
    }

    private void init() throws Exception {
        OptionsManager optionsManager = OptionsManagerImpl.getInstance();
        IconManager im = IconManager.getInstance();
        menuIcon = im.getIcon("opt_standard");
        port.setDocument(new NumberInputVerifier());
        xmlPort.setDocument(new NumberInputVerifier());
        temp.setEditable(false);
        temp.setBackground(Color.WHITE);
        incoming.setEditable(false);
        incoming.setBackground(Color.WHITE);
        browser.setEditable(false);
        browser.setBackground(Color.WHITE);
        browser.setText(optionsManager.getStandardBrowser());
        port.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                int portNr = Integer.parseInt(port.getText());
                if (ajSettings.getPort() != portNr) {
                    if (portNr > 1024 && portNr <= 32000) {
                        dirty = true;
                        ajSettings.setPort(Integer.parseInt(port.getText()));
                    }
                    else {
                        port.setText(Long.toString(ajSettings.getPort()));
                    }
                }
            }
        });
        xmlPort.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                int xmlPortNr = Integer.parseInt(xmlPort.getText());
                if (ajSettings.getXMLPort() != Long.parseLong(xmlPort.getText())) {
                    if (xmlPortNr > 1024 && xmlPortNr <= 32000) {
                        dirty = true;
                        xmlPortDirty = true;
                        remote.setXmlPort(Integer.parseInt(xmlPort.getText()));
                        ajSettings.setXMLPort(Long.parseLong(xmlPort.getText()));
                    }
                    else {
                        xmlPort.setText(Long.toString(ajSettings.getXMLPort()));
                    }
                }
            }
        });
        nick.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                if (ajSettings.getNick().compareTo(nick.getText()) != 0) {
                    dirty = true;
                    ajSettings.setNick(nick.getText());
                }
            }
        });

        JPanel panel8 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel8.add(new JLabel("Logging: "));
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        Level logLevel = optionsManager.getLogLevel();

        LevelItem[] levelItems = new LevelItem[3]; //{ "Info", "Debug", "keins"};
        levelItems[0] = new LevelItem(Level.INFO,
                                      ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.getFirstAttrbuteByTagName(".root.javagui.options.logging.info")));
        levelItems[1] = new LevelItem(Level.DEBUG,
                                      ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.getFirstAttrbuteByTagName(".root.javagui.options.logging.debug")));
        levelItems[2] = new LevelItem(Level.OFF,
                                      ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.getFirstAttrbuteByTagName(".root.javagui.options.logging.off")));
        menuText = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.einstform.standardsheet.caption"));
        cmbLog = new JComboBox(levelItems);
        cmbLog.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                dirty = true;
            }
        });

        int index = 0;
        if (logLevel == Level.INFO) {
            index = 0;
        }
        else if (logLevel == Level.DEBUG) {
            index = 1;
        }
        else if (logLevel == Level.OFF) {
            index = 2;
        }
        cmbLog.setSelectedIndex(index);

        panel8.add(cmbLog);

        updateInfoModus = new JComboBox();
        UpdateInfoItem item0 = new UpdateInfoItem(0,
                                                  ZeichenErsetzer.
                                                  korrigiereUmlaute(
            languageSelector.getFirstAttrbuteByTagName(".root.javagui.options.standard.updateinfo0")));
        UpdateInfoItem item1 = new UpdateInfoItem(1,
                                                  ZeichenErsetzer.
                                                  korrigiereUmlaute(
            languageSelector.getFirstAttrbuteByTagName(".root.javagui.options.standard.updateinfo1")));
        UpdateInfoItem item2 = new UpdateInfoItem(2,
                                                  ZeichenErsetzer.
                                                  korrigiereUmlaute(
            languageSelector.getFirstAttrbuteByTagName(".root.javagui.options.standard.updateinfo2")));
        updateInfoModus.addItem(item0);
        updateInfoModus.addItem(item1);
        updateInfoModus.addItem(item2);
        int infoModus = optionsManager.getVersionsinfoModus();
        switch (infoModus) {
            case 0: {
                updateInfoModus.setSelectedItem(item0);
                break;
            }
            case 1: {
                updateInfoModus.setSelectedItem(item1);
                break;
            }
            case 2: {
                updateInfoModus.setSelectedItem(item2);
                break;
            }
            default: {
                updateInfoModus.setSelectedIndex( -1);
            }
        }
        updateInfoModus.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                dirty = true;
            }
        });
        JPanel panel9 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel label10 = new JLabel(ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.getFirstAttrbuteByTagName(".root.javagui.options.standard.updateinfotext")));
        panel9.add(label10);
        panel9.add(updateInfoModus);

        setLayout(new BorderLayout());
        port.setHorizontalAlignment(JLabel.RIGHT);
        xmlPort.setHorizontalAlignment(JLabel.RIGHT);
        reloadSettings();
        JPanel panel6 = new JPanel(new GridBagLayout());
        label1.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.einstform.Label2.caption")));
        label2.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.einstform.Label7.caption")));
        label3.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.einstform.Label3.caption")));
        label4.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.einstform.Label8.caption")));
        label6.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.options.standard.xmlport")));
        label7.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.options.standard.standardbrowser")));
        loadPlugins.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.options.standard.ladeplugins")));

        loadPlugins.setSelected(optionsManager.shouldLoadPluginsOnStartup());

        ImageIcon icon = im.getIcon("hint");
        hint1 = new JLabel(icon) {
            private static final long serialVersionUID = -2622145586136954136L;

			public JToolTip createToolTip() {
                MultiLineToolTip tip = new MultiLineToolTip();
                tip.setComponent(this);
                return tip;
            }
        };
        hint2 = new JLabel(icon) {
            private static final long serialVersionUID = -3253769321269579030L;

			public JToolTip createToolTip() {
                MultiLineToolTip tip = new MultiLineToolTip();
                tip.setComponent(this);
                return tip;
            }
        };
        hint3 = new JLabel(icon) {
            private static final long serialVersionUID = -2048500163182191117L;

			public JToolTip createToolTip() {
                MultiLineToolTip tip = new MultiLineToolTip();
                tip.setComponent(this);
                return tip;
            }
        };
        hint4 = new JLabel(icon) {
            private static final long serialVersionUID = -3799359788295967464L;

			public JToolTip createToolTip() {
                MultiLineToolTip tip = new MultiLineToolTip();
                tip.setComponent(this);
                return tip;
            }
        };
        hint5 = new JLabel(icon) {
            private static final long serialVersionUID = 8709140576027778947L;

			public JToolTip createToolTip() {
                MultiLineToolTip tip = new MultiLineToolTip();
                tip.setComponent(this);
                return tip;
            }
        };
        hint6 = new JLabel(icon) {
            private static final long serialVersionUID = 7619548238121352978L;

			public JToolTip createToolTip() {
                MultiLineToolTip tip = new MultiLineToolTip();
                tip.setComponent(this);
                return tip;
            }
        };
        hint1.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.options.standard.ttipp_temp")));
        hint2.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.options.standard.ttipp_port")));
        hint3.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.options.standard.ttipp_nick")));
        hint4.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.einstform.Label1.caption")));
        hint5.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.options.logging.ttip")));
        hint6.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.options.standard.ttipp_xmlport")));

        Icon icon2 = im.getIcon("folderopen");
        DirectoryChooserMouseAdapter dcMouseAdapter = new
            DirectoryChooserMouseAdapter();
        openTemp = new JLabel(icon2);
        openTemp.addMouseListener(dcMouseAdapter);
        openIncoming = new JLabel(icon2);
        openIncoming.addMouseListener(dcMouseAdapter);
        selectStandardBrowser = new JLabel(icon2);
        selectStandardBrowser.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                JLabel source = (JLabel) e.getSource();
                source.setBorder(BorderFactory.createLineBorder(Color.black));
            }

            public void mouseClicked(MouseEvent e) {
                JLabel source = (JLabel) e.getSource();
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogType(JFileChooser.FILES_ONLY);
                fileChooser.setDialogTitle(label7.getText());
                if (browser.getText().length() != 0) {
                    File tmpFile = new File(browser.getText());
                    if (tmpFile.isFile()) {
                        fileChooser.setCurrentDirectory(tmpFile);
                    }
                }
                int returnVal = fileChooser.showOpenDialog(source);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File browserFile = fileChooser.getSelectedFile();
                    if (browserFile.isFile()) {
                        browser.setText(browserFile.getPath());
                        dirty = true;
                    }
                }
            }

            public void mouseExited(MouseEvent e) {
                JLabel source = (JLabel) e.getSource();
                source.setBorder(null);
            }
        });

        loadPlugins.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                dirty = true;
            }
        });

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets.top = 5;

        JPanel panel1 = new JPanel(new GridBagLayout());
        JPanel panel2 = new JPanel(new GridBagLayout());
        JPanel panel3 = new JPanel(new GridBagLayout());
        JPanel panel4 = new JPanel(new GridBagLayout());
        JPanel panel7 = new JPanel(new GridBagLayout());
        JPanel panel10 = new JPanel(new GridBagLayout());
        JPanel panel11 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel11.add(loadPlugins);

        constraints.insets.right = 5;
        constraints.insets.left = 4;
        panel1.add(label1, constraints);
        panel2.add(label2, constraints);
        panel3.add(label3, constraints);
        panel4.add(label4, constraints);
        panel7.add(label6, constraints);
        panel10.add(label7, constraints);

        constraints.insets.left = 0;
        constraints.insets.right = 2;
        constraints.gridx = 1;
        constraints.weightx = 1;
        panel1.add(temp, constraints);
        panel2.add(incoming, constraints);
        panel3.add(port, constraints);
        panel7.add(xmlPort, constraints);
        panel4.add(nick, constraints);
        panel10.add(browser, constraints);
        constraints.gridx = 2;
        constraints.weightx = 0;
        panel1.add(openTemp, constraints);
        panel2.add(openIncoming, constraints);
        panel10.add(selectStandardBrowser, constraints);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        panel6.add(panel1, constraints);
        constraints.gridy = 1;
        panel6.add(panel2, constraints);
        constraints.gridy = 2;
        panel6.add(panel3, constraints);
        constraints.gridy = 3;
        panel6.add(panel7, constraints);
        constraints.gridy = 4;
        panel6.add(panel4, constraints);
        constraints.gridy = 5;
        panel6.add(panel10, constraints);

        constraints.insets.top = 10;
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.weightx = 0;
        panel6.add(hint1, constraints);
        constraints.gridy = 2;
        panel6.add(hint2, constraints);
        constraints.gridy = 3;
        panel6.add(hint6, constraints);
        constraints.gridy = 4;
        panel6.add(hint3, constraints);

        constraints.gridy = 6;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        panel6.add(panel8, constraints);
        constraints.gridx = 1;
        panel6.add(hint5, constraints);
        constraints.gridy = 7;
        constraints.gridx = 0;
        panel6.add(panel9, constraints);
        constraints.gridy = 8;
        constraints.gridx = 0;
        panel6.add(panel11, constraints);

        add(panel6, BorderLayout.NORTH);
    }

    public boolean isXmlPortDirty() {
        return xmlPortDirty;
    }

    public Icon getIcon() {
        return menuIcon;
    }

    public String getMenuText() {
        return menuText;
    }

    public void reloadSettings() {
        AJSettings ajSettings2 = ApplejuiceFassade.getInstance().getAJSettings();
        temp.setText(ajSettings2.getTempDir());
        incoming.setText(ajSettings2.getIncomingDir());
        port.setText(Long.toString(ajSettings2.getPort()));
        xmlPort.setText(Long.toString(ajSettings2.getXMLPort()));
        nick.setText(ajSettings2.getNick());
    }

    class DirectoryChooserMouseAdapter
        extends MouseAdapter {
        public void mouseEntered(MouseEvent e) {
            JLabel source = (JLabel) e.getSource();
            source.setBorder(BorderFactory.createLineBorder(Color.black));
        }

        public void mouseClicked(MouseEvent e) {
            JLabel source = (JLabel) e.getSource();
            String title = "";
            if (source == openTemp) {
                title = label1.getText();
            }
            else {
                title = label2.getText();
            }
            ODDirectoryChooser chooser = new ODDirectoryChooser(parent, title);
            chooser.setLocation(parent.getLocation());
            chooser.setVisible(true);
            if (chooser.isNewPathSelected()) {
                dirty = true;
                String path = chooser.getSelectedPath();
                if (source == openTemp) {
                    temp.setText(path);
                    ajSettings.setTempDir(path);
                }
                else {
                    incoming.setText(path);
                    ajSettings.setIncomingDir(path);
                }
            }
        }

        public void mouseExited(MouseEvent e) {
            JLabel source = (JLabel) e.getSource();
            source.setBorder(null);
        }
    }

    class LevelItem {
        private Level level;
        private String bezeichnung;

        public LevelItem(Level level, String bezeichnung) {
            this.level = level;
            this.bezeichnung = bezeichnung;
        }

        public Level getLevel() {
            return level;
        }

        public String toString() {
            return bezeichnung;
        }
    }

    class UpdateInfoItem {
        private int modus;
        private String bezeichnung;

        public UpdateInfoItem(int modus, String bezeichnung) {
            this.modus = modus;
            this.bezeichnung = bezeichnung;
        }

        public int getModus() {
            return modus;
        }

        public String toString() {
            return bezeichnung;
        }
    }
}
