/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.options;

import de.applejuicenet.client.gui.DialogLocation;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.facade.ApplejuiceFacade;
import de.applejuicenet.client.facade.shared.AJSettings;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.shared.ConnectionSettings;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.MultiLineToolTip;
import de.applejuicenet.client.shared.NumberInputVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Copyright: General Public License
 */
public class ODStandardPanel extends JPanel implements OptionsRegister {
    private boolean dirty = false;
    private boolean xmlPortDirty = false;
    private JLabel label1 = new JLabel();
    private JLabel label2 = new JLabel();
    private JLabel label3 = new JLabel();
    private JLabel label4 = new JLabel();
    private JLabel label6 = new JLabel();
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
    private JLabel hint6;
    private JDialog parent;
    private AJSettings ajSettings;
    private ConnectionSettings remote;
    private Icon menuIcon;
    private String menuText;

    public ODStandardPanel(JDialog parent, AJSettings ajSettings, ConnectionSettings remote) {
        Logger logger = LoggerFactory.getLogger(getClass());
        try {
            this.remote = remote;
            this.parent = parent;
            this.ajSettings = ajSettings;
            init();
        } catch (Exception e) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
        }
    }

    public boolean isDirty() {
        return dirty;
    }

    private void init() {
        IconManager im = IconManager.getInstance();

        menuIcon = im.getIcon("opt_standard");
        port.setDocument(new NumberInputVerifier());
        xmlPort.setDocument(new NumberInputVerifier());
        temp.setEditable(false);
        incoming.setEditable(false);
        port.addFocusListener(new PortFocusListener());
        xmlPort.addFocusListener(new XmlPortFocusListener());
        nick.addFocusListener(new NickFocusListener());

        LanguageSelector languageSelector = LanguageSelector.getInstance();
        menuText = languageSelector.getFirstAttributeByTagName("einstform.standardsheet.caption");
        setLayout(new BorderLayout());
        port.setHorizontalAlignment(JLabel.RIGHT);
        xmlPort.setHorizontalAlignment(JLabel.RIGHT);
        reloadSettings();
        JPanel panel6 = new JPanel(new GridBagLayout());

        label1.setText(languageSelector.getFirstAttributeByTagName("einstform.Label2.caption"));
        label2.setText(languageSelector.getFirstAttributeByTagName("einstform.Label7.caption"));
        label3.setText(languageSelector.getFirstAttributeByTagName("einstform.Label3.caption"));
        label4.setText(languageSelector.getFirstAttributeByTagName("einstform.Label8.caption"));
        label6.setText(languageSelector.getFirstAttributeByTagName("javagui.options.standard.xmlport"));

        ImageIcon icon = im.getIcon("hint");

        hint1 = new HintLabel(icon);
        hint2 = new HintLabel(icon);
        hint3 = new HintLabel(icon);
        hint4 = new HintLabel(icon);
        hint6 = new HintLabel(icon);
        hint1.setToolTipText(languageSelector.getFirstAttributeByTagName("javagui.options.standard.tooltip_temp"));
        hint2.setToolTipText(languageSelector.getFirstAttributeByTagName("javagui.options.standard.tooltip_port"));
        hint3.setToolTipText(languageSelector.getFirstAttributeByTagName("javagui.options.standard.tooltip_nick"));
        hint4.setToolTipText(languageSelector.getFirstAttributeByTagName("javagui.options.standard.tooltip_incoming"));
        hint6.setToolTipText(languageSelector.getFirstAttributeByTagName("javagui.options.standard.tooltip_xmlport"));

        Icon icon2 = im.getIcon("folderopen");
        DirectoryChooserMouseAdapter dcMouseAdapter = new DirectoryChooserMouseAdapter();

        openTemp = new JLabel(icon2);
        openTemp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        openTemp.addMouseListener(dcMouseAdapter);
        openIncoming = new JLabel(icon2);
        openIncoming.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        openIncoming.addMouseListener(dcMouseAdapter);

        JLabel[] labels = {label1, label2, label3, label6, label4};
        JLabel[] hints = {hint1, hint4, hint2, hint6, hint3};
        JTextField[] fields = {temp, incoming, port, xmlPort, nick};
        JLabel[] choosers = {openTemp, openIncoming, null, null, null};
        for (int row = 0; row < labels.length; row++) {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridy = row;
            constraints.fill = GridBagConstraints.BOTH;
            constraints.insets = new Insets(5, 5, 0, 0);
            panel6.add(labels[row], constraints);
            constraints.gridx = 1;
            constraints.fill = GridBagConstraints.VERTICAL;
            if (hints[row] != null) panel6.add(hints[row], constraints);
            constraints.gridx = 2;
            constraints.weightx = 1;
            constraints.fill = GridBagConstraints.BOTH;
            panel6.add(fields[row], constraints);
            constraints.gridx = 3;
            constraints.weightx = 0;
            constraints.insets.right = 5;
            if (choosers[row] != null) panel6.add(choosers[row], constraints);
        }
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
        AJSettings ajSettings2 = AppleJuiceClient.getAjFacade().getAJSettings();

        temp.setText(ajSettings2.getTempDir());
        incoming.setText(ajSettings2.getIncomingDir());
        port.setText(Long.toString(ajSettings2.getPort()));
        xmlPort.setText(Long.toString(ajSettings2.getXMLPort()));
        nick.setText(ajSettings2.getNick());
    }

    class PortFocusListener extends FocusAdapter {
        public void focusLost(FocusEvent e) {
            int portNr;
            try {
                portNr = Integer.parseInt(port.getText());
            } catch (NumberFormatException ex) {
                port.setText(Long.toString(ajSettings.getPort()));
                return;
            }

            if (ajSettings.getPort() != portNr) {
                if (portNr > 1024 && portNr <= 65535) {
                    dirty = true;
                    ajSettings.setPort(portNr);
                } else {
                    port.setText(Long.toString(ajSettings.getPort()));
                }
            }
        }
    }

    class NickFocusListener extends FocusAdapter {
        public void focusLost(FocusEvent e) {
            if (ajSettings.getNick().compareTo(nick.getText()) != 0) {
                dirty = true;
                ajSettings.setNick(nick.getText());
            }
        }
    }

    class XmlPortFocusListener extends FocusAdapter {
        public void focusLost(FocusEvent e) {
            int xmlPortNr;
            try {
                xmlPortNr = Integer.parseInt(xmlPort.getText());
            } catch (NumberFormatException ex) {
                xmlPort.setText(Long.toString(ajSettings.getXMLPort()));
                return;
            }

            if (ajSettings.getXMLPort() != xmlPortNr) {
                if (xmlPortNr > 1024 && xmlPortNr <= 65535) {
                    dirty = true;
                    xmlPortDirty = true;
                    remote.setXmlPort(xmlPortNr);
                    ajSettings.setXMLPort(xmlPortNr);
                } else {
                    xmlPort.setText(Long.toString(ajSettings.getXMLPort()));
                }
            }
        }
    }

    class HintLabel extends JLabel {
        HintLabel(Icon image) {
            super(image);
        }

        public JToolTip createToolTip() {
            MultiLineToolTip tip = new MultiLineToolTip();

            tip.setComponent(this);
            return tip;
        }
    }

    class DirectoryChooserMouseAdapter extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            JLabel source = (JLabel) e.getSource();
            String title = "";

            if (source == openTemp) {
                title = label1.getText();
            } else {
                title = label2.getText();
            }

            ODDirectoryChooser chooser = new ODDirectoryChooser(parent, title);

            DialogLocation.center(chooser);
            chooser.setVisible(true);
            if (chooser.isNewPathSelected()) {
                dirty = true;
                String path = chooser.getSelectedPath();

                if (source == openTemp) {
                    temp.setText(path);
                    ajSettings.setTempDir(path);
                } else {
                    incoming.setText(path);
                    ajSettings.setIncomingDir(path);
                }
            }
        }

    }
}
