package de.applejuicenet.client.gui.tables.download;

import java.util.*;

import javax.swing.*;
import javax.swing.tree.*;

import de.applejuicenet.client.gui.controller.*;
import de.applejuicenet.client.gui.tablerenderer.AbstractTreeTableModel;
import de.applejuicenet.client.gui.tablerenderer.TreeTableModel;
import de.applejuicenet.client.shared.*;
import de.applejuicenet.client.shared.dac.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/download/Attic/DownloadModel.java,v 1.1 2003/07/01 18:34:28 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DownloadModel.java,v $
 * Revision 1.1  2003/07/01 18:34:28  maj0r
 * Struktur verändert.
 *
 * Revision 1.17  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class DownloadModel
    extends AbstractTreeTableModel
    implements TreeTableModel {

  static protected String[] cNames = {"", "", "", "", "", "", "", "", "", ""};

  static protected Class[] cTypes = {
      TreeTableModel.class, String.class, String.class, String.class, String.class,
      String.class, String.class, String.class, String.class, String.class};

  public DownloadModel() {
    super(new DownloadNode());
    HashMap downloads = DataManager.getInstance().getDownloads();
    Iterator it = downloads.values().iterator();
    while (it.hasNext()) {
      DownloadSourceDO download = (DownloadSourceDO) it.next();
      ( (DownloadNode) getRoot()).addChild(download);
    }
  }

  public void fillTree() {
    HashMap downloads = DataManager.getInstance().getDownloads();
    Iterator it = downloads.values().iterator();
    while (it.hasNext()) {
      DownloadSourceDO download = (DownloadSourceDO) it.next();
//      DownloadNode node = ((DownloadNode) getRoot()).getChildrenMap().get(download.getId());
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
          if (download.getVersion() != null) {
            return download.getVersion().getVersion();
          }
          else {
            return "";
          }
        default:
          return "";
      }
    }
    catch (SecurityException se) {}

    return null;
  }
}
