package de.applejuicenet.client.gui;

import java.io.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Enumeration;

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

public class ODStandardPanel
    extends JPanel {
  private JLabel label1 = new JLabel();
  private JLabel label2 = new JLabel();
  private JLabel label3 = new JLabel();
  private JLabel label4 = new JLabel();
  private JLabel label5 = new JLabel();
  private JLabel openTemp;
  private JLabel openIncoming;
  private JTextField temp = new JTextField();
  private JTextField incoming = new JTextField();
  private JTextField text3 = new JTextField();
  private JTextField text4 = new JTextField();
  private JComboBox cmbUploadPrio = new JComboBox();
  private JLabel hint1;
  private JLabel hint2;
  private JLabel hint3;
  private JFrame parent;

  public ODStandardPanel(JFrame parent) {
    this.parent = parent;
    try {
      jbInit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    setLayout(new BorderLayout());
    JPanel panel6 = new JPanel(new GridBagLayout());
    LanguageSelector languageSelector = LanguageSelector.getInstance();
    label1.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"einstform", "Label2",
                                  "caption"})));
    label2.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"einstform", "Label7",
                                  "caption"})));
    label3.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"einstform", "Label3",
                                  "caption"})));
    label4.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"einstform", "Label8",
                                  "caption"})));
    label5.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"einstform", "Label14",
                                  "caption"})));
    IconManager im = IconManager.getInstance();
    ImageIcon icon = im.getIcon("hint");
    hint1 = new JLabel(icon) {
      public JToolTip createToolTip() {
        MultiLineToolTip tip = new MultiLineToolTip();
        tip.setComponent(this);
        return tip;
      }
    };
    hint2 = new JLabel(icon) {
      public JToolTip createToolTip() {
        MultiLineToolTip tip = new MultiLineToolTip();
        tip.setComponent(this);
        return tip;
      }
    };
    hint3 = new JLabel(icon) {
      public JToolTip createToolTip() {
        MultiLineToolTip tip = new MultiLineToolTip();
        tip.setComponent(this);
        return tip;
      }
    };
    hint1.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"javagui", "options",
                                  "standard", "ttipp_temp"})));
    hint2.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"javagui", "options",
                                  "standard", "ttipp_port"})));
    hint3.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"javagui", "options",
                                  "standard", "ttipp_nick"})));

    for (int i = 1; i < 11; i++) {
      cmbUploadPrio.addItem(new Integer(i));
    }
    Icon icon2 = UIManager.getIcon("FileChooser.detailsViewIcon");
    FileChooserMouseAdapter fcMouseAdapter = new FileChooserMouseAdapter();
    openTemp = new JLabel(icon2);
    openTemp.addMouseListener(fcMouseAdapter);
    openIncoming = new JLabel(icon2);
    openIncoming.addMouseListener(fcMouseAdapter);

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
    JPanel panel5 = new JPanel(new GridBagLayout());

    constraints.insets.right = 5;
    constraints.insets.left = 4;
    panel1.add(label1, constraints);
    panel2.add(label2, constraints);
    panel3.add(label3, constraints);
    panel4.add(label4, constraints);
    panel5.add(label5, constraints);

    constraints.insets.left = 0;
    constraints.insets.right = 2;
    constraints.gridx = 1;
    constraints.weightx = 1;
    panel1.add(temp, constraints);
    panel2.add(incoming, constraints);
    panel3.add(text3, constraints);
    panel4.add(text4, constraints);
    panel5.add(cmbUploadPrio, constraints);
    constraints.gridx = 2;
    constraints.weightx = 0;
    panel1.add(openTemp, constraints);
    panel2.add(openIncoming, constraints);

    constraints.gridx = 0;
    constraints.gridy = 0;
    constraints.weightx = 1;
    panel6.add(panel1, constraints);
    constraints.gridy = 1;
    panel6.add(panel2, constraints);
    constraints.gridy = 2;
    panel6.add(panel3, constraints);
    constraints.gridy = 3;
    panel6.add(panel4, constraints);
    constraints.gridy = 4;
    panel6.add(panel5, constraints);

    constraints.insets.top = 10;
    constraints.gridx = 1;
    constraints.gridy = 0;
    constraints.weightx = 0;
    panel6.add(hint1, constraints);
    constraints.gridy = 2;
    panel6.add(hint2, constraints);
    constraints.gridy = 3;
    panel6.add(hint3, constraints);
    add(panel6, BorderLayout.NORTH);
  }

  class FileChooserMouseAdapter extends MouseAdapter{
    public void mouseEntered(MouseEvent e){
      JLabel source = (JLabel) e.getSource();
      source.setBorder(BorderFactory.createLineBorder(Color.black));
    }

    public void mouseClicked(MouseEvent e){
      JLabel source = (JLabel) e.getSource();
      JFileChooser jf = new JFileChooser();
      jf.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      int ret = jf.showOpenDialog(parent);
      if (ret == JFileChooser.APPROVE_OPTION) {
        File selectedFile = jf.getSelectedFile();
        if (selectedFile.isDirectory()) {
          if (source == openTemp)
            temp.setText(selectedFile.getPath());
          else if (source == openIncoming)
            incoming.setText(selectedFile.getPath());
        }
      }
    }

    public void mouseExited(MouseEvent e){
      JLabel source = (JLabel) e.getSource();
      source.setBorder(null);
    }
  }
}
