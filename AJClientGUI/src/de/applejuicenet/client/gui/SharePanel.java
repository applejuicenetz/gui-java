package de.applejuicenet.client.gui;

import de.applejuicenet.client.gui.listener.LanguageListener;
import javax.swing.*;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import java.awt.*;
import javax.swing.border.*;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.gui.controller.DataManager;
import java.util.HashMap;
import de.applejuicenet.client.gui.listener.DataUpdateListener;
import javax.swing.table.TableColumn;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class SharePanel extends JPanel implements LanguageListener, RegisterI, DataUpdateListener{
  private JPanel panelWest;
  private JPanel panelCenter;
  private JButton addFolderWithSubfolder = new JButton();
  private JButton addFolderWithoutSubfolder = new JButton();
  private JButton removeFolder = new JButton();
  private JButton startCheck = new JButton();
  private JList folderList = new JList();
  TitledBorder titledBorder1;
  TitledBorder titledBorder2;

  private JButton neueListe = new JButton();
  private JButton neuLaden = new JButton();
  private JButton prioritaetSetzen = new JButton();
  private JButton prioritaetAufheben = new JButton();
  JComboBox cmbPrio = new JComboBox();

  JTable shareTable;

  public SharePanel() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  private void jbInit() throws Exception {
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

    for (int i=0; i<=250; i++)
      cmbPrio.addItem(Integer.toString(i));

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

    shareTable.setModel(new ShareTableModel(DataManager.getInstance().getShare()));
    TableColumn tc = shareTable.getColumnModel().getColumn(1);
    tc.setCellRenderer(new ShareTableCellRenderer());

    LanguageSelector.getInstance().addLanguageListener(this);
//    DataManager.getInstance().addShareListener(this);
  }

  public void registerSelected(){
    HashMap shares = DataManager.getInstance().getShare();
  }

  public void fireLanguageChanged(){
    LanguageSelector languageSelector = LanguageSelector.getInstance();
    titledBorder1.setTitle(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "dirssheet", "caption"})));
    titledBorder2.setTitle(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "filessheet", "caption"})));
    addFolderWithSubfolder.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "addwsubdirsbtn", "caption"})));
    addFolderWithSubfolder.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "addwsubdirsbtn", "hint"})));
    addFolderWithoutSubfolder.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "addosubdirsbtn", "caption"})));
    addFolderWithoutSubfolder.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "addosubdirsbtn", "hint"})));
    removeFolder.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "deldirbtn", "caption"})));
    removeFolder.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "deldirbtn", "hint"})));
    startCheck.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "startsharecheck", "caption"})));
    startCheck.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "startsharecheck", "hint"})));

    neueListe.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "newfilelist", "caption"})));
    neueListe.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "newfilelist", "hint"})));
    neuLaden.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "sharereload", "caption"})));
    neuLaden.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "sharereload", "hint"})));
    prioritaetSetzen.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "setprio", "caption"})));
    prioritaetSetzen.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "setprio", "hint"})));
    prioritaetAufheben.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "clearprio", "caption"})));
    prioritaetAufheben.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "clearprio", "hint"})));
  }

  public void fireContentChanged(int type, HashMap content){

  }
}