package de.applejuicenet.client.gui;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;


/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class UploadPanel extends JPanel {
  private JTable uploadDataTable;
  private int anzahlClients = 0;
  private JLabel clients = new JLabel();

  public UploadPanel() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  private void jbInit() throws Exception {
    setLayout(new BorderLayout());
    uploadDataTable = new JTable();
    uploadDataTable.setModel(new UploadDataTableModel());
    JScrollPane aScrollPane = new JScrollPane();
    aScrollPane.getViewport().add(uploadDataTable);
    add(aScrollPane, BorderLayout.CENTER);

    JPanel panel = new JPanel();
    panel.setLayout(new FlowLayout());
    clients.setText(Integer.toString(anzahlClients));
    panel.add(clients);
    panel.add(new JLabel(" Clients in Deiner Uploadliste"));
    JPanel panel2 = new JPanel();
    panel2.setLayout(new BorderLayout());
    panel2.add(panel, BorderLayout.WEST);
    add(panel2, BorderLayout.SOUTH);
  }
}