package de.applejuicenet.client.gui.components.table;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.table.JTableHeader;


/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/components/table/HeaderListener.java,v 1.1 2004/10/29 11:58:43 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class HeaderListener
    extends MouseAdapter {

    JTableHeader header;
    SortButtonRenderer renderer;

    public HeaderListener(JTableHeader header, SortButtonRenderer renderer) {
        this.header = header;
        this.renderer = renderer;
    }

    public void mouseClicked(MouseEvent e) {
        int col = header.columnAtPoint(e.getPoint());
        int sortCol = header.getTable().convertColumnIndexToModel(col);
        renderer.setPressedColumn(col);
        renderer.setSelectedColumn(col);
        header.repaint();

        if (header.getTable().isEditing()) {
            header.getTable().getCellEditor().stopCellEditing();
        }

        boolean isAscent;
        if (SortButtonRenderer.UP == renderer.getState(col)) {
            isAscent = true;
        }
        else {
            isAscent = false;
        }
        ( (SortableTableModel) header.getTable().getModel()).sortByColumn(
            sortCol,
            isAscent);
        renderer.setPressedColumn( -1);
        header.repaint();
    }
}
