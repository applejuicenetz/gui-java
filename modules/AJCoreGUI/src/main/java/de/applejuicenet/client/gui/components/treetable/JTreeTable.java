package de.applejuicenet.client.gui.components.treetable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import com.formdev.flatlaf.util.UIScale;
import java.util.EventObject;


/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/components/treetable/JTreeTable.java,v 1.5 2004/11/30 19:09:41 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 *
 */

public class JTreeTable
    extends JTable {

	protected DefaultTreeTableCellRenderer tree;
    protected JTable thisTable;

    public JTreeTable(TreeTableModel treeTableModel, DefaultTreeTableCellRenderer treeTableCellRenderer) {
        super();
        thisTable = this;
        treeTableCellRenderer.setTreeTable(this);
        tree = treeTableCellRenderer;

        super.setModel(new TreeTableModelAdapter(treeTableModel, tree));

        ListToTreeSelectionModelWrapper selectionWrapper = new
            ListToTreeSelectionModelWrapper();
        tree.setSelectionModel(selectionWrapper);
        setSelectionModel(selectionWrapper.getListSelectionModel());

        setDefaultRenderer(TreeTableModel.class, tree);
        setDefaultEditor(TreeTableModel.class, new TreeTableCellEditor());

        setShowGrid(false);

        setIntercellSpacing(new Dimension(0, 0));

        synchronizeTreeAppearance();
        installTreeNavigation();
    }

    public void updateUI() {
        super.updateUI();
        if (tree != null) {
            tree.updateUI();
            synchronizeTreeAppearance();
            installTreeNavigation();
        }
        /*LookAndFeel.installColorsAndFont(this, "Tree.background",
                                         "Tree.foreground", "Tree.font");*/
    }

    public int getEditingRow() {
        return (editingColumn < 0 || getColumnClass(editingColumn) == TreeTableModel.class) ? -1 :
            editingRow;
    }

    private void synchronizeTreeAppearance() {
        tree.setFont(getFont());
        tree.setForeground(getForeground());
        tree.setBackground(getBackground());
        int contentHeight = getFontMetrics(getFont()).getHeight();
        for (String key : new String[] {"Tree.openIcon", "Tree.closedIcon", "Tree.leafIcon"}) {
            Icon icon = UIManager.getIcon(key);
            if (icon != null) {
                contentHeight = Math.max(contentHeight, icon.getIconHeight());
            }
        }
        setRowHeight(Math.max(getRowHeight(), contentHeight + UIScale.scale(4)));
    }

    private void installTreeNavigation() {
        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("LEFT"), "collapseTreeNode");
        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("RIGHT"), "expandTreeNode");
        getActionMap().put("collapseTreeNode", new AbstractAction() {
            public void actionPerformed(ActionEvent event) {
                navigateTree(false);
            }
        });
        getActionMap().put("expandTreeNode", new AbstractAction() {
            public void actionPerformed(ActionEvent event) {
                navigateTree(true);
            }
        });
    }

    private void navigateTree(boolean expand) {
        int row = getSelectionModel().getLeadSelectionIndex();
        if (row < 0 || !isRowSelected(row)) {
            return;
        }
        TreePath path = tree.getPathForRow(row);
        if (path == null) {
            return;
        }
        TreePath target = path;
        if (expand) {
            if (tree.getModel().isLeaf(path.getLastPathComponent())) {
                return;
            }
            if (!tree.isExpanded(path)) {
                tree.expandPath(path);
            } else if (tree.getModel().getChildCount(path.getLastPathComponent()) > 0) {
                target = path.pathByAddingChild(tree.getModel().getChild(path.getLastPathComponent(), 0));
            }
        } else if (tree.isExpanded(path)) {
            tree.collapsePath(path);
        } else {
            target = path.getParentPath();
        }
        int targetRow = target == null ? -1 : tree.getRowForPath(target);
        if (targetRow >= 0) {
            getSelectionModel().setSelectionInterval(targetRow, targetRow);
            scrollRectToVisible(getCellRect(targetRow, 0, true));
        }
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
    
    public class TreeTableCellEditor
        extends AbstractCellEditor
        implements
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
                        MouseEvent me = (MouseEvent) e;
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

    class ListToTreeSelectionModelWrapper
        extends DefaultTreeSelectionModel {

		protected boolean updatingListSelectionModel;

        public ListToTreeSelectionModelWrapper() {
            super();
            getListSelectionModel().addListSelectionListener
                (createListSelectionListener());
        }

        ListSelectionModel getListSelectionModel() {
            return listSelectionModel;
        }

        public void resetRowSelection() {
            if (!updatingListSelectionModel) {
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
            if (!updatingListSelectionModel) {
                updatingListSelectionModel = true;
                try {
                    int min = listSelectionModel.getMinSelectionIndex();
                    int max = listSelectionModel.getMaxSelectionIndex();

                    clearSelection();
                    if (min != -1 && max != -1) {
                        for (int counter = min; counter <= max; counter++) {
                            if (listSelectionModel.isSelectedIndex(counter)) {
                                TreePath selPath = tree.getPathForRow
                                    (counter);

                                if (selPath != null) {
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

        class ListSelectionHandler
            implements ListSelectionListener {
            public void valueChanged(ListSelectionEvent e) {
                updateSelectedPathsFromSelectedRows();
            }
        }
    }

}
