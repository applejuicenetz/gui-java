package de.applejuicenet.client.gui;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.shared.exception.LanguageSelectorNotInstanciatedException;
import de.applejuicenet.client.gui.listener.LanguageListener;


/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class UploadPanel extends JPanel implements LanguageListener{
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
  }

  public void fireLanguageChanged() {
    try {
      LanguageSelector languageSelector = LanguageSelector.getInstance();

      String temp = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
          getFirstAttrbuteByTagName("mainform", "uplcounttext"));
      temp = temp.replaceAll("%d", Integer.toString(anzahlClients));
      label1.setText(temp);
    }
    catch (LanguageSelectorNotInstanciatedException ex) {
      ex.printStackTrace();
    }
  }
}