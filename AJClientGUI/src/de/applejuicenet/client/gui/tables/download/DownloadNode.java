package de.applejuicenet.client.gui.tables.download;

import de.applejuicenet.client.shared.dac.DownloadSourceDO;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.gui.tablerenderer.Node;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.*;
import java.util.HashMap;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/download/Attic/DownloadNode.java,v 1.1 2003/07/01 18:34:28 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DownloadNode.java,v $
 * Revision 1.1  2003/07/01 18:34:28  maj0r
 * Struktur verändert.
 *
 *
 */

public class DownloadNode
    extends DefaultMutableTreeNode implements Node {
  private static ImageIcon rootIcon;
  private static ImageIcon uebertrageIcon;
  private static ImageIcon warteschlangeIcon;
  private static ImageIcon indirektIcon;

  DownloadSourceDO download;
  HashMap children;

  public DownloadNode(DownloadSourceDO download) {
    initIcons();
    this.download = download;
    children = new HashMap();
    if (download.getSources() != null && download.getSources().length != 0) {
      for (int i = 0; i < download.getSources().length; i++) {
        children.put(download.getSources()[i].getId(),
                     new DownloadNode(download.getSources()[i]));
      }
    }
  }

  public DownloadNode() {
    initIcons();
    children = new HashMap();
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
    if (children.size() != 0) {
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

  public DownloadNode addChild(DownloadSourceDO download) {
    DownloadNode childNode = new DownloadNode(download);
    children.put(download.getId(), childNode);
    return childNode;
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

  public HashMap getChildrenMap() {
    return children;
  }

  protected Object[] getChildren() {
    return children.values().toArray(new DownloadNode[children.size()]);
  }
}
