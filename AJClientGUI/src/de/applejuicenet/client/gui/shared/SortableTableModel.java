package de.applejuicenet.client.gui.shared;

import java.util.List;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/shared/Attic/SortableTableModel.java,v 1.5 2004/05/07 10:40:04 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [Maj0r@applejuicenet.de]
 *
 */

public interface SortableTableModel {
    public int getRowCount();

    public List getContent();

    public Object getValueForSortAt(int row, int column);

    public Class getColumnClass(int column);

    public void sortByColumn(int column, boolean isAscent);
}