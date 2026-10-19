/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.components.treetable;

import javax.swing.tree.TreeModel;

/**
 * Copyright: General Public License
 */
public interface TreeTableModel extends TreeModel
{
   int getColumnCount();

   String getColumnName(int column);

   @SuppressWarnings("unchecked")
   Class getColumnClass(int column);

   Object getValueAt(Object node, int column);

   boolean isCellEditable(Object node, int column);

   void setValueAt(Object aValue, Object node, int column);
}
