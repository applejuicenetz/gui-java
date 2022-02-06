/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.components.treetable;


/*
 * @(#)TreeTableModelAdapter.java        1.2 98/10/27
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.TreePath;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/components/treetable/TreeTableModelAdapter.java,v 1.4 2009/01/12 07:45:46 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 *
 */
public class TreeTableModelAdapter extends AbstractTableModel
{
   JTree          tree;
   TreeTableModel treeTableModel;

   public TreeTableModelAdapter(TreeTableModel treeTableModel, JTree tree)
   {
      this.tree           = tree;
      this.treeTableModel = treeTableModel;
      tree.setRootVisible(false);
      tree.setShowsRootHandles(true);

      tree.addTreeExpansionListener(new TreeExpansionListener()
         {
            public void treeExpanded(TreeExpansionEvent event)
            {
               fireTableDataChanged();
            }

            public void treeCollapsed(TreeExpansionEvent event)
            {
               fireTableDataChanged();
            }
         });

      treeTableModel.addTreeModelListener(new TreeModelListener()
         {
            public void treeNodesChanged(TreeModelEvent e)
            {
               delayedFireTableDataChanged();
            }

            public void treeNodesInserted(TreeModelEvent e)
            {
               delayedFireTableDataChanged();
            }

            public void treeNodesRemoved(TreeModelEvent e)
            {
               delayedFireTableDataChanged();
            }

            public void treeStructureChanged(TreeModelEvent e)
            {
               delayedFireTableDataChanged();
            }
         });
   }

   public int getColumnCount()
   {
      return treeTableModel.getColumnCount();
   }

   public String getColumnName(int column)
   {
      return treeTableModel.getColumnName(column);
   }

   @SuppressWarnings("unchecked")
   public Class getColumnClass(int column)
   {
      return treeTableModel.getColumnClass(column);
   }

   public int getRowCount()
   {
      return tree.getRowCount();
   }

   public Object nodeForRow(int row)
   {
      if(row == -1)
      {
         return null;
      }

      TreePath treePath = tree.getPathForRow(row);

      if(treePath != null)
      {
         return treePath.getLastPathComponent();
      }
      else
      {
         return null;
      }
   }

   public Object getValueAt(int row, int column)
   {
      return treeTableModel.getValueAt(nodeForRow(row), column);
   }

   public boolean isCellEditable(int row, int column)
   {
      return treeTableModel.isCellEditable(nodeForRow(row), column);
   }

   public void setValueAt(Object value, int row, int column)
   {
      treeTableModel.setValueAt(value, nodeForRow(row), column);
   }

   protected void delayedFireTableDataChanged()
   {
      SwingUtilities.invokeLater(this::fireTableDataChanged);
   }

   public void expandOrCollapseRow(int row)
   {
      if(tree.isExpanded(row))
      {
         tree.collapseRow(row);
      }
      else
      {
         tree.expandRow(row);
      }
   }
}
