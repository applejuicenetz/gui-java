package de.applejuicenet.client.gui;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.shared.exception.LanguageSelectorNotInstanciatedException;
import de.applejuicenet.client.gui.listener.LanguageListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import de.applejuicenet.client.gui.controller.DataManager;
import de.applejuicenet.client.gui.listener.DataUpdateListener;
import java.util.HashMap;


/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class UploadPanel extends JPanel implements LanguageListener, RegisterI, DataUpdateListener{
  private JTable uploadDataTable;
  private int anzahlClients = 0;
  private JLabel label1 = new JLabel("0 Clients in Deiner Uploadliste");

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
    LanguageSelector.getInstance().addLanguageListener(this);
    uploadDataTable = new JTable();
    uploadDataTable.setModel(new UploadDataTableModel());
    JScrollPane aScrollPane = new JScrollPane();
    aScrollPane.getViewport().add(uploadDataTable);
    add(aScrollPane, BorderLayout.CENTER);

    JPanel panel = new JPanel();
    panel.setLayout(new FlowLayout());
    panel.add(label1);
    JPanel panel2 = new JPanel();
    panel2.setLayout(new BorderLayout());
    panel2.add(panel, BorderLayout.WEST);
    add(panel2, BorderLayout.SOUTH);
    DataManager.getInstance().addDataUpdateListener(this, DataUpdateListener.UPLOAD_CHANGED);
  }

  public void fireLanguageChanged() {
    LanguageSelector languageSelector = LanguageSelector.getInstance();
    String temp = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "uplcounttext"}));
    label1.setText(temp.replaceAll("%d", Integer.toString(anzahlClients)));
    String[] columns = new String[6];
    columns[0] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "uploads" ,"col0caption"}));
    columns[1] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "uploads" ,"col1caption"}));
    columns[2] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "uploads" ,"col2caption"}));
    columns[3] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "uploads" ,"col3caption"}));
    columns[4] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "uploads" ,"col4caption"}));
    columns[5] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "uploads" ,"col5caption"}));
    TableColumnModel tcm = uploadDataTable.getColumnModel();
    for (int i=0; i<tcm.getColumnCount(); i++){
      tcm.getColumn(i).setHeaderValue(columns[i]);
    }
  }

  public void fireContentChanged(int type, Object content){
    if (type != DataUpdateListener.UPLOAD_CHANGED || !(content instanceof HashMap))
      return;
    ((UploadDataTableModel)uploadDataTable.getModel()).setTable((HashMap)content);
  }

  public void registerSelected(){
//    DataManager.getInstance().updateModifiedXML();
  }
}