package de.applejuicenet.client.gui;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.*;


/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class ServerPanel extends JPanel {
  private JTable serverTable;

  public ServerPanel() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    setLayout(new BorderLayout());

    JLabel sucheServer = new JLabel("<html><font><u>mehr Server gibt es hier</u></font></html>");
    sucheServer.setForeground(Color.blue);
    add(sucheServer, BorderLayout.NORTH);
    serverTable = new JTable();
    serverTable.setModel(new ServerTableModel());
    JScrollPane aScrollPane = new JScrollPane();
    aScrollPane.getViewport().add(serverTable);
    add(aScrollPane, BorderLayout.CENTER);

  }
}