package de.applejuicenet.client.gui.share.table;

import de.applejuicenet.client.gui.components.dragndrop.DndSourceAdapter;
import de.applejuicenet.client.gui.components.treetable.DefaultTreeTableCellRenderer;
import de.applejuicenet.client.gui.components.treetable.JTreeTable;
import de.applejuicenet.client.gui.components.treetable.TreeTableModelAdapter;

import java.awt.dnd.*;

public class ShareTable
    extends JTreeTable {
	private static DragSource dragSource = DragSource.getDefaultDragSource();
    private boolean dragEnabled = false;

    public ShareTable(ShareTableModel treeTableModel) {
        super(treeTableModel, new DefaultTreeTableCellRenderer(treeTableModel));
        dragSource.createDefaultDragGestureRecognizer(this,
            DnDConstants.ACTION_COPY_OR_MOVE,
            new DragGestureListener() {
            public void dragGestureRecognized(DragGestureEvent event) {
                Object[] toDrag = getSelectedItems();
                dragSource.startDrag(event, DragSource.DefaultMoveNoDrop,
                                     new DragShareNode(toDrag),
                                     new DndSourceAdapter() {
                    public void dragDropEnd(DragSourceDropEvent event) {
                    }
                });
            }
        }
        );
    }

    public void setDragEnabled(boolean enabled) {
        dragEnabled = enabled;
    }

    public Object[] getSelectedItems() {
        int count = getSelectedRowCount();
        Object[] result = null;
        if (count == 1) {
            result = new Object[count];
            result[0] = ( (TreeTableModelAdapter) getModel()).nodeForRow(
                getSelectedRow());
        }
        else if (count > 1) {
            result = new Object[count];
            int[] indizes = getSelectedRows();
            for (int i = 0; i < indizes.length; i++) {
                result[i] = ( (TreeTableModelAdapter) getModel()).nodeForRow(
                    indizes[i]);
            }
        }
        return result;
    }

    public boolean isDragEnabled() {
        return dragEnabled;
    }
}
