package de.applejuicenet.client.gui.tables.download;

import java.util.*;

import javax.swing.*;
import javax.swing.tree.*;

import de.applejuicenet.client.gui.controller.*;
import de.applejuicenet.client.shared.*;
import de.applejuicenet.client.gui.tables.AbstractTreeTableModel;
import de.applejuicenet.client.gui.tables.TreeTableModel;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.shared.dac.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/download/Attic/DownloadModel.java,v 1.4 2003/07/03 19:11:16 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DownloadModel.java,v $
 * Revision 1.4  2003/07/03 19:11:16  maj0r
 * DownloadTable überarbeitet.
 *
 * Revision 1.3  2003/07/02 13:54:34  maj0r
 * JTreeTable komplett überarbeitet.
 *
 * Revision 1.2  2003/07/01 18:41:39  maj0r
 * Struktur verändert.
 *
 * Revision 1.1  2003/07/01 18:34:28  maj0r
 * Struktur verändert.
 *
 * Revision 1.17  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class DownloadModel
    extends AbstractTreeTableModel implements LanguageListener{

  static protected String[] cNames = {"", "", "", "", "", "", "", "", "", ""};

  static protected Class[] cTypes = {
      TreeTableModel.class, String.class, String.class, String.class, String.class,
      String.class, String.class, String.class, String.class, String.class};

  public DownloadModel() {
    super(new DownloadNode());
    LanguageSelector.getInstance().addLanguageListener(this);
  }

  protected Object[] getChildren(Object node) {
    DownloadNode downloadNode = ( (DownloadNode) node);
    return downloadNode.getChildren();
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
    DownloadNode downloadNode = (DownloadNode)node;
    if (downloadNode.getNodeType()==DownloadNode.DOWNLOAD_NODE){
        DownloadDO downloadDO = downloadNode.getDownloadDO();
        switch (column) {
          case 0:
            {
                return downloadDO.getFilename();
            }
          default:
            return "";
        }
    }
    else if (downloadNode.getNodeType()==DownloadNode.DIRECTORY_NODE){
        DownloadSourceDO downloadSourceDO = downloadNode.getDownloadSourceDO();
        if (downloadSourceDO == null) {
          return "";
        }
          switch (column) {
            case 0:
                return downloadNode.getPfad();
            default:
              return "";
          }
    }
    else if (downloadNode.getNodeType()==DownloadNode.SOURCE_NODE){
        DownloadSourceDO downloadSourceDO = downloadNode.getDownloadSourceDO();
        if (downloadSourceDO == null) {
          return "";
        }
          switch (column) {
            case 0:
                return downloadSourceDO.getFilename();
            case 1:
                return Integer.toString(downloadSourceDO.getStatus());
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                break;
            case 9:
              if (downloadSourceDO.getVersion() != null) {
                return downloadSourceDO.getVersion().getVersion();
              }
              else {
                return "";
              }
            default:
              return "";
          }
    }
    return null;
  }

    public void fireLanguageChanged() {
    }
}
