package de.applejuicenet.client.gui.shared;

import java.awt.event.*;
import javax.swing.table.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/shared/Attic/HeaderListener.java,v 1.2 2003/10/10 15:12:26 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: HeaderListener.java,v $
 * Revision 1.2  2003/10/10 15:12:26  maj0r
 * Sortieren im Downloadbereich eingefuegt.
 *
 * Revision 1.1  2003/06/24 14:32:27  maj0r
 * Klassen zum Sortieren von Tabellen eingefügt.
 * Servertabelle kann nun spaltenweise sortiert werden.
 *
 *
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

  public void mousePressed(MouseEvent e) {
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
    ( (SortableTableModel) header.getTable().getModel()).sortByColumn(sortCol,
        isAscent);
  }

  public void mouseReleased(MouseEvent e) {
    int col = header.columnAtPoint(e.getPoint());
    renderer.setPressedColumn( -1);
    header.repaint();
  }
}