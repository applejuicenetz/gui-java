package de.applejuice.client.gui;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JButton;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class DownloadPanel extends JPanel {
  private JTextField downloadLink = new JTextField();
  private JButton btnStartDownload = new JButton("Download");
  private PowerDownloadPanel powerDownloadPanel = new PowerDownloadPanel();

  public DownloadPanel() {
    try {
      jbInit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    setLayout(new GridBagLayout());
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.anchor= GridBagConstraints.NORTH;
    constraints.fill = GridBagConstraints.BOTH;
    constraints.gridx=0;
    constraints.gridy=0;
    constraints.gridwidth=1;
    constraints.gridheight=1;
    add(new JLabel("ajfsp-Link hinzufügen"), constraints);
    constraints.gridx=1;
    constraints.weightx=1;
    add(downloadLink, constraints);
    constraints.weightx=0;
    constraints.gridx=2;
    add(btnStartDownload, constraints);
    constraints.gridwidth=3;
    constraints.gridx=0;
    constraints.gridy=1;
    constraints.weighty=1;
    add(new JTable(), constraints);
    constraints.gridwidth=1;
    constraints.weighty=0;
    constraints.gridy=2;
    add(new JLabel(" "), constraints);
    constraints.gridy=3;
    add(powerDownloadPanel, constraints);
    constraints.gridwidth=2;
    constraints.gridx=1;
    add(new JTable(), constraints);
  }
}