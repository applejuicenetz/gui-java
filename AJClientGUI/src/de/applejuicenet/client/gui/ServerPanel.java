package de.applejuicenet.client.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import de.applejuicenet.client.gui.controller.*;
import de.applejuicenet.client.gui.listener.*;
import de.applejuicenet.client.shared.*;
import de.applejuicenet.client.shared.exception.*;
import javax.swing.table.TableColumnModel;
import java.util.HashMap;

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
    implements LanguageListener, DataUpdateListener, RegisterI {
  private JTable serverTable;
  private JLabel sucheServer = new JLabel(
      "<html><font><u>mehr Server gibt es hier</u></font></html>");
  private JPopupMenu popup = new JPopupMenu();
  JMenuItem item1;
  JMenuItem item2;
  JMenuItem item3;
  JMenuItem item4;
  JMenuItem item5;
  JMenuItem item6;

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

    item1 = new JMenuItem("Abbrechen");
    item2 = new JMenuItem("Pause/Fortsetzen");
    item3 = new JMenuItem("Powerdownload");
    item4 = new JMenuItem("Umbenennen");
    item5 = new JMenuItem("Zielordner ändern");
    item6 = new JMenuItem("Fertige Übertragungen entfernen");
    popup.add(item1);
    popup.add(item2);
    popup.add(item3);
    popup.add(item4);
    popup.add(item5);
    popup.add(item6);

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

      public void mouseClicked(MouseEvent e){
        //to do
      }
    });
    panel1.add(sucheServer, constraints);
    constraints.gridx = 1;
    constraints.weightx = 1;
    panel1.add(new JLabel(), constraints);
    add(panel1, BorderLayout.NORTH);
    serverTable = new JTable();
    serverTable.setModel(new ServerTableModel(DataManager.getInstance().getAllServer()));
    serverTable.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        if (e.isPopupTrigger()) {
          popup.show(serverTable, e.getX(), e.getY());
        }
      }
    });
    JScrollPane aScrollPane = new JScrollPane();
    aScrollPane.getViewport().add(serverTable);
    add(aScrollPane, BorderLayout.CENTER);
    DataManager.getInstance().addServerListener(this);
  }

  public void registerSelected(){
    DataManager.getInstance().updateServer();
  }

  public void fireContentChanged(HashMap changedContent){
    ((ServerTableModel)serverTable.getModel()).setTable(changedContent);
  }

  public void fireLanguageChanged() {
    LanguageSelector languageSelector = LanguageSelector.getInstance();
    sucheServer.setText("<html><font><u>" +
                        ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "Label11", "caption"})) +
                        "</u></font></html>");
    String[] columns = new String[4];
    columns[0] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "serverlist" ,"col0caption"}));
    columns[1] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "serverlist" ,"col1caption"}));
    columns[2] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "serverlist" ,"col4caption"}));
    columns[3] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "serverlist" ,"col5caption"}));
    item1.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "canceldown" ,"caption"})));
    String temp = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "pausedown" ,"caption"}));
    temp+="/" + ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "resumedown" ,"caption"}));
    item2.setText(temp);
    item3.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "powerdownload" ,"caption"})));
    item4.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "renamefile" ,"caption"})));
    item5.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "changetarget" ,"caption"})));
    item6.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "Clearfinishedentries1" ,"caption"})));

    TableColumnModel tcm = serverTable.getColumnModel();
    for (int i=0; i<tcm.getColumnCount(); i++){
      tcm.getColumn(i).setHeaderValue(columns[i]);
    }
  }
}