package de.applejuicenet.client.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import de.applejuicenet.client.gui.controller.*;
import de.applejuicenet.client.shared.*;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class ODVerbindungPanel
    extends JPanel {
  private boolean dirty = false;
  private JLabel label1;
  private JLabel label2;
  private JLabel label3;
  private JLabel label4;
  private JLabel label5;
  private JLabel label6;
  private JLabel label7;
  private JLabel kbSlot;
  private JCheckBox automaticConnect;
  private JTextField maxVerbindungen = new JTextField();
  private JTextField maxUpload = new JTextField();
  private JTextField maxDownload = new JTextField();
  private JTextField anzahl = new JTextField();
  private JTextField url = new JTextField();
  private JTextField pinggrenze = new JTextField();
  private JSlider kbSlider;
  private JLabel btnPdlUp;
  private JLabel btnPdlDown;
  private int anzahlDownloads = 0;
  private AJSettings ajSettings;

  public ODVerbindungPanel(AJSettings ajSettings) {
    this.ajSettings = ajSettings;
    try {
      jbInit();
    }
    catch (Exception e) {
      e.printStackTrace();
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
    FlowLayout flowL = new FlowLayout();
    flowL.setAlignment(FlowLayout.RIGHT);
    JPanel panel9 = new JPanel(flowL);

    anzahl.setText(Integer.toString(anzahlDownloads));
    anzahl.setDocument(new NumberInputVerifier(0, 1000));
    anzahl.addFocusListener(new FocusAdapter() {
      public void focusLost(FocusEvent e) {
        anzahlDownloads = Integer.parseInt(anzahl.getText());
      }
    });
    maxVerbindungen.setDocument(new NumberInputVerifier());
    maxVerbindungen.setHorizontalAlignment(JLabel.RIGHT);
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
        if (Long.parseLong(maxUpload.getText()) != ajSettings.getMaxUploadInKB()) {
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
            ajSettings.getMaxDownloadInKB()) {
          dirty = true;
          ajSettings.setMaxDownload(Long.parseLong(maxDownload.getText()) *
                                    1024);
        }
      }
    });
    pinggrenze.setDocument(new NumberInputVerifier());

    LanguageSelector languageSelector = LanguageSelector.getInstance();

    label1 = new JLabel(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"einstform", "Label4",
                                  "caption"})));
    label2 = new JLabel(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"einstform", "Label5",
                                  "caption"})));
    label3 = new JLabel(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"javagui", "options",
                                  "verbindung", "label3"})));
    label4 = new JLabel(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"einstform", "Label13",
                                  "caption"})));
    label5 = new JLabel(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"einstform", "Label9",
                                  "caption"})));
    label6 = new JLabel(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"einstform", "Label19",
                                  "caption"})));
    label7 = new JLabel(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"einstform", "Label18",
                                  "caption"})));
    kbSlot = new JLabel();

    IconManager im = IconManager.getInstance();
    ImageIcon icon2 = im.getIcon("increase");
    btnPdlUp = new JLabel(icon2);
    ImageIcon icon3 = im.getIcon("decrease");
    btnPdlDown = new JLabel(icon3);
    SlotMouseAdapter slotMA = new SlotMouseAdapter();
    btnPdlUp.addMouseListener(slotMA);
    btnPdlDown.addMouseListener(slotMA);

    int untereGrenze = (int) Math.pow( (double) ajSettings.getMaxUploadInKB(),
                                      0.2);
    int obereGrenze = (int) Math.pow( (double) ajSettings.getMaxUploadInKB(),
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
        getFirstAttrbuteByTagName(new String[] {"einstform", "autoconn",
                                  "caption"})));

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
    panel6.add(label5, constraints);
    constraints.gridx = 1;
    panel6.add(btnPdlUp, constraints);
    constraints.gridx = 2;
    constraints.weightx = 1;
    constraints.insets.left = 0;
    panel6.add(anzahl, constraints);
    constraints.gridx = 3;
    constraints.weightx = 0;
    panel6.add(btnPdlDown, constraints);
    constraints.insets.left = 5;

    constraints.gridx = 0;
    panel7.add(label6, constraints);
    constraints.gridx = 1;
    constraints.weightx = 1;
    panel7.add(url, constraints);
    constraints.weightx = 0;

    constraints.gridx = 0;
    panel8.add(label7, constraints);
    constraints.gridx = 1;
    constraints.weightx = 1;
    panel8.add(pinggrenze, constraints);

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
    panel1.add(panel9, constraints);

    add(panel1, BorderLayout.NORTH);

    maxUpload.setText(Long.toString(ajSettings.getMaxUploadInKB()));
    maxDownload.setText(Long.toString(ajSettings.getMaxDownloadInKB()));
    kbSlider.setValue(ajSettings.getSpeedPerSlot());
    kbSlot.setText(Integer.toString(kbSlider.getValue()) + " kb/s");
  }

  class SlotMouseAdapter
      extends MouseAdapter {
    public void mouseClicked(MouseEvent e) {
      JLabel source = (JLabel) e.getSource();
      if (source == btnPdlUp) {
        if (anzahlDownloads == 1000) {
          return;
        }
        anzahlDownloads++;
      }
      else if (source == btnPdlDown) {
        if (anzahlDownloads == 0) {
          return;
        }
        anzahlDownloads--;
      }
      anzahl.setText(Integer.toString(anzahlDownloads));
    }

    public void mouseEntered(MouseEvent e) {
      JLabel source = (JLabel) e.getSource();
      source.setBorder(BorderFactory.createLineBorder(Color.black));

    }

    public void mouseExited(MouseEvent e) {
      JLabel source = (JLabel) e.getSource();
      source.setBorder(null);
    }
  }
}