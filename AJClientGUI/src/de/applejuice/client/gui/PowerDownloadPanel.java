package de.applejuice.client.gui;

import javax.swing.JPanel;
import java.awt.*;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class PowerDownloadPanel extends JPanel {
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  private JRadioButton btnInaktiv = new JRadioButton();
  private JRadioButton btnAktiv = new JRadioButton();

  public PowerDownloadPanel() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  private void jbInit() throws Exception {
    setLayout(gridBagLayout1);
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.anchor= GridBagConstraints.NORTH;
    constraints.fill = GridBagConstraints.BOTH;
    constraints.gridx=0;
    constraints.gridy=0;
    constraints.gridheight=1;
    constraints.gridwidth=3;
    add(new JLabel("Powerdownload"), constraints);
    constraints.gridy=1;
    add(new JLabel("Wieviel willst Du maximal für 1 Byte bezahlen?"), constraints);
    constraints.gridwidth=1;
    constraints.gridy=2;
    ButtonGroup buttonGroup = new ButtonGroup();
    buttonGroup.add(btnInaktiv);
    buttonGroup.add(btnAktiv);
    btnInaktiv.setText("Powerdownload inaktiv");
    btnInaktiv.setSelected(true);
    btnAktiv.setText("Powerdownload aktiv");
    add(btnInaktiv, constraints);
    constraints.gridy=3;
    add(btnAktiv, constraints);
  }
}