package de.applejuicenet.client.gui;

import java.util.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import de.applejuicenet.client.gui.controller.*;
import de.applejuicenet.client.gui.listener.*;
import de.applejuicenet.client.gui.tables.share.ShareTableCellRenderer;
import de.applejuicenet.client.gui.tables.share.ShareTableModel;
import de.applejuicenet.client.shared.*;
import java.awt.event.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/SharePanel.java,v 1.12 2003/07/01 18:41:39 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: SharePanel.java,v $
 * Revision 1.12  2003/07/01 18:41:39  maj0r
 * Struktur verändert.
 *
 * Revision 1.11  2003/07/01 18:33:53  maj0r
 * Sprachauswahl eingearbeitet.
 *
 * Revision 1.10  2003/06/22 19:01:55  maj0r
 * Laden des Shares nun erst nach Betätigen des Buttons "Erneut laden".
 *
 * Revision 1.9  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class SharePanel
    extends JPanel
    implements LanguageListener, RegisterI, DataUpdateListener {
  private JPanel panelWest;
  private JPanel panelCenter;
  private JButton addFolderWithSubfolder = new JButton();
  private JButton addFolderWithoutSubfolder = new JButton();
  private JButton removeFolder = new JButton();
  private JButton startCheck = new JButton();
  private JList folderList = new JList(new DefaultListModel());
  TitledBorder titledBorder1;
  TitledBorder titledBorder2;

  private JButton neueListe = new JButton();
  private JButton neuLaden = new JButton();
  private JButton prioritaetSetzen = new JButton();
  private JButton prioritaetAufheben = new JButton();
  private JComboBox cmbPrio = new JComboBox();
  private AJSettings ajSettings;

  JTable shareTable;

  public SharePanel() {
    try {
      jbInit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    ajSettings = DataManager.getInstance().getAJSettings();
    Iterator it = ajSettings.getShareDirs().iterator();
    while (it.hasNext()) {
      ShareEntry entry = (ShareEntry) it.next();
      ( (DefaultListModel) folderList.getModel()).addElement(entry);
    }

    shareTable = new JTable();
    titledBorder1 = new TitledBorder("Test");
    titledBorder2 = new TitledBorder("Tester");
    setLayout(new BorderLayout());
    panelWest = new JPanel(new GridBagLayout());
    panelWest.setBorder(titledBorder1);
    panelCenter = new JPanel(new BorderLayout());
    panelCenter.setBorder(titledBorder2);

    neueListe.setIcon(IconManager.getInstance().getIcon("treeRoot"));
    neuLaden.setIcon(IconManager.getInstance().getIcon("erneuern"));
    neuLaden.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent ae){
        ShareTableModel tableModel = (ShareTableModel)shareTable.getModel();
        tableModel.setTable(DataManager.getInstance().getShare());
      }
    });
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.anchor = GridBagConstraints.NORTH;
    constraints.fill = GridBagConstraints.BOTH;
    constraints.gridx = 0;
    constraints.gridy = 0;

    cmbPrio.setEditable(true);
    panelWest.add(addFolderWithSubfolder, constraints);

    constraints.gridy = 1;
    panelWest.add(addFolderWithoutSubfolder, constraints);

    constraints.gridy = 2;
    panelWest.add(removeFolder, constraints);

    constraints.gridy = 3;
    panelWest.add(startCheck, constraints);

    constraints.gridy = 4;
    constraints.weighty = 1;
    panelWest.add(folderList, constraints);

    for (int i = 0; i <= 250; i++) {
      cmbPrio.addItem(Integer.toString(i));
    }
    JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel1.add(neueListe);
    panel1.add(neuLaden);
    panel1.add(cmbPrio);
    panel1.add(prioritaetSetzen);
    panel1.add(prioritaetAufheben);

    panelCenter.add(panel1, BorderLayout.NORTH);
    panelCenter.add(new JScrollPane(shareTable), BorderLayout.CENTER);

    add(panelWest, BorderLayout.WEST);
    add(panelCenter, BorderLayout.CENTER);

    shareTable.setModel(new ShareTableModel(null));
    TableColumn tc = shareTable.getColumnModel().getColumn(1);
    tc.setCellRenderer(new ShareTableCellRenderer());

    LanguageSelector.getInstance().addLanguageListener(this);
//    DataManager.getInstance().addShareListener(this);
  }

  public void registerSelected() {
//    HashMap shares = DataManager.getInstance().getShare();
  }

  public void fireLanguageChanged() {
    LanguageSelector languageSelector = LanguageSelector.getInstance();
    titledBorder1.setTitle(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "dirssheet",
                                  "caption"})));
    titledBorder2.setTitle(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "filessheet",
                                  "caption"})));
    addFolderWithSubfolder.setText(ZeichenErsetzer.korrigiereUmlaute(
        languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform",
        "addwsubdirsbtn", "caption"})));
    addFolderWithSubfolder.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(
        languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform",
        "addwsubdirsbtn", "hint"})));
    addFolderWithoutSubfolder.setText(ZeichenErsetzer.korrigiereUmlaute(
        languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform",
        "addosubdirsbtn", "caption"})));
    addFolderWithoutSubfolder.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(
        languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform",
        "addosubdirsbtn", "hint"})));
    removeFolder.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "deldirbtn",
                                  "caption"})));
    removeFolder.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(
        languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform",
        "deldirbtn", "hint"})));
    startCheck.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "startsharecheck",
                                  "caption"})));
    startCheck.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(
        languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform",
        "startsharecheck", "hint"})));

    neueListe.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "newfilelist",
                                  "caption"})));
    neueListe.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "newfilelist",
                                  "hint"})));
    neuLaden.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "sharereload",
                                  "caption"})));
    neuLaden.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "sharereload",
                                  "hint"})));
    prioritaetSetzen.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "setprio",
                                  "caption"})));
    prioritaetSetzen.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(
        languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform",
        "setprio", "hint"})));
    prioritaetAufheben.setText(ZeichenErsetzer.korrigiereUmlaute(
        languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform",
        "clearprio", "caption"})));
    prioritaetAufheben.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(
        languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform",
        "clearprio", "hint"})));

    String[] tableColumns = new String[3];
    tableColumns[0] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "sfiles",
                                  "col0caption"}));
    tableColumns[1] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "sfiles",
                                  "col1caption"}));
    tableColumns[2] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "sfiles",
                                  "col2caption"}));

    TableColumnModel tcm = shareTable.getColumnModel();
    for (int i = 0; i < 3; i++) {
      tcm.getColumn(i).setHeaderValue(tableColumns[i]);
    }
  }

  public void fireContentChanged(int type, Object content) {

  }
}