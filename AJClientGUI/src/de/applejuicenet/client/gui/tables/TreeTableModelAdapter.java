package de.applejuicenet.client.gui.tables;

/*
 * @(#)TreeTableModelAdapter.java	1.2 98/10/27
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

import de.applejuicenet.client.gui.tables.TreeTableModel;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.TreePath;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/Attic/TreeTableModelAdapter.java,v 1.3 2003/07/02 13:54:34 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: TreeTableModelAdapter.java,v $
 * Revision 1.3  2003/07/02 13:54:34  maj0r
 * JTreeTable komplett überarbeitet.
 *
 *
 */

public class TreeTableModelAdapter extends AbstractTableModel
{
    JTree tree;
    TreeTableModel treeTableModel;

    public TreeTableModelAdapter(TreeTableModel treeTableModel, JTree tree) {
        this.tree = tree;
        this.treeTableModel = treeTableModel;
        tree.setRootVisible(false);

	tree.addTreeExpansionListener(new TreeExpansionListener() {
	    public void treeExpanded(TreeExpansionEvent event) {
	      fireTableDataChanged(); 
	    }
            public void treeCollapsed(TreeExpansionEvent event) {
	      fireTableDataChanged(); 
	    }
	});

	treeTableModel.addTreeModelListener(new TreeModelListener() {
	    public void treeNodesChanged(TreeModelEvent e) {
		delayedFireTableDataChanged();
	    }

	    public void treeNodesInserted(TreeModelEvent e) {
		delayedFireTableDataChanged();
	    }

	    public void treeNodesRemoved(TreeModelEvent e) {
		delayedFireTableDataChanged();
	    }

	    public void treeStructureChanged(TreeModelEvent e) {
		delayedFireTableDataChanged();
	    }
	});
    }

    public int getColumnCount() {
	return treeTableModel.getColumnCount();
    }

    public String getColumnName(int column) {
	return treeTableModel.getColumnName(column);
    }

    public Class getColumnClass(int column) {
	return treeTableModel.getColumnClass(column);
    }

    public int getRowCount() {
	return tree.getRowCount();
    }

    public Object nodeForRow(int row) {
	TreePath treePath = tree.getPathForRow(row);
	return treePath.getLastPathComponent();         
    }

    public Object getValueAt(int row, int column) {
	return treeTableModel.getValueAt(nodeForRow(row), column);
    }

    public boolean isCellEditable(int row, int column) {
         return treeTableModel.isCellEditable(nodeForRow(row), column); 
    }

    public void setValueAt(Object value, int row, int column) {
	treeTableModel.setValueAt(value, nodeForRow(row), column);
    }

    protected void delayedFireTableDataChanged() {
	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		fireTableDataChanged();
	    }
	});
    }

  public void expandOrCollapseRow(int row) {
    if (tree.isExpanded(row)) {
      tree.collapseRow(row);
    }
    else {
      tree.expandRow(row);
    }
  }
}

