/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.components.treetable;

import javax.swing.tree.TreeModel;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/components/treetable/TreeTableModel.java,v 1.2 2009/01/12 07:45:46 maj0r Exp $
 *
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: Official GUI for the appleJuice core developed by muhviehstarr</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 *
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
