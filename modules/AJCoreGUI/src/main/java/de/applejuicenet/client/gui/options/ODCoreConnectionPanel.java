/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.options;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.facade.ApplejuiceFacade;
import de.applejuicenet.client.facade.shared.AJSettings;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.NumberInputVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * Copyright: General Public License
 */
public class ODCoreConnectionPanel extends JPanel implements OptionsRegister {
    private boolean dirty = false;
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;
    private JLabel label5;
    private JLabel label6;
    private JLabel kbSlot;
    private JCheckBox automaticConnect;
    private JTextField maxSourcesPerFile = new JTextField();
    private JTextField maxConnections = new JTextField();
    private SpeedInputField maxUpload = new SpeedInputField("upload");
    private SpeedInputField maxDownload = new SpeedInputField("download");
    private JSlider kbSlider;
    private JTextField maxConnectionsPerTurn = new JTextField();
    private AJSettings ajSettings;
    private Icon menuIcon;
    private String menuText;
    private JButton wizard = new JButton();
    private OptionsDialog parent;

    public ODCoreConnectionPanel(OptionsDialog parent, AJSettings ajSettings) {
        Logger logger = LoggerFactory.getLogger(getClass());
        this.parent = parent;
        this.ajSettings = ajSettings;
        try {
            init();
        } catch (Exception ex) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, ex);
        }
    }

    public boolean isDirty() {
        return dirty;
    }

    private void init() throws Exception {
        IconManager im = IconManager.getInstance();

        menuIcon = im.getIcon("opt_verbindung");
        setLayout(new BorderLayout());
        JPanel panel1 = new JPanel(new GridBagLayout());

        FlowLayout flowL = new FlowLayout();

        flowL.setAlignment(FlowLayout.RIGHT);
        JPanel panel9 = new JPanel(flowL);

        maxConnections.setDocument(new NumberInputVerifier());
        maxConnections.setHorizontalAlignment(JLabel.RIGHT);
        maxConnections.addFocusListener(new MaxConnectionsFocusListener());
        maxUpload.addFocusLostListener(this::maxUploadChanged);
        maxDownload.addFocusLostListener(this::maxDownloadChanged);
        maxSourcesPerFile.setDocument(new NumberInputVerifier());
        maxSourcesPerFile.setHorizontalAlignment(JLabel.RIGHT);
        maxSourcesPerFile.addFocusListener(new MaxSourcesPerFileFocusListener());
        maxConnectionsPerTurn.setDocument(new NumberInputVerifier());
        maxConnectionsPerTurn.setHorizontalAlignment(JLabel.RIGHT);
        maxConnectionsPerTurn.addFocusListener(new MaxConnectionsPerTurnFocusListener());

        LanguageSelector languageSelector = LanguageSelector.getInstance();

        label1 = new JLabel(languageSelector.getFirstAttributeByTagName("einstform.Label4.caption"));
        label2 = new JLabel(languageSelector.getFirstAttributeByTagName("javagui.options.connection.maxupload"));
        label3 = new JLabel(languageSelector.getFirstAttributeByTagName("javagui.options.connection.label3"));
        label4 = new JLabel(languageSelector.getFirstAttributeByTagName("javagui.options.connection.maxdownload"));
        label5 = new JLabel(languageSelector.getFirstAttributeByTagName("javagui.options.connection.label5"));
        label6 = new JLabel(languageSelector.getFirstAttributeByTagName("javagui.options.connection.label6"));
        menuText = languageSelector.getFirstAttributeByTagName("einstform.connectionsheet.caption");
        wizard.setText(languageSelector.getFirstAttributeByTagName("javagui.options.connection.labelwizard"));
        kbSlot = new JLabel();

        int lowerLimit = (int) Math.pow((double) ajSettings.getMaxUploadInKB(), 0.2);
        int upperLimit = (int) Math.pow((double) ajSettings.getMaxUploadInKB(), 0.6);

        kbSlider = new JSlider(lowerLimit, upperLimit);
        kbSlider.setMajorTickSpacing(1);
        kbSlider.setMinorTickSpacing(1);
        kbSlider.setSnapToTicks(true);
        kbSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider slider = (JSlider) e.getSource();

                kbSlot.setText(Integer.toString(slider.getValue()) + " kb/s");
                dirty = true;
                ajSettings.setSpeedPerSlot(slider.getValue());
            }
        });
        automaticConnect = new JCheckBox(languageSelector.getFirstAttributeByTagName("einstform.autoconn.caption"));
        automaticConnect.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                dirty = true;
                ajSettings.setAutoConnect(automaticConnect.isSelected());
            }
        });
        wizard.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayConnectionWizard();
            }
        });

        JPanel slot = new JPanel(new BorderLayout(5, 0));
        slot.add(kbSlider, BorderLayout.CENTER);
        slot.add(kbSlot, BorderLayout.EAST);
        JLabel[] labels = {label1, label2, label3, label4, label5, label6};
        JComponent[] fields = {maxConnections, maxUpload, slot, maxDownload,
                maxConnectionsPerTurn, maxSourcesPerFile};
        String[] hints = {"javagui.options.connection.hint.maxconnections",
                "javagui.options.connection.hint.maxupload", "javagui.options.connection.hint.slot",
                "javagui.options.connection.hint.maxdownload", "javagui.options.connection.hint.newconnections",
                "javagui.options.connection.hint.sources"};
        for (int row = 0; row < labels.length; row++) {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridy = row;
            constraints.fill = GridBagConstraints.BOTH;
            constraints.insets = new Insets(5, 5, 0, 0);
            panel1.add(labels[row], constraints);
            constraints.gridx = 1;
            JLabel hint = new JLabel(im.getIcon("hint")) {
                @Override public JToolTip createToolTip() {
                    JToolTip tip = new de.applejuicenet.client.shared.MultiLineToolTip();
                    tip.setComponent(this);
                    return tip;
                }
            };
            hint.setToolTipText(languageSelector.getFirstAttributeByTagName(hints[row]));
            panel1.add(hint, constraints);
            constraints.gridx = 2;
            constraints.weightx = 1;
            constraints.insets.right = 5;
            panel1.add(fields[row], constraints);
        }

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = labels.length;
        constraints.gridwidth = 3;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.insets = new Insets(5, 5, 0, 5);
        panel9.add(automaticConnect);
        panel1.add(panel9, constraints);
        constraints.gridy++;
        JPanel panel11 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel11.add(wizard);
        panel1.add(panel11, constraints);

        add(panel1, BorderLayout.NORTH);

        reloadSettings();
    }

    public Icon getIcon() {
        return menuIcon;
    }

    public String getMenuText() {
        return menuText;
    }

    public void displayConnectionWizard() {
        boolean regularClosed = AppleJuiceClient.showConnectionWizard(parent, ajSettings);

        if (regularClosed) {
            parent.reloadSettings();
        }
    }

    public void reloadSettings() {
        AJSettings ajSettings2 = AppleJuiceClient.getAjFacade().getAJSettings();

      /*
      ajSettings.setAutoConnect(ajSettings2.isAutoConnect());
      ajSettings.setIncomingDir(ajSettings2.getIncomingDir());
      ajSettings.setMaxConnections(ajSettings2.getMaxConnections());
      ajSettings.setMaxDownload(ajSettings2.getMaxDownload());
      ajSettings.setMaxNewConnectionsPerTurn(ajSettings2.getMaxNewConnectionsPerTurn());
      ajSettings.setMaxSourcesPerFile(ajSettings2.getMaxSourcesPerFile());
      ajSettings.setMaxUpload(ajSettings2.getMaxUpload());
      ajSettings.setNick(ajSettings2.getNick());
      ajSettings.setPort(ajSettings2.getPort());
      ajSettings.setShareDirs(ajSettings2.getShareDirs());
      ajSettings.setSpeedPerSlot(ajSettings2.getSpeedPerSlot());
      ajSettings.setTempDir(ajSettings2.getTempDir());
      ajSettings.setXMLPort(ajSettings2.getXMLPort());
      */
        maxUpload.setKb(ajSettings2.getMaxUploadInKB());
        maxDownload.setKb(ajSettings2.getMaxDownloadInKB());
        maxConnections.setText(Long.toString(ajSettings2.getMaxConnections()));
        kbSlider.setValue(ajSettings2.getSpeedPerSlot());
        kbSlot.setText(Integer.toString(kbSlider.getValue()) + " kb/s");
        automaticConnect.setSelected(ajSettings2.isAutoConnect());
        maxConnectionsPerTurn.setText(Long.toString(ajSettings2.getMaxNewConnectionsPerTurn()));
        maxSourcesPerFile.setText(Long.toString(ajSettings2.getMaxSourcesPerFile()));
    }

    class MaxConnectionsPerTurnFocusListener extends FocusAdapter {
        public void focusLost(FocusEvent e) {
            long value = Long.parseLong(maxConnectionsPerTurn.getText());

            if (value != ajSettings.getMaxNewConnectionsPerTurn()) {
                if (value < 1 || value > 200) {
                    maxConnectionsPerTurn.setText(Long.toString(ajSettings.getMaxNewConnectionsPerTurn()));
                } else {
                    dirty = true;
                    ajSettings.setMaxNewConnectionsPerTurn(Long.parseLong(maxConnectionsPerTurn.getText()));
                }
            }
        }
    }


    class MaxSourcesPerFileFocusListener extends FocusAdapter {
        public void focusLost(FocusEvent e) {
            long value = Long.parseLong(maxSourcesPerFile.getText());

            if (value != ajSettings.getMaxSourcesPerFile()) {
                if (value < 1) {
                    maxSourcesPerFile.setText(Long.toString(ajSettings.getMaxSourcesPerFile()));
                } else {
                    dirty = true;
                    ajSettings.setMaxSourcesPerFile(Long.parseLong(maxSourcesPerFile.getText()));
                }
            }
        }
    }


    private void maxDownloadChanged() {
        long kb = maxDownload.getKb();

        if (kb != ajSettings.getMaxDownloadInKB()) {
            dirty = true;
            ajSettings.setMaxDownload(kb * 1024);
        }
    }

    private void maxUploadChanged() {
        long kb = maxUpload.getKb();

        kbSlider.setMinimum((int) Math.pow((double) kb, 0.2));
        kbSlider.setMaximum((int) Math.pow((double) kb, 0.6));
        if (kb != ajSettings.getMaxUploadInKB()) {
            dirty = true;
            ajSettings.setMaxUpload(kb * 1024);
        }
    }


    class MaxConnectionsFocusListener extends FocusAdapter {
        public void focusLost(FocusEvent e) {
            if (Long.parseLong(maxConnections.getText()) != ajSettings.getMaxConnections()) {
                dirty = true;
                ajSettings.setMaxConnections(Long.parseLong(maxConnections.getText()));
            }
        }
    }
}
