package de.applejuicenet.client.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import de.applejuicenet.client.gui.controller.*;
import de.applejuicenet.client.gui.listener.*;
import de.applejuicenet.client.shared.*;
import de.applejuicenet.client.shared.exception.*;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class ServerPanel
    extends JPanel
    implements LanguageListener {
  private JTable serverTable;
  private JLabel sucheServer = new JLabel(
      "<html><font><u>mehr Server gibt es hier</u></font></html>");

  public ServerPanel() {
    try {
      jbInit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    setLayout(new BorderLayout());
    LanguageSelector.getInstance().addLanguageListener(this);
    JPanel panel1 = new JPanel();
    panel1.setLayout(new GridBagLayout());
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.anchor = GridBagConstraints.NORTH;
    constraints.fill = GridBagConstraints.BOTH;
    constraints.gridx = 0;
    constraints.gridy = 0;

    sucheServer.setForeground(Color.blue);
    sucheServer.addMouseListener(new MouseAdapter() {
      public void mouseExited(MouseEvent e) {
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
      }

      public void mouseEntered(MouseEvent e) {
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

  public void fireLanguageChanged() {
    try {
      LanguageSelector languageSelector = LanguageSelector.getInstance();
      sucheServer.setText("<html><font><u>" +
                          ZeichenErsetzer.korrigiereUmlaute(languageSelector.
          getFirstAttrbuteByTagName("mainform", "Label11", "caption")) +
                          "</u></font></html>");
    }
    catch (LanguageSelectorNotInstanciatedException ex) {
      ex.printStackTrace();
    }
  }
}