package de.applejuicenet.client.gui;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import java.awt.*;
import javax.swing.table.TableColumn;
import de.applejuicenet.client.gui.tablerenderer.JTreeTable;
import de.applejuicenet.client.shared.DownloadSourceDO;
import java.util.HashSet;
import de.applejuicenet.client.shared.Version;
import de.applejuicenet.client.gui.tablerenderer.DownloadModel;

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
  private JTreeTable downloadTable;
  private JTable actualDlOverviewTable = new JTable();

  public DownloadPanel() {
    try {
      jbInit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    setLayout(new BorderLayout());
    JPanel topPanel = new JPanel();
    topPanel.setLayout(new GridBagLayout());
    JPanel bottomPanel = new JPanel();
    bottomPanel.setLayout(new BorderLayout());

    GridBagConstraints constraints = new GridBagConstraints();
    constraints.anchor = GridBagConstraints.NORTH;
    constraints.fill = GridBagConstraints.BOTH;
    constraints.gridx = 0;
    constraints.gridy = 0;
    constraints.gridwidth = 3;
    constraints.gridheight = 1;
    JPanel tempPanel = new JPanel();
    tempPanel.setLayout(new BorderLayout());
    tempPanel.add(new JLabel("ajfsp-Link hinzufügen"), BorderLayout.WEST);
    tempPanel.add(downloadLink, BorderLayout.CENTER);
    tempPanel.add(btnStartDownload, BorderLayout.EAST);
    topPanel.add(tempPanel, constraints);
    constraints.gridwidth = 3;
    constraints.gridx = 0;
    constraints.gridy = 1;
    constraints.weighty = 1;
    constraints.weightx = 1;

    downloadTable = new JTreeTable(new DownloadModel());

    JScrollPane aScrollPane = new JScrollPane();
    aScrollPane.getViewport().add(downloadTable);
    topPanel.add(aScrollPane, constraints);

    bottomPanel.add(powerDownloadPanel, BorderLayout.WEST);
    JPanel tempPanel1 = new JPanel();
    tempPanel1.setLayout(new FlowLayout());
    JLabel blau = new JLabel("     ");
    blau.setOpaque(true);
    blau.setBackground(Color.blue);
    tempPanel1.add(blau);
    tempPanel1.add(new JLabel("Vorhanden"));

    JLabel red = new JLabel("     ");
    red.setOpaque(true);
    red.setBackground(Color.red);
    tempPanel1.add(red);
    tempPanel1.add(new JLabel("Nicht vorhanden"));

    JLabel black = new JLabel("     ");
    black.setOpaque(true);
    black.setBackground(Color.black);
    tempPanel1.add(black);
    tempPanel1.add(new JLabel("In Ordnung"));

    JLabel green = new JLabel("     ");
    green.setOpaque(true);
    green.setBackground(Color.green);
    tempPanel1.add(green);
    tempPanel1.add(new JLabel("Überprüft"));

    JPanel tempPanel2 = new JPanel();
    tempPanel2.setLayout(new BorderLayout());
    tempPanel2.add(tempPanel1, BorderLayout.NORTH);
    tempPanel2.add(actualDlOverviewTable, BorderLayout.CENTER);
    bottomPanel.add(tempPanel2, BorderLayout.CENTER);

    add(topPanel, BorderLayout.CENTER);
    add(bottomPanel, BorderLayout.SOUTH);
  }
}