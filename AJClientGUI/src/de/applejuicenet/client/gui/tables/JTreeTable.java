package de.applejuicenet.client.gui.tables;

import de.applejuicenet.client.gui.tables.Node;
import de.applejuicenet.client.gui.tables.AbstractCellEditor;
import de.applejuicenet.client.gui.tables.download.DownloadNode;
import de.applejuicenet.client.shared.dac.DownloadDO;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.table.*;

import java.awt.Dimension;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;

import java.awt.event.MouseEvent;

import java.util.EventObject;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/Attic/JTreeTable.java,v 1.4 2003/07/06 20:00:19 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: JTreeTable.java,v $
 * Revision 1.4  2003/07/06 20:00:19  maj0r
 * DownloadTable bearbeitet.
 *
 * Revision 1.3  2003/07/02 13:54:34  maj0r
 * JTreeTable komplett überarbeitet.
 *
 *
 */

public class JTreeTable extends JTable {

    protected TreeTableCellRenderer tree;

    protected JTable thisTable;

    public JTreeTable(TreeTableModel treeTableModel) {
	super();
    thisTable = this;
	tree = new TreeTableCellRenderer(treeTableModel);

	super.setModel(new TreeTableModelAdapter(treeTableModel, tree));

	ListToTreeSelectionModelWrapper selectionWrapper = new
            ListToTreeSelectionModelWrapper();
	tree.setSelectionModel(selectionWrapper);
	setSelectionModel(selectionWrapper.getListSelectionModel()); 

	setDefaultRenderer(TreeTableModel.class, tree);
	setDefaultEditor(TreeTableModel.class, new TreeTableCellEditor());

	setShowGrid(false);

	setIntercellSpacing(new Dimension(0, 0));

	if (tree.getRowHeight() < 1) {
	    // Metal looks better like this.
	    setRowHeight(18);
	}
    }

    public void updateUI() {
	super.updateUI();
	if(tree != null) {
	    tree.updateUI();
	}
        LookAndFeel.installColorsAndFont(this, "Tree.background",
                                         "Tree.foreground", "Tree.font");
    }

    public int getEditingRow() {
        return (getColumnClass(editingColumn) == TreeTableModel.class) ? -1 :
	        editingRow;  
    }

    public void setRowHeight(int rowHeight) {
        super.setRowHeight(rowHeight); 
	if (tree != null && tree.getRowHeight() != rowHeight) {
            tree.setRowHeight(getRowHeight()); 
	}
    }

    public JTree getTree() {
	return tree;
    }

    public class TreeTableCellRenderer extends JTree implements
            TableCellRenderer {

	protected int visibleRow;

	public TreeTableCellRenderer(TreeModel model) {
	    super(model);
        this.setCellRenderer(new IconNodeRenderer());
	}

	public void updateUI() {
	    super.updateUI();
	    TreeCellRenderer tcr = getCellRenderer();
	    if (tcr instanceof DefaultTreeCellRenderer) {
		DefaultTreeCellRenderer dtcr = ((DefaultTreeCellRenderer)tcr);
		dtcr.setTextSelectionColor(UIManager.getColor
					   ("Table.selectionForeground"));
		dtcr.setBackgroundSelectionColor(UIManager.getColor
						("Table.selectionBackground"));
	    }
	}

	public void setRowHeight(int rowHeight) {
	    if (rowHeight > 0) {
		super.setRowHeight(rowHeight); 
		if (JTreeTable.this != null &&
		    JTreeTable.this.getRowHeight() != rowHeight) {
		    JTreeTable.this.setRowHeight(getRowHeight());
		}
	    }
	}

	public void setBounds(int x, int y, int w, int h) {
	    super.setBounds(x, 0, w, JTreeTable.this.getHeight());
	}

	public void paint(Graphics g) {
	    g.translate(0, -visibleRow * getRowHeight());
	    super.paint(g);
	}

	public Component getTableCellRendererComponent(JTable table,
						       Object value,
						       boolean isSelected,
						       boolean hasFocus,
						       int row, int column) {
        DownloadNode node = (DownloadNode) ( (TreeTableModelAdapter) table.getModel()).
            nodeForRow(row);
	    if(isSelected)
    		setBackground(table.getSelectionBackground());
	    else{
            if (node.getNodeType()==DownloadNode.SOURCE_NODE){
                setBackground(DownloadNode.SOURCE_NODE_COLOR);
            }
            else if (node.getNodeType()==DownloadNode.DOWNLOAD_NODE &&
                node.getDownloadDO().getStatus()==DownloadDO.FERTIGSTELLEN){
                  setBackground(DownloadNode.DOWNLOAD_FERTIG_COLOR);
            }
            else{
                setBackground(table.getBackground());
            }
        }

	    visibleRow = row;
	    return this;
	}
    }


    public class TreeTableCellEditor extends AbstractCellEditor implements
            TableCellEditor {
	public Component getTableCellEditorComponent(JTable table,
						     Object value,
						     boolean isSelected,
						     int r, int c) {
	    return tree;
	}

	public boolean isCellEditable(EventObject e) {
	    if (e instanceof MouseEvent) {
		for (int counter = getColumnCount() - 1; counter >= 0;
		     counter--) {
		    if (getColumnClass(counter) == TreeTableModel.class) {
			MouseEvent me = (MouseEvent)e;
			MouseEvent newME = new MouseEvent(tree, me.getID(),
				   me.getWhen(), me.getModifiers(),
				   me.getX() - getCellRect(0, counter, true).x,
				   me.getY(), me.getClickCount(),
                                   me.isPopupTrigger());
			tree.dispatchEvent(newME);
			break;
		    }
		}
	    }
	    return false;
	}
    }

    class ListToTreeSelectionModelWrapper extends DefaultTreeSelectionModel {

	protected boolean         updatingListSelectionModel;

	public ListToTreeSelectionModelWrapper() {
	    super();
	    getListSelectionModel().addListSelectionListener
	                            (createListSelectionListener());
	}

	ListSelectionModel getListSelectionModel() {
	    return listSelectionModel; 
	}

	public void resetRowSelection() {
	    if(!updatingListSelectionModel) {
		updatingListSelectionModel = true;
		try {
		    super.resetRowSelection();
		}
		finally {
		    updatingListSelectionModel = false;
		}
	    }
	}

	protected ListSelectionListener createListSelectionListener() {
	    return new ListSelectionHandler();
	}

	protected void updateSelectedPathsFromSelectedRows() {
	    if(!updatingListSelectionModel) {
		updatingListSelectionModel = true;
		try {
		    int        min = listSelectionModel.getMinSelectionIndex();
		    int        max = listSelectionModel.getMaxSelectionIndex();

		    clearSelection();
		    if(min != -1 && max != -1) {
			for(int counter = min; counter <= max; counter++) {
			    if(listSelectionModel.isSelectedIndex(counter)) {
				TreePath     selPath = tree.getPathForRow
				                            (counter);

				if(selPath != null) {
				    addSelectionPath(selPath);
				}
			    }
			}
		    }
		}
		finally {
		    updatingListSelectionModel = false;
		}
	    }
	}

	class ListSelectionHandler implements ListSelectionListener {
	    public void valueChanged(ListSelectionEvent e) {
		updateSelectedPathsFromSelectedRows();
	    }
	}
    }

  public class IconNodeRenderer
      extends DefaultTreeCellRenderer {

    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean sel, boolean expanded,
                                                  boolean leaf,
                                                  int row, boolean hasFocus) {

      Component c = super.getTreeCellRendererComponent(tree, value,
                                         sel, expanded, leaf, row, hasFocus);
      if (c instanceof JLabel){
          ((JLabel)c).setOpaque(true);
          if (sel){
              ((JLabel)c).setBackground(thisTable.getSelectionBackground());
              ((JLabel)c).setForeground(thisTable.getSelectionForeground());
          }
          else{
              if (((DownloadNode)value).getNodeType()==DownloadNode.SOURCE_NODE){
                  ((JLabel)c).setBackground(DownloadNode.SOURCE_NODE_COLOR);
              }
              else if (((DownloadNode)value).getNodeType()==DownloadNode.DOWNLOAD_NODE &&
                  ((DownloadNode)value).getDownloadDO().getStatus()==DownloadDO.FERTIGSTELLEN){
                    ((JLabel)c).setBackground(DownloadNode.DOWNLOAD_FERTIG_COLOR);
              }
              else{
                  ((JLabel)c).setBackground(tree.getBackground());
              }
          }
      }
      Icon icon = ( (Node) value).getConvenientIcon();
      if (icon != null) {
        setIcon(icon);
      }
      return this;
    }
  }
}