package de.applejuicenet.client.gui;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.*;
import java.awt.Cursor;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;


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
    JPanel panel1 = new JPanel();
    panel1.setLayout(new GridBagLayout());
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.anchor = GridBagConstraints.NORTH;
    constraints.fill = GridBagConstraints.BOTH;
    constraints.gridx = 0;
    constraints.gridy = 0;

    JLabel sucheServer = new JLabel("<html><font><u>mehr Server gibt es hier</u></font></html>");
    sucheServer.setForeground(Color.blue);
    sucheServer.addMouseListener(new MouseAdapter(){
       public void mouseExited (MouseEvent e){
           setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
       }
       public void mouseEntered(MouseEvent e){
         setCursor(new Cursor(Cursor.HAND_CURSOR));
       }
    });
    panel1.add(sucheServer, constraints);
    constraints.gridx = 1;
    constraints.weightx = 1;
    panel1.add(new JLabel(), constraints);
    add(panel1, BorderLayout.NORTH);
    serverTable = new JTable();
    serverTable.setModel(new ServerTableModel());
    JScrollPane aScrollPane = new JScrollPane();
    aScrollPane.getViewport().add(serverTable);
    add(aScrollPane, BorderLayout.CENTER);

  }
}