package de.applejuice.client.gui;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;

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
  private JTable downloadTable;

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
    constraints.gridwidth=3;
    constraints.gridheight=1;
    JPanel tempPanel = new JPanel();
    tempPanel.setLayout(new BorderLayout());
    tempPanel.add(new JLabel("ajfsp-Link hinzufügen"), BorderLayout.WEST);
    tempPanel.add(downloadLink, BorderLayout.CENTER);
    tempPanel.add(btnStartDownload, BorderLayout.EAST);
    add(tempPanel, constraints);
    constraints.gridwidth=3;
    constraints.gridx=0;
    constraints.gridy=1;
    constraints.weighty=1;
    constraints.weightx=1;
    downloadTable = new JTable();
    downloadTable.setModel(new DownloadDataTableModel());
    JScrollPane aScrollPane = new JScrollPane();
    aScrollPane.getViewport().add(downloadTable);
    add(aScrollPane, constraints);
    constraints.weightx=0;
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