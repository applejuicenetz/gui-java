package de.applejuicenet.client.gui.tables.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import de.applejuicenet.client.gui.shared.SortableTableModel;
import de.applejuicenet.client.gui.shared.TableSorter;
import de.applejuicenet.client.shared.dac.ServerDO;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/server/Attic/ServerTableModel.java,v 1.11 2004/05/07 10:40:04 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [Maj0r@applejuicenet.de]
 *
 */

public class ServerTableModel
    extends AbstractTableModel
    implements SortableTableModel {
    final static String[] COL_NAMES = {
        "Name", "DynIP", "Port", "Verbindungsversuche", "Letztes mal online"};

    private TableSorter sorter;
    private List servers = new ArrayList();

    public List getContent() {
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

        switch (column) {
            case 0:
                return server.getName();
            case 1:
                return server.getHost();
            case 2:
                return server.getPort();
            case 3:
                return new Integer(server.getVersuche());
            case 4:
                return server.getTimeLastSeenAsString();
            default:
                return "";
        }
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
        if (index == 3) {
            return Number.class;
        }
        else {
            return String.class;
        }
    }

    public void setTable(Map changedContent) {
        //alte Server entfernen
        String suchKey = null;
        ArrayList toRemove = new ArrayList();
        for (int i = 0; i < servers.size(); i++) {
            suchKey = Integer.toString( ( (ServerDO) servers.get(i)).getID());
            if (!changedContent.containsKey(suchKey)) {
                toRemove.add(servers.get(i));
            }
        }
        for (int x = 0; x < toRemove.size(); x++) {
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
                oldServer.setVersuche(server.getVersuche());
                oldServer.setTimeLastSeen(server.getTimeLastSeen());
                oldServer.setConnected(server.isConnected());
                oldServer.setTryConnect(server.isTryConnect());
            }
        }
        this.fireTableDataChanged();
    }

    public Object getValueForSortAt(int row, int column) {
        if (column!=4){
            return getValueAt(row, column);
        }
        else{
            if ( (servers == null) || (row >= servers.size())) {
               return "";
           }
           ServerDO server = (ServerDO) servers.get(row);
           if (server == null) {
               return "";
           }
           return new Long(server.getTimeLastSeen());
        }
    }
}