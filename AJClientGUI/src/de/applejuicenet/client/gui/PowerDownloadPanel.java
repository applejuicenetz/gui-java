package de.applejuicenet.client.gui;

import java.net.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import de.applejuicenet.client.gui.controller.DataManager;
import de.applejuicenet.client.shared.NumberInputVerifier;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class PowerDownloadPanel
    extends JPanel {
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  private JRadioButton btnInaktiv = new JRadioButton();
  private JRadioButton btnAktiv = new JRadioButton();
  private JRadioButton btnAutoInaktiv = new JRadioButton();
  private JRadioButton btnAutoAktiv = new JRadioButton();
  private JLabel btnHint;
  private JLabel btnPdlUp;
  private JLabel btnPdlDown;
  private float ratioWert = 2.2f;
  private JTextField ratio = new JTextField("2.2");
  private JTextField autoAb = new JTextField();
  JButton btnPdl = new JButton("Übernehmen");
  JButton btnAutoPdl = new JButton("Übernehmen");

  public PowerDownloadPanel() {
    try {
      jbInit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    setLayout(new BorderLayout());
    JPanel backPanel = new JPanel();
    backPanel.setLayout(gridBagLayout1);

    GridBagConstraints constraints = new GridBagConstraints();
    constraints.anchor = GridBagConstraints.NORTH;
    constraints.fill = GridBagConstraints.BOTH;
    constraints.gridx = 0;
    constraints.gridy = 0;
    constraints.gridheight = 1;
    constraints.gridwidth = 3;
    JPanel tempPanel = new JPanel();
    tempPanel.setLayout(new BorderLayout());
    JLabel powerdownload = new JLabel("Powerdownload");
    powerdownload.setForeground(Color.white);
    powerdownload.setOpaque(true);
    powerdownload.setBackground(Color.blue);
    ratio.setBackground(Color.white);
    ratio.setMinimumSize(new Dimension(50, 21));
    ratio.setPreferredSize(new Dimension(50, 21));
    ratio.setEditable(false);
    ratio.setHorizontalAlignment(SwingConstants.RIGHT);
    btnPdl.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        btnPdl_actionPerformed(e);
      }
    });
    tempPanel.add(powerdownload, BorderLayout.CENTER);

    URL url = getClass().getResource("hint.gif");
    ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(url));
    btnHint = new JLabel(icon);
    btnHint.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        executeHint();
      }
    });
    tempPanel.add(btnHint, BorderLayout.EAST);
    backPanel.add(tempPanel, constraints);
    constraints.gridy = 1;
    backPanel.add(new JLabel("Wieviel willst Du maximal für 1 Byte bezahlen?"),
                  constraints);
    constraints.gridwidth = 1;
    constraints.gridy = 2;
    ButtonGroup buttonGroup = new ButtonGroup();
    buttonGroup.add(btnInaktiv);
    buttonGroup.add(btnAktiv);
    btnInaktiv.setText("Powerdownload inaktiv");
    btnInaktiv.setSelected(true);
    btnAktiv.setText("Powerdownload aktiv");
    backPanel.add(btnInaktiv, constraints);
    constraints.gridy = 3;
    backPanel.add(btnAktiv, constraints);
    JPanel tempFlowPanel = new JPanel();
    tempFlowPanel.setLayout(new FlowLayout());
    tempFlowPanel.add(new JLabel("Für 1 Byte zahle"));
    url = getClass().getResource("upload.gif");
    ImageIcon icon2 = new ImageIcon(Toolkit.getDefaultToolkit().getImage(url));
    btnPdlUp = new JLabel(icon2);
    url = getClass().getResource("download.GIF");
    ImageIcon icon3 = new ImageIcon(Toolkit.getDefaultToolkit().getImage(url));
    btnPdlDown = new JLabel(icon3);
    btnPdlUp.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        alterRatio(true);
      }
    });
    btnPdlDown.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        alterRatio(false);
      }
    });
    tempFlowPanel.add(btnPdlUp);
    tempFlowPanel.add(ratio);
    tempFlowPanel.add(btnPdlDown);
    tempFlowPanel.add(new JLabel("Credits"));

    constraints.gridy = 4;
    backPanel.add(tempFlowPanel, constraints);
    constraints.gridy = 5;
    constraints.gridwidth = 3;
    backPanel.add(btnPdl, constraints);

    constraints.gridy = 6;
    backPanel.add(new JLabel(" "), constraints);
    constraints.gridx = 0;
    constraints.gridy = 7;
    constraints.gridheight = 1;
    constraints.gridwidth = 3;
    JPanel tempPanel2 = new JPanel();
    tempPanel2.setLayout(new BorderLayout());
    JLabel label = new JLabel("Automatischer Powerdownload");
    label.setForeground(Color.white);
    label.setOpaque(true);
    label.setBackground(Color.blue);
    tempPanel2.add(label, BorderLayout.CENTER);
    JLabel btnHint2 = new JLabel(icon);
    tempPanel2.add(btnHint2, BorderLayout.EAST);
    backPanel.add(tempPanel2, constraints);

    constraints.gridwidth = 1;
    constraints.gridy = 8;
    ButtonGroup buttonGroup2 = new ButtonGroup();
    buttonGroup2.add(btnAutoInaktiv);
    buttonGroup2.add(btnAutoAktiv);
    btnAutoInaktiv.setText("inaktiv");
    btnAutoInaktiv.setSelected(true);
    btnAutoAktiv.setText("aktiv");
    backPanel.add(btnAutoInaktiv, constraints);
    constraints.gridy = 9;
    backPanel.add(btnAutoAktiv, constraints);
    constraints.gridy = 10;
    JPanel panel = new JPanel(new FlowLayout());
    panel.add(new JLabel("ab "));
    autoAb.setDocument(new NumberInputVerifier());
    autoAb.setPreferredSize(new Dimension(40, 21));
    autoAb.setText("200");
    panel.add(autoAb);
    panel.add(new JLabel("MB"));
    backPanel.add(panel, constraints);
    constraints.gridy = 11;
    constraints.gridwidth = 3;
    backPanel.add(btnAutoPdl, constraints);
    add(backPanel, BorderLayout.NORTH);
  }

  private void executeHint() {

  }

  private void alterRatio(boolean increase) {
    String temp = ratio.getText();
    int ganzZahl = Integer.parseInt(temp.substring(0, 1));
    int nachKomma = Integer.parseInt(temp.substring(2, 3));
    if (increase) {
      if (ratioWert < 5.5f) {
        if (nachKomma == 9) {
          nachKomma = 0;
          ganzZahl += 1;
        }
        else {
          nachKomma += 1;
        }
      }
      else {
        return;
      }
    }
    else {
      if (ratioWert > 2.2f) {
        if (nachKomma == 0) {
          nachKomma = 9;
          ganzZahl -= 1;
        }
        else {
          nachKomma -= 1;
        }
      }
      else {
        return;
      }
    }
    ratio.setText(ganzZahl + "." + nachKomma);
    ratioWert = Float.parseFloat(ratio.getText());
  }

  void btnPdl_actionPerformed(ActionEvent e) {
    DataManager.getInstance().getDownloads();
  }
}
