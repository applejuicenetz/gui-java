package de.applejuicenet.client.gui.tables.server;

import java.util.*;

import javax.swing.table.*;

import de.applejuicenet.client.shared.dac.*;
import de.applejuicenet.client.shared.MapSetStringKey;
import de.applejuicenet.client.shared.NetworkInfo;
import de.applejuicenet.client.gui.shared.TableSorter;
import de.applejuicenet.client.gui.shared.SortableTableModel;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/server/Attic/ServerTableModel.java,v 1.4 2003/08/19 16:02:16 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: ServerTableModel.java,v $
 * Revision 1.4  2003/08/19 16:02:16  maj0r
 * Optimierungen.
 *
 * Revision 1.3  2003/08/17 20:55:34  maj0r
 * Neusortierung der Tabelle nach Änderung entfernt.
 *
 * Revision 1.2  2003/08/02 12:03:38  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.1  2003/07/01 18:41:39  maj0r
 * Struktur verändert.
 *
 * Revision 1.6  2003/07/01 14:54:27  maj0r
 * Weggefallene Server werden erkannt und entfernt.
 *
 * Revision 1.5  2003/06/24 14:32:27  maj0r
 * Klassen zum Sortieren von Tabellen eingefügt.
 * Servertabelle kann nun spaltenweise sortiert werden.
 *
 * Revision 1.4  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class ServerTableModel
    extends AbstractTableModel implements SortableTableModel{
  final static String[] COL_NAMES = {
      "Name", "DynIP", "Port", "Letztes mal online"};

  TableSorter sorter;

  ArrayList servers = new ArrayList();

  public ServerTableModel() {
    super();
  }

  public ArrayList getContent() {
    return servers;
  }

  public Object getRow(int row) {
    if ( (servers != null) && (row < servers.size())) {
      return servers.get(row);
    }
    return null;
  }


  public void sortByColumn(int column, boolean isAscent) {
    if (sorter == null) {
      sorter = new TableSorter(this);
    }
    sorter.sort(column, isAscent);
    fireTableDataChanged();
  }


  public Object getValueAt(int row, int column) {
    if ( (servers == null) || (row >= servers.size())) {
      return "";
    }

    ServerDO server = (ServerDO) servers.get(row);
    if (server == null) {
      return "";
    }

    String s = new String("");
    switch (column) {
      case 0:
        s = server.getName();
        break;
      case 1:
        s = server.getHost();
        break;
      case 2:
        s = server.getPort();
        break;
      case 3:
        s = server.getTimeLastSeenAsString();
        break;
      default:
        s = new String("Fehler");
    }
    if (s == null) {
      s = "";
    }
    return s;
  }

  public int getColumnCount() {
    return COL_NAMES.length;
  }

  public String getColumnName(int index) {
    return COL_NAMES[index];
  }

  public int getRowCount() {
    if (servers == null) {
      return 0;
    }
    return servers.size();
  }

  public Class getClass(int index) {
    return String.class;
  }

  public void setTable(HashMap changedContent) {
    //alte Server entfernen
    MapSetStringKey suchKey = null;
    ArrayList toRemove = new ArrayList();
    for(int i=0; i<servers.size(); i++){
        suchKey = new MapSetStringKey(((ServerDO)servers.get(i)).getIDasString());
        if (!changedContent.containsKey(suchKey)){
              toRemove.add(servers.get(i));
        }
    }
    for (int x=0; x<toRemove.size(); x++){
        servers.remove(toRemove.get(x));
    }
    Iterator it = changedContent.values().iterator();
    while (it.hasNext()) {
      ServerDO server = (ServerDO) it.next();
      int index = servers.indexOf(server);
      if (index == -1) { // Der Server ist neu
        servers.add(server);
      }
      else { // Der Server hat sich verändert
        ServerDO oldServer = (ServerDO) servers.get(index);
        oldServer.setHost(server.getHost());
        oldServer.setName(server.getName());
        oldServer.setPort(server.getPort());
        oldServer.setTimeLastSeen(server.getTimeLastSeen());
        oldServer.setConnected(server.isConnected());
        oldServer.setTryConnect(server.isTryConnect());
      }
    }
    this.fireTableDataChanged();
  }
}