package de.applejuicenet.client.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import de.applejuicenet.client.gui.controller.*;
import de.applejuicenet.client.shared.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/ODVerbindungPanel.java,v 1.11 2003/10/14 15:43:52 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: ODVerbindungPanel.java,v $
 * Revision 1.11  2003/10/14 15:43:52  maj0r
 * An pflegbaren Xml-Port angepasst.
 *
 * Revision 1.10  2003/09/10 13:16:28  maj0r
 * Veraltete Option "Browsen erlauben" entfernt und neue Option MaxNewConnectionsPerTurn hinzugefuegt.
 *
 * Revision 1.9  2003/09/08 16:46:08  maj0r
 * Ueberfluessige Einstellungen entfernt.
 *
 * Revision 1.8  2003/08/10 21:08:18  maj0r
 * Diverse Änderungen.
 *
 * Revision 1.7  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class ODVerbindungPanel
        extends JPanel {
    private boolean dirty = false;
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;
    private JLabel label5;
    private JLabel kbSlot;
    private JCheckBox automaticConnect;
    private JTextField maxVerbindungen = new JTextField();
    private JTextField maxUpload = new JTextField();
    private JTextField maxDownload = new JTextField();
    private JSlider kbSlider;
    private JTextField maxVerbindungenProTurn = new JTextField();
    private AJSettings ajSettings;
    private Logger logger;

    public ODVerbindungPanel(AJSettings ajSettings) {
        logger = Logger.getLogger(getClass());
        this.ajSettings = ajSettings;
        try
        {
            jbInit();
        }
        catch (Exception ex)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", ex);
        }
    }

    public boolean isDirty() {
        return dirty;
    }

    private void jbInit() throws Exception {
        setLayout(new BorderLayout());
        JPanel panel1 = new JPanel(new GridBagLayout());

        JPanel panel2 = new JPanel(new GridBagLayout());
        JPanel panel3 = new JPanel(new GridBagLayout());
        JPanel panel4 = new JPanel(new GridBagLayout());
        JPanel panel5 = new JPanel(new GridBagLayout());
        JPanel panel6 = new JPanel(new GridBagLayout());
        JPanel panel7 = new JPanel(new GridBagLayout());
        JPanel panel8 = new JPanel(new GridBagLayout());
        JPanel panel10 = new JPanel(new GridBagLayout());
        FlowLayout flowL = new FlowLayout();
        flowL.setAlignment(FlowLayout.RIGHT);
        JPanel panel9 = new JPanel(flowL);

        maxVerbindungen.setDocument(new NumberInputVerifier());
        maxVerbindungen.setHorizontalAlignment(JLabel.RIGHT);
        maxVerbindungen.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                if (Long.parseLong(maxVerbindungen.getText()) !=
                        ajSettings.getMaxConnections())
                {
                    dirty = true;
                    ajSettings.setMaxConnections(Long.parseLong(maxVerbindungen.getText()));
                }
            }
        });
        maxUpload.setDocument(new NumberInputVerifier());
        maxUpload.setHorizontalAlignment(JLabel.RIGHT);
        maxUpload.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                int untereGrenze = (int) Math.pow(Double.parseDouble(maxUpload.getText()),
                                                  0.2);
                int obereGrenze = (int) Math.pow(Double.parseDouble(maxUpload.getText()),
                                                 0.6);
                kbSlider.setMinimum(untereGrenze);
                kbSlider.setMaximum(obereGrenze);
                if (Long.parseLong(maxUpload.getText()) != ajSettings.getMaxUploadInKB())
                {
                    dirty = true;
                    ajSettings.setMaxUpload(Long.parseLong(maxUpload.getText()) * 1024);
                }
            }
        });
        maxDownload.setDocument(new NumberInputVerifier());
        maxDownload.setHorizontalAlignment(JLabel.RIGHT);
        maxDownload.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                if (Long.parseLong(maxDownload.getText()) !=
                        ajSettings.getMaxDownloadInKB())
                {
                    dirty = true;
                    ajSettings.setMaxDownload(Long.parseLong(maxDownload.getText()) *
                                              1024);
                }
            }
        });
        maxVerbindungenProTurn.setDocument(new NumberInputVerifier());
        maxVerbindungenProTurn.setHorizontalAlignment(JLabel.RIGHT);
        maxVerbindungenProTurn.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                long wert = Long.parseLong(maxVerbindungenProTurn.getText());
                if (wert != ajSettings.getMaxNewConnectionsPerTurn())
                {
                    if (wert < 1 || wert > 200)
                    {
                        maxVerbindungenProTurn.setText(Long.toString(ajSettings.getMaxNewConnectionsPerTurn()));
                    }
                    else
                    {
                        dirty = true;
                        ajSettings.setMaxNewConnectionsPerTurn(Long.parseLong(maxVerbindungenProTurn.getText()));
                    }
                }
            }
        });
        LanguageSelector languageSelector = LanguageSelector.getInstance();

        label1 = new JLabel(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                              getFirstAttrbuteByTagName(new String[]{"einstform", "Label4",
                                                                                                     "caption"})));
        label2 = new JLabel(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                              getFirstAttrbuteByTagName(new String[]{"einstform", "Label5",
                                                                                                     "caption"})));
        label3 = new JLabel(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                              getFirstAttrbuteByTagName(new String[]{"javagui", "options",
                                                                                                     "verbindung", "label3"})));
        label4 = new JLabel(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                              getFirstAttrbuteByTagName(new String[]{"einstform", "Label13",
                                                                                                     "caption"})));
        label5 = new JLabel(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                              getFirstAttrbuteByTagName(new String[]{"javagui", "options",
                                                                                                     "verbindung", "label5"})));
        kbSlot = new JLabel();

        int untereGrenze = (int) Math.pow((double) ajSettings.getMaxUploadInKB(),
                                          0.2);
        int obereGrenze = (int) Math.pow((double) ajSettings.getMaxUploadInKB(),
                                         0.6);

        kbSlider = new JSlider(untereGrenze, obereGrenze);
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
        automaticConnect = new JCheckBox(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[]{"einstform", "autoconn",
                                                       "caption"})));
        automaticConnect.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                dirty = true;
                ajSettings.setAutoConnect(automaticConnect.isSelected());
            }
        });
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets.top = 5;

        constraints.insets.left = 5;
        panel2.add(label1, constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        panel2.add(maxVerbindungen, constraints);
        constraints.weightx = 0;

        constraints.gridx = 0;
        panel3.add(label2, constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        panel3.add(maxUpload, constraints);
        constraints.weightx = 0;

        constraints.gridx = 0;
        panel4.add(label3, constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        panel4.add(kbSlider, constraints);
        constraints.gridx = 2;
        constraints.weightx = 0;
        panel4.add(kbSlot, constraints);

        constraints.gridx = 0;
        panel5.add(label4, constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        panel5.add(maxDownload, constraints);
        constraints.weightx = 0;

        constraints.gridx = 0;
        panel10.add(label5, constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        panel10.add(maxVerbindungenProTurn, constraints);
        constraints.weightx = 0;

        panel9.add(automaticConnect);

        constraints.gridx = 0;
        constraints.weightx = 1;
        constraints.gridy = 0;
        constraints.insets.right = 5;
        panel1.add(panel2, constraints);
        constraints.gridy = 1;
        panel1.add(panel3, constraints);
        constraints.gridy = 2;
        panel1.add(panel4, constraints);
        constraints.gridy = 3;
        panel1.add(panel5, constraints);
        constraints.gridy = 4;
        panel1.add(panel6, constraints);
        constraints.gridy = 5;
        panel1.add(panel7, constraints);
        constraints.gridy = 6;
        panel1.add(panel8, constraints);
        constraints.gridy = 7;
        panel1.add(panel10, constraints);
        constraints.gridy = 8;
        panel1.add(panel9, constraints);

        add(panel1, BorderLayout.NORTH);

        maxUpload.setText(Long.toString(ajSettings.getMaxUploadInKB()));
        maxDownload.setText(Long.toString(ajSettings.getMaxDownloadInKB()));
        maxVerbindungen.setText(Long.toString(ajSettings.getMaxConnections()));
        kbSlider.setValue(ajSettings.getSpeedPerSlot());
        kbSlot.setText(Integer.toString(kbSlider.getValue()) + " kb/s");
        automaticConnect.setSelected(ajSettings.isAutoConnect());
        maxVerbindungenProTurn.setText(Long.toString(ajSettings.getMaxNewConnectionsPerTurn()));
    }
}