package de.applejuice.client.gui;

import javax.swing.*;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.FlowLayout;
import java.awt.BorderLayout;


/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class SearchPanel extends JPanel {
  JTable searchResultTable = new JTable();
  JButton btnStartStopSearch = new JButton("Suche starten");
  JTextField suchbegriff = new JTextField();
  int anzahlSuchanfragen = 0;
  JLabel suchanfragen = new JLabel();

  public SearchPanel() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  private void jbInit() throws Exception {
    setLayout(new BorderLayout());
    JPanel panel3 = new JPanel();
    JPanel leftPanel = new JPanel();
    panel3.setLayout(new GridBagLayout());
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.anchor = GridBagConstraints.NORTH;
    constraints.fill = GridBagConstraints.BOTH;
    constraints.gridx = 0;
    constraints.gridy = 0;
    constraints.gridwidth = 1;
    constraints.gridheight = 1;
    panel3.add(new JLabel("Suchbegriff: "), constraints);
    constraints.gridx = 1;
    panel3.add(suchbegriff, constraints);
    constraints.gridy = 1;
    panel3.add(btnStartStopSearch, constraints);
    JPanel panel2 = new JPanel();
    panel2.setLayout(new FlowLayout());
    suchanfragen.setText(Integer.toString(anzahlSuchanfragen));
    panel2.add(suchanfragen);
    panel2.add(new JLabel(" Suchanfragen in Bearbeitung"));
    constraints.gridx = 0;
    constraints.gridy = 2;
    constraints.gridwidth = 2;
    panel3.add(panel2, constraints);
    leftPanel.setLayout(new BorderLayout());
    leftPanel.add(panel3, BorderLayout.NORTH);
    add(leftPanel, BorderLayout.WEST);

    searchResultTable.setModel(new SearchResultTableModel());
    JScrollPane aScrollPane = new JScrollPane();
    aScrollPane.getViewport().add(searchResultTable);
    add(aScrollPane, BorderLayout.CENTER);
  }
}