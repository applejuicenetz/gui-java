package de.applejuicenet.client.gui.shared;

import java.util.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/shared/Attic/SortableTableModel.java,v 1.1 2003/06/24 14:32:27 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: SortableTableModel.java,v $
 * Revision 1.1  2003/06/24 14:32:27  maj0r
 * Klassen zum Sortieren von Tabellen eingefügt.
 * Servertabelle kann nun spaltenweise sortiert werden.
 *
 *
 *
 */

public interface SortableTableModel {
  public int getRowCount();

  public ArrayList getContent();

  public Object getValueAt(int row, int column);

  public Class getColumnClass(int column);

  public void sortByColumn(int column, boolean isAscent);
}