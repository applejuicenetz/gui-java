package de.applejuicenet.client.gui.tables.dateiliste;

import java.util.HashMap;
import java.util.Iterator;

import javax.swing.table.AbstractTableModel;

import de.applejuicenet.client.gui.tables.share.ShareNode;
import de.applejuicenet.client.shared.dac.ShareDO;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/dateiliste/Attic/DateiListeTableModel.java,v 1.7 2004/02/05 23:11:27 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DateiListeTableModel.java,v $
 * Revision 1.7  2004/02/05 23:11:27  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.6  2004/01/30 16:32:47  maj0r
 * MapSetStringKey ausgebaut.
 *
 * Revision 1.5  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.4  2003/10/21 14:08:45  maj0r
 * Mittels PMD Code verschoenert, optimiert.
 *
 * Revision 1.3  2003/09/01 15:50:51  maj0r
 * Wo es moeglich war, DOs auf primitive Datentypen umgebaut.
 *
 * Revision 1.2  2003/08/28 10:39:05  maj0r
 * Sharelisten koennen jetzt gespeichert werden.
 *
 * Revision 1.1  2003/08/27 16:44:42  maj0r
 * Unterstuetzung fuer DragNDrop teilweise eingebaut.
 *
 *
 */

public class DateiListeTableModel
    extends AbstractTableModel {
    final static String[] COL_NAMES = {
        "Name", "Größe"};

    private HashMap dateien = new HashMap();

    public Object getRow(int row) {
        if (row < dateien.size()) {
            return dateien.values().toArray()[row];
        }
        return null;
    }

    public Object getValueAt(int row, int column) {
        if (row >= dateien.size()) {
            return "";
        }

        ShareDO shareDO = (ShareDO) dateien.values().toArray()[row];
        if (shareDO == null) {
            return "";
        }

        switch (column) {
            case 0:
                return shareDO.getShortfilename();
            case 1:
                return Long.toString(shareDO.getSize());
            default:
                return "Fehler";
        }
    }

    public int getColumnCount() {
        return COL_NAMES.length;
    }

    public String getColumnName(int index) {
        return COL_NAMES[index];
    }

    public int getRowCount() {
        return dateien.size();
    }

    public Class getClass(int column) {
        return String.class;
    }

    public void addNodes(ShareNode shareNode) {
        if (shareNode.isLeaf()) {
            dateien.put(Integer.toString(shareNode.getDO().getId()),
                        shareNode.getDO());
        }
        else {
            Iterator it = shareNode.getChildrenMap().values().iterator();
            while (it.hasNext()) {
                addNodes( (ShareNode) it.next());
            }
        }
        fireTableDataChanged();
    }

    public void removeRow(int row) {
        Object toRemove = getRow(row);
        if (toRemove != null) {
            dateien.remove(Integer.toString( ( (ShareDO) toRemove).getId()));
            fireTableDataChanged();
        }
    }

    public ShareDO[] getShareDOs() {
        return (ShareDO[]) dateien.values().toArray(new ShareDO[dateien.values().
            size()]);
    }
}