package de.applejuicenet.client.gui.tables.search;

import de.applejuicenet.client.shared.Search;
import de.applejuicenet.client.shared.dac.DownloadDO;
import de.applejuicenet.client.gui.tables.AbstractTreeTableModel;
import de.applejuicenet.client.gui.tables.TreeTableModel;
import de.applejuicenet.client.gui.tables.download.DownloadMainNode;

import javax.swing.table.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/search/Attic/SearchResultTableModel.java,v 1.3 2003/10/01 07:25:44 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: SearchResultTableModel.java,v $
 * Revision 1.3  2003/10/01 07:25:44  maj0r
 * Suche weiter gefuehrt.
 *
 * Revision 1.2  2003/09/30 16:35:11  maj0r
 * Suche begonnen und auf neues ID-Listen-Prinzip umgebaut.
 *
 * Revision 1.1  2003/07/01 18:41:39  maj0r
 * Struktur verändert.
 *
 * Revision 1.4  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class SearchResultTableModel
        extends AbstractTreeTableModel {
    final static String[] COL_NAMES = {
        "Dateiname", "Größe", "Anzahl"};

    static protected Class[] cTypes = {
        TreeTableModel.class, String.class, String.class};

    public SearchResultTableModel(Search aSearch) {
        super(new SearchNode(aSearch));
    }

    protected Object[] getChildren(Object node) {
        if (node.getClass()==SearchNode.class){
            return ((SearchNode)node).getChildren();
        }
        return null;
    }

    public int getChildCount(Object node) {
        if (node.getClass()==SearchNode.class){
            return ((SearchNode)node).getChildCount();
        }
        return 0;
    }

    public Object getChild(Object node, int i) {
        Object[] obj = getChildren(node);
        if (obj==null || i>obj.length-1)
          return null;
        return obj[i];
    }

    public int getColumnCount() {
        return COL_NAMES.length;
    }

    public String getColumnName(int index) {
        return COL_NAMES[index];
    }

    public Class getColumnClass(int column) {
      return cTypes[column];
    }

    public Object getValueAt(Object node, int column) {
      if (node.getClass()==SearchNode.class){
          Object o = ((SearchNode)node).getValueObject();
          if (((SearchNode)node).getNodeType()==SearchNode.ROOT_NODE){
              return "";
          }
          else{
              Search.SearchEntry entry = (Search.SearchEntry) o;
              switch (column) {
                case 0:
                      return entry.getFileNames()[0].getDateiName();
                case 1:
                      return Long.toString(entry.getGroesse());
                case 2:
                      return entry.getChecksumme();
                default:
                  return "";
              }
          }
      }
      else if(node.getClass()==Search.SearchEntry.FileName.class){
          Search.SearchEntry.FileName filename = (Search.SearchEntry.FileName) node;
          switch (column) {
            case 0:
                  return filename.getDateiName();
            case 1:
                  return "";
            case 2:
                  return Integer.toString(filename.getHaeufigkeit());
            default:
              return "";
          }
      }
      return null;
    }
}