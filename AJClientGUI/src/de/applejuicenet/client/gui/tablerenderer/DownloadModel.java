package de.applejuicenet.client.gui.tablerenderer;

import java.net.*;
import java.util.*;

import javax.swing.*;
import javax.swing.tree.*;

import de.applejuicenet.client.gui.controller.DataManager;
import java.awt.Toolkit;
import javax.swing.event.TreeModelListener;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.shared.exception.*;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.shared.dac.DownloadSourceDO;
import de.applejuicenet.client.shared.IconManager;

public class DownloadModel
    extends AbstractTreeTableModel
    implements TreeTableModel {

  static protected String[] cNames;

  static protected Class[] cTypes = {
      TreeTableModel.class, String.class, String.class, String.class, String.class,
      String.class, String.class, String.class, String.class, String.class};

  public void setNames(String[] names){
    cNames = names;
  }

  public DownloadModel() {
    super(new DownloadNode());
    loadHeader();
    HashMap downloads = DataManager.getInstance().getDownloads();
    Iterator it = downloads.values().iterator();
    while (it.hasNext()){
      DownloadSourceDO download = (DownloadSourceDO) it.next();
      ( (DownloadNode) getRoot()).addChild(download);
    }
  }

  private void loadHeader(){
    LanguageSelector languageSelector = LanguageSelector.getInstance();
    cNames = new String[10];
    cNames[0] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "queue", "col0caption"}));
    cNames[1] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "queue", "col1caption"}));
    cNames[2] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "queue", "col2caption"}));
    cNames[3] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "queue", "col3caption"}));
    cNames[4] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "queue", "col4caption"}));
    cNames[5] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "queue", "col5caption"}));
    cNames[6] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "queue", "col6caption"}));
    cNames[7] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "queue", "col7caption"}));
    cNames[8] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "queue", "col8caption"}));
    cNames[9] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "queue", "col9caption"}));
  }

  protected DownloadSourceDO getDO(Object node) {
    DownloadNode fileNode = ( (DownloadNode) node);
    return fileNode.getDO();
  }

  protected Object[] getChildren(Object node) {
    DownloadNode fileNode = ( (DownloadNode) node);
    return fileNode.getChildren();
  }

  public int getChildCount(Object node) {
    Object[] children = getChildren(node);
    return (children == null) ? 0 : children.length;
  }

  public Object getChild(Object node, int i) {
    return getChildren(node)[i];
  }

  public int getColumnCount() {
    return cNames.length;
  }

  public String getColumnName(int column) {
    return cNames[column];
  }

  public Class getColumnClass(int column) {
    return cTypes[column];
  }

  public Object getValueAt(Object node, int column) {
    DownloadSourceDO download = getDO(node);
    if (download == null) {
      return "";
    }
    try {
      switch (column) {
        case 0:
          return download.getDateiname();
        case 1:
          return download.getStatus();
        case 2:
          return download.getGroesse();
        case 3:
          return download.getBereitsGeladen();
        case 4:
          return download.getGeschwindigkeit();
        case 5:
          return download.getRestlicheZeit();
        case 6:
          return download.getProzentGeladen();
        case 7:
          return download.getNochZuLaden();
        case 8:
          return download.getPowerdownload();
        case 9:
          if (download.getVersion()!=null)
            return download.getVersion().getVersion();
          else
            return "";
        default:
          return "";
      }
    }
    catch (SecurityException se) {}

    return null;
  }
}

class DownloadNode
    extends DefaultMutableTreeNode {
  private static ImageIcon rootIcon;
  private static ImageIcon uebertrageIcon;
  private static ImageIcon warteschlangeIcon;
  private static ImageIcon indirektIcon;

  DownloadSourceDO download;
  HashSet children;

  public DownloadNode(DownloadSourceDO download) {
    initIcons();
    this.download = download;
    children = new HashSet();
    if (download.getSources() != null && download.getSources().length != 0) {
      for (int i = 0; i < download.getSources().length; i++) {
        children.add(new DownloadNode(download.getSources()[i]));
      }
    }
  }

  public DownloadNode() {
    initIcons();
    children = new HashSet();
  }

  private void initIcons() {
    if (rootIcon == null) {
      IconManager im = IconManager.getInstance();
      rootIcon = im.getIcon("treeRoot");
      uebertrageIcon = im.getIcon("treeUebertrage");
      warteschlangeIcon = im.getIcon("treeWarteschlange");
      indirektIcon = im.getIcon("treeIndirekt");
    }
  }

  public Icon getConvenientIcon() {
    if (children.size()!=0) {
      return rootIcon;
    }
    if (download.getIntStatus() == DownloadSourceDO.UEBERTRAGE) {
      return uebertrageIcon;
    }
    else if (download.getIntStatus() == DownloadSourceDO.VERSUCHEINDIREKT) {
      return indirektIcon;
    }
    else if (download.getIntStatus() == DownloadSourceDO.WARTESCHLANGE) {
      return warteschlangeIcon;
    }
    else if (download.getIntStatus() == DownloadSourceDO.ROOT) {
      return rootIcon;
    }
    else {
      return rootIcon;
    }
  }

  public void addChild(DownloadSourceDO download) {
    children.add(new DownloadNode(download));
  }

  public DownloadSourceDO getDO() {
    return download;
  }

  public String toString() {
    if (download == null) {
      return "";
    }
    return download.getDateiname();
  }

  protected Object[] getChildren() {
    return children.toArray(new DownloadNode[children.size()]);
  }
}
