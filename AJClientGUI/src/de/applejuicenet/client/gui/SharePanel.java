package de.applejuicenet.client.gui;

import java.util.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.border.*;
import javax.swing.table.*;

import de.applejuicenet.client.gui.controller.*;
import de.applejuicenet.client.gui.listener.*;
import de.applejuicenet.client.gui.tables.share.ShareTableCellRenderer;
import de.applejuicenet.client.shared.*;
import de.applejuicenet.client.shared.exception.NodeAlreadyExistsException;
import de.applejuicenet.client.shared.dac.ShareDO;
import de.applejuicenet.client.gui.tables.JTreeTable;
import de.applejuicenet.client.gui.tables.share.ShareModel;
import de.applejuicenet.client.gui.tables.share.ShareNode;
import de.applejuicenet.client.gui.trees.share.ShareSelectionTreeModel;
import de.applejuicenet.client.gui.trees.share.ShareSelectionTreeCellRenderer;
import de.applejuicenet.client.gui.trees.WaitNode;

import java.awt.event.*;
import java.io.File;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/SharePanel.java,v 1.22 2003/08/24 14:59:59 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: SharePanel.java,v $
 * Revision 1.22  2003/08/24 14:59:59  maj0r
 * Version 0.14
 * Diverse Aenderungen.
 *
 * Revision 1.21  2003/08/22 11:34:43  maj0r
 * WarteNode eingefuegt.
 *
 * Revision 1.20  2003/08/20 07:49:50  maj0r
 * Programmstart beschleunigt.
 *
 * Revision 1.19  2003/08/15 14:46:30  maj0r
 * Refactoring.
 *
 * Revision 1.18  2003/08/14 20:08:42  maj0r
 * Tree fuer Shareauswahl eingefuegt, aber noch nicht fertiggestellt.
 *
 * Revision 1.17  2003/08/04 14:28:55  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.16  2003/08/03 19:54:05  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.15  2003/08/02 12:03:38  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.14  2003/07/04 11:32:18  maj0r
 * Anzeige der Anzahl der Dateien und Gesamtgöße des Shares hinzugefügt.
 *
 * Revision 1.13  2003/07/02 13:54:34  maj0r
 * JTreeTable komplett überarbeitet.
 *
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
    implements LanguageListener, RegisterI{
  private JPanel panelWest;
  private JPanel panelCenter;
  private JButton addFolderWithSubfolder = new JButton();
  private JButton addFolderWithoutSubfolder = new JButton();
  private JButton removeFolder = new JButton();
  private JButton startCheck = new JButton();
  private JList folderList = new JList(new DefaultListModel());
  private JTree folderTree = new JTree();
  private TitledBorder titledBorder1;
  private TitledBorder titledBorder2;
  private JLabel dateien = new JLabel();

  private JButton neueListe = new JButton();
  private JButton neuLaden = new JButton();
  private JButton prioritaetSetzen = new JButton();
  private JButton prioritaetAufheben = new JButton();
  private JComboBox cmbPrio = new JComboBox();
  private AJSettings ajSettings;

  private JTreeTable shareTable;
  private ShareModel shareModel;

  private String eintraege;

  private int anzahlDateien = 0;
  private String dateiGroesse = "0 MB";
  private boolean treeInitialisiert = false;

  public SharePanel() {
    try {
      jbInit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    //todo
    addFolderWithSubfolder.setEnabled(false);
    addFolderWithoutSubfolder.setEnabled(false);
    startCheck.setEnabled(false);
    neueListe.setEnabled(false);
    prioritaetSetzen.setEnabled(false);
    prioritaetAufheben.setEnabled(false);

    shareModel = new ShareModel(new ShareNode(null, "/"));
    shareTable = new JTreeTable(shareModel);
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
          ShareNode rootNode = shareModel.getRootNode();
          rootNode.removeAllChildren();
          ArrayList sharesArray = new ArrayList();
          HashSet shareDirs = ajSettings.getShareDirs();
          Iterator it = shareDirs.iterator();
          ShareNode directoryNode = null;
          String pfad;
          while (it.hasNext()){
              pfad = ((ShareEntry)it.next()).getDir();
              sharesArray.add(pfad);
              try {
                  directoryNode = new ShareNode(rootNode, pfad);
                  rootNode.addDirectory(directoryNode);
              }
              catch (NodeAlreadyExistsException e) {
                  //Schon da, also brauchts den auch nicht.
              }
          }
          HashMap shares = ApplejuiceFassade.getInstance().getShare(true);
          Iterator iterator = shares.values().iterator();
          int anzahlDateien = 0;
          double size = 0;
          int anzahlArray;
          ShareDO shareDO;
          String filename;
          String path;
          ShareNode superParentNode;
          ShareNode parentNode;
          while (iterator.hasNext()){
              shareDO = (ShareDO)iterator.next();
              filename = shareDO.getFilename();
              path = filename.substring(0, filename.lastIndexOf(File.separator));
              parentNode = ShareNode.getNodeByPath(path);
              if (parentNode!=null){
                  parentNode.addChild(shareDO);
              }
              else{
                  anzahlArray = sharesArray.size();
                  for (int i=0; i<anzahlArray; i++){
                      if (path.indexOf((String)sharesArray.get(i))!=-1){
                          path = path.substring(((String)sharesArray.get(i)).length());
                          superParentNode = ShareNode.getNodeByPath((String)sharesArray.get(i));
                          parentNode = ShareNode.getNodeByPath(path);
                          if (parentNode!=null){
                              parentNode.addChild(shareDO);
                          }
                          else{
                              try {
                                  ShareNode neuesDirectory = new ShareNode(rootNode, path);
                                  superParentNode.addDirectory(neuesDirectory);
                                  neuesDirectory.addChild(shareDO);
                              } catch (NodeAlreadyExistsException e) {
                                  e.printStackTrace();  //To change body of catch statement use Options | File Templates.
                              }
                          }
                          break;
                      }
                  }
              }
              size += Long.parseLong(shareDO.getSize());
              anzahlDateien++;
          }
        size = size / 1048576;
        dateiGroesse = Double.toString(size);
        if (dateiGroesse.indexOf(".") + 3 < dateiGroesse.length()){
            dateiGroesse = dateiGroesse.substring(0, dateiGroesse.indexOf(".") + 3) + " MB";
        }
          String temp = eintraege;
          temp = temp.replaceFirst("%i", Integer.toString(anzahlDateien));
          temp = temp.replaceFirst("%s", dateiGroesse);
          dateien.setText( temp);
          shareTable.updateUI();
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
    panelWest.add(new JScrollPane(folderTree), constraints);

    constraints.gridy = 3;
    panelWest.add(removeFolder, constraints);

    constraints.gridy = 4;
    panelWest.add(startCheck, constraints);

    constraints.gridy = 5;
    constraints.weighty = 1;
    panelWest.add(new JScrollPane(folderList), constraints);

    for (int i = 1; i <= 250; i++) {
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
    panelCenter.add(dateien, BorderLayout.SOUTH);

    removeFolder.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent ae){
            entferneOrdner();
        }
    });
    add(panelWest, BorderLayout.WEST);
    add(panelCenter, BorderLayout.CENTER);

    TableColumn tc = shareTable.getColumnModel().getColumn(1);
    tc.setCellRenderer(new ShareTableCellRenderer());

    LanguageSelector.getInstance().addLanguageListener(this);
  }

  private void entferneOrdner(){
      HashSet shares = ajSettings.getShareDirs();
      Object[] selected = folderList.getSelectedValues();
      if (selected!=null && selected.length!=0){
          removeFolder.setEnabled(false);
          for (int i=0; i<selected.length; i++){
              shares.remove(selected[i]);
          }
          ApplejuiceFassade.getInstance().setShare(shares);
          initShareList();
      }
  }

  private void initShareSelectionTree(){
      folderTree.setModel(new DefaultTreeModel(new WaitNode()));
      folderTree.setCellRenderer(new ShareSelectionTreeCellRenderer());
      final SwingWorker worker2 = new SwingWorker() {
                  public Object construct() {
                      ShareSelectionTreeModel treeModel = new ShareSelectionTreeModel();
                      folderTree.setModel(treeModel);
                      folderTree.setRootVisible(false);
                      return null;
                  }
              };
      worker2.start();
  }

  private void initShareList(){
      final SwingWorker worker = new SwingWorker() {
                  public Object construct() {
                      removeFolder.setEnabled(false);
                      ajSettings = ApplejuiceFassade.getInstance().getAJSettings();
                      ( (DefaultListModel) folderList.getModel()).removeAllElements();
                      Iterator it = ajSettings.getShareDirs().iterator();
                      while (it.hasNext()) {
                        ShareEntry entry = (ShareEntry) it.next();
                        ( (DefaultListModel) folderList.getModel()).addElement(entry);
                      }
                      removeFolder.setEnabled(true);
                      return null;
                  }
              };
      worker.start();
  }

  public void registerSelected() {
    if (!treeInitialisiert){
        treeInitialisiert = true;
        initShareList();
        initShareSelectionTree();
    }
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

    eintraege = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"javagui", "shareform", "anzahlShare"}));
    if (anzahlDateien>0){
        String temp = eintraege;
        temp = temp.replaceFirst("%i", Integer.toString(anzahlDateien));
        temp = temp.replaceFirst("%s", dateiGroesse);
        dateien.setText(temp);
    }
    else{
        dateien.setText("");
    }
  }
}