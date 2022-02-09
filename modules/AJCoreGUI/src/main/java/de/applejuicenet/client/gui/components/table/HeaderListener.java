/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.components.table;

import javax.swing.table.JTableHeader;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/components/table/HeaderListener.java,v 1.5 2009/02/12 13:03:34 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 *
 */
public class HeaderListener extends MouseAdapter
{
   private JTableHeader       header;
   private SortButtonRenderer renderer;

   public HeaderListener(JTableHeader header, SortButtonRenderer renderer)
   {
      this.header   = header;
      this.renderer = renderer;
   }

   public void mouseClicked(MouseEvent e)
   {
      int     col               = header.columnAtPoint(e.getPoint());
      int     curSelectedColumn = renderer.getSelectedColumn();
      boolean ascent;

      if(col == curSelectedColumn)
      {
         ascent = !renderer.getState();
      }
      else
      {
         ascent = true;
      }

      internalSort(col, ascent);
   }

   public void internalSort(int column, boolean ascent)
   {
      sort(column, ascent);
   }

   @SuppressWarnings("unchecked")
   public void sort(int column, boolean ascent)
   {
      renderer.setSelectedColumn(column, ascent);
      header.repaint();

      if(header.getTable().isEditing())
      {
         header.getTable().getCellEditor().stopCellEditing();
      }

      int sortCol = header.getTable().convertColumnIndexToModel(column);

      ((SortableTableModel) header.getTable().getModel()).sortByColumn(sortCol, ascent);
      header.repaint();
   }
}
