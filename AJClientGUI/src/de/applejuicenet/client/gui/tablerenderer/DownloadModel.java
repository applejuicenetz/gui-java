package de.applejuicenet.client.gui.tablerenderer;

import java.net.*;
import java.util.*;

import javax.swing.*;
import javax.swing.tree.*;

import de.applejuicenet.client.shared.*;
import de.applejuicenet.client.gui.controller.DataManager;
import java.awt.Toolkit;
import javax.swing.event.TreeModelListener;

public class DownloadModel
    extends AbstractTreeTableModel
    implements TreeTableModel {

  static protected String[] cNames = {
      "Dateiname", "Status", "Größe", "Bereits geladen", "Prozent geladen",
      "Noch zu laden", "Geschwindigkeit", "Restzeit", "Powerdownload", "Client"};

  static protected Class[] cTypes = {
      TreeTableModel.class, String.class, String.class, String.class, String.class,
      String.class, String.class, String.class, String.class, String.class};

  public DownloadModel() {
    super(new DownloadNode());
    DownloadSourceDO[] downloads = DataManager.getInstance().getDownloads();
    for (int i = 0; i < downloads.length; i++) {
      ( (DownloadNode) getRoot()).addChild(downloads[i]);
    }
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
          return download.getProzentGeladen();
        case 5:
          return download.getNochZuLaden();
        case 6:
          return download.getGeschwindigkeit();
        case 7:
          return download.getRestlicheZeit();
        case 8:
          return download.getPowerdownload();
        case 9:
          return download.getVersion().getVersion();
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
      URL url = getClass().getResource("treeRoot.gif");
      rootIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(url));
      url = getClass().getResource("treeUebertrage.gif");
      uebertrageIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(url));
      url = getClass().getResource("treeWarteschlange.gif");
      warteschlangeIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(url));
      url = getClass().getResource("treeIndirekt.gif");
      indirektIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(url));
    }
  }

  public ImageIcon getConvenientIcon(boolean expanded) {
    if (download == null) {
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
