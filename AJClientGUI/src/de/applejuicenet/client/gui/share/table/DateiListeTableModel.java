package de.applejuicenet.client.gui.share.table;

import java.util.HashMap;
import java.util.Iterator;

import javax.swing.table.AbstractTableModel;

import de.applejuicenet.client.shared.dac.ShareDO;
import java.util.Map;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/share/table/DateiListeTableModel.java,v 1.2 2004/11/22 16:25:26 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class DateiListeTableModel
    extends AbstractTableModel {

	final static String[] COL_NAMES = {
        "Name", "Groesze"};

    private Map dateien = new HashMap();
    private Object[] sortedChildren;

    public Object getRow(int row) {
        if (row < dateien.size()) {
            return sortedChildren[row];
        }
        return null;
    }

    private Object[] sortChildren(){
        Object[] children = dateien.values().toArray();
        Object tmp;
        int n = children.length;
        for (int i = 0; i < n - 1; i++) {
            int k = i;
            for (int j = i + 1; j < n; j++) {
                if (((((ShareDO)children[j]).getShortfilename().compareToIgnoreCase(
                    ((ShareDO)children[k]).getShortfilename()))) < 0) {
                    k = j;
                }
            }
            tmp = children[i];
            children[i] = children[k];
            children[k] = tmp;
        }
        sortedChildren = children;
        return sortedChildren;
    }

    public Object getValueAt(int row, int column) {
        if (row >= dateien.size()) {
            return "";
        }

        ShareDO shareDO = (ShareDO) sortedChildren[row];
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
        sortChildren();
        fireTableDataChanged();
    }

    public void removeRow(int row) {
        Object toRemove = getRow(row);
        if (toRemove != null) {
            dateien.remove(Integer.toString( ( (ShareDO) toRemove).getId()));
            sortChildren();
            fireTableDataChanged();
        }
    }

    public ShareDO[] getShareDOs() {
        return (ShareDO[]) dateien.values().toArray(new ShareDO[dateien.values().
            size()]);
    }
}