package de.applejuicenet.client.gui.share.table;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.entity.Share;
import org.junit.Test;
import de.applejuicenet.client.gui.components.treetable.DefaultTreeTableCellRenderer;
import de.applejuicenet.client.gui.components.treetable.JTreeTable;

import javax.swing.SwingUtilities;
import javax.swing.JTree;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.GraphicsEnvironment;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeFalse;

public class ShareTableRefreshTest {
    @Test
    public void priorityIsCenteredEvenAfterColumnReordering() throws Exception {
        assumeFalse(GraphicsEnvironment.isHeadless());
        SwingUtilities.invokeAndWait(() -> {
            ShareTable table = new ShareTable(new ShareTableModel(new ShareNode(null, null)));
            TableCellRenderer renderer = table.getColumnModel().getColumn(2).getCellRenderer();
            JLabel label = (JLabel) renderer.getTableCellRendererComponent(table, 42, true, false, 0, 2);
            assertEquals("42", label.getText());
            assertEquals(SwingConstants.CENTER, label.getHorizontalAlignment());
            assertEquals(table.getSelectionBackground(), label.getBackground());
            table.moveColumn(2, 0);
            assertSame(renderer, table.getColumnModel().getColumn(0).getCellRenderer());
            label = (JLabel) renderer.getTableCellRendererComponent(table, null, false, false, 0, 0);
            assertEquals("", label.getText());
            assertEquals(SwingConstants.CENTER, label.getHorizontalAlignment());
            assertNull(table.getColumnModel().getColumn(table.convertColumnIndexToView(1)).getCellRenderer());
        });
    }

    private Share share(int id, String name) {
        return new Share() {
            public int getId() { return id; }
            public String getFilename() { return name; }
            public String getShortfilename() { return name.substring(name.lastIndexOf('/') + 1); }
            public long getSize() { return 1024; }
            public String getCheckSum() { return "hash"; }
            public int getPrioritaet() { return 1; }
            public long getAskCount() { return 0; }
            public long getLastAsked() { return 0; }
            public long getSearchCount() { return 0; }
            public String getAjfspLink() { return "ajfsp://file|" + getShortfilename() + "|hash|1024/"; }
        };
    }

    @Test
    public void refreshRebuildsTreeAndNotifiesSwingOnEventThread() throws Exception {
        String oldSeparator = ApplejuiceFassade.separator;
        ApplejuiceFassade.separator = "/";
        try {
            SwingUtilities.invokeAndWait(() -> {
                ShareTableModel model = new ShareTableModel(new ShareNode(null, null));
                AtomicInteger changes = new AtomicInteger();
                model.addTreeModelListener(new TreeModelListener() {
                    public void treeNodesChanged(TreeModelEvent e) { }
                    public void treeNodesInserted(TreeModelEvent e) { }
                    public void treeNodesRemoved(TreeModelEvent e) { }
                    public void treeStructureChanged(TreeModelEvent e) {
                        assertTrue(SwingUtilities.isEventDispatchThread());
                        assertSame(model.getRoot(), e.getTreePath().getLastPathComponent());
                        changes.incrementAndGet();
                    }
                });
                model.setShares(Collections.singletonList(share(1, "/old/file.bin")));
                ShareNode oldRoot = model.getRootNode();
                assertEquals("old", model.getChild(oldRoot, 0).toString());
                model.setShares(Collections.singletonList(share(1, "/new/file.bin")));
                assertNotSame(oldRoot, model.getRootNode());
                assertEquals(1, model.getChildCount(model.getRoot()));
                assertEquals("new", model.getChild(model.getRoot(), 0).toString());
                model.setShares(Collections.emptyList());
                assertEquals(0, model.getChildCount(model.getRoot()));
                assertEquals(3, changes.get());
            });
        } finally {
            ApplejuiceFassade.separator = oldSeparator;
        }
    }

    @Test
    public void addingDirectoryInvalidatesSortedChildren() {
        String oldSeparator = ApplejuiceFassade.separator;
        ApplejuiceFassade.separator = "/";
        try {
            ShareNode root = new ShareNode(null, null);
            root.addChild(share(1, "/first/file.bin"));
            assertEquals(1, root.getChildren().length);
            root.addChild(share(2, "/second/file.bin"));
            assertEquals(2, root.getChildren().length);
        } finally {
            ApplejuiceFassade.separator = oldSeparator;
        }
    }

    @Test
    public void refreshPreservesExpandedFoldersAndSelectionDespiteChangedIdsAndRows() throws Exception {
        verifyTreeState(Arrays.asList(share(8, "/aaa/new.bin"), share(9, "/folder/sub/file.bin"),
                share(10, "/other/file.bin")), new String[] {"folder", "sub", "file.bin"});
    }

    @Test
    public void removedFileSelectsExistingParent() throws Exception {
        verifyTreeState(Collections.singletonList(share(2, "/folder/sub/replacement.bin")),
                new String[] {"folder", "sub"});
    }

    @Test
    public void removedFolderSelectsNearestExistingAncestor() throws Exception {
        verifyTreeState(Collections.singletonList(share(2, "/folder/replacement.bin")),
                new String[] {"folder"});
    }

    @Test
    public void removedTopLevelFolderClearsSelection() throws Exception {
        verifyTreeState(Collections.singletonList(share(2, "/other/file.bin")), null);
    }

    @Test
    public void emptyShareClearsSelection() throws Exception {
        verifyTreeState(Collections.emptyList(), null);
    }

    @Test
    public void refreshPreservesMultipleSelectedPathsAndScrollPosition() throws Exception {
        String oldSeparator = ApplejuiceFassade.separator;
        ApplejuiceFassade.separator = "/";
        AtomicReference<JTreeTable> tableReference = new AtomicReference<>();
        AtomicReference<JScrollPane> scrollReference = new AtomicReference<>();
        try {
            SwingUtilities.invokeAndWait(() -> {
                List<Share> shares = new ArrayList<>();
                for (int index = 0; index < 40; index++) {
                    shares.add(share(index, String.format("/folder/file%02d.bin", index)));
                }
                ShareTableModel model = new ShareTableModel(new ShareNode(null, null));
                model.setShares(shares);
                JTreeTable table = new JTreeTable(model, new DefaultTreeTableCellRenderer(model));
                tableReference.set(table);
                JScrollPane scrollPane = new JScrollPane(table);
                scrollReference.set(scrollPane);
                scrollPane.getViewport().setExtentSize(new Dimension(200, 100));
                scrollPane.getViewport().setViewSize(table.getPreferredSize());
                JTree tree = table.getTree();
                tree.expandPath(path(tree, "folder"));
                tree.setSelectionPaths(new TreePath[] {path(tree, "folder", "file20.bin"),
                        path(tree, "folder", "file21.bin")});
                scrollPane.getViewport().setViewPosition(new Point(0, 200));
                ShareTreeState.refresh(table, shares);
            });
            SwingUtilities.invokeAndWait(() -> {
                JTreeTable table = tableReference.get();
                JTree tree = table.getTree();
                assertEquals(2, tree.getSelectionCount());
                assertEquals(2, table.getSelectedRowCount());
                assertTrue(tree.isPathSelected(path(tree, "folder", "file20.bin")));
                assertTrue(tree.isPathSelected(path(tree, "folder", "file21.bin")));
                assertEquals(new Point(0, 200), scrollReference.get().getViewport().getViewPosition());
            });
        } finally {
            ApplejuiceFassade.separator = oldSeparator;
        }
    }

    private void verifyTreeState(Collection<Share> refreshedShares, String[] expectedSelection) throws Exception {
        String oldSeparator = ApplejuiceFassade.separator;
        ApplejuiceFassade.separator = "/";
        AtomicReference<JTreeTable> tableReference = new AtomicReference<>();
        try {
            SwingUtilities.invokeAndWait(() -> {
                ShareTableModel model = new ShareTableModel(new ShareNode(null, null));
                model.setShares(Arrays.asList(share(1, "/folder/sub/file.bin"), share(2, "/other/file.bin")));
                JTreeTable table = new JTreeTable(model, new DefaultTreeTableCellRenderer(model));
                tableReference.set(table);
                JTree tree = table.getTree();
                tree.expandPath(path(tree, "folder", "sub"));
                tree.setSelectionPath(path(tree, "folder", "sub", "file.bin"));
                ShareTreeState.refresh(table, refreshedShares);
            });
            SwingUtilities.invokeAndWait(() -> {
                JTreeTable table = tableReference.get();
                JTree tree = table.getTree();
                if (expectedSelection == null) {
                    assertNull(tree.getSelectionPath());
                    assertEquals(-1, table.getSelectedRow());
                } else {
                    TreePath expectedPath = path(tree, expectedSelection);
                    assertEquals(expectedPath, tree.getSelectionPath());
                    assertEquals(expectedPath, tree.getLeadSelectionPath());
                    assertEquals(tree.getRowForPath(expectedPath), table.getSelectedRow());
                    assertTrue(tree.isExpanded(path(tree, "folder")));
                    if (expectedSelection.length > 1) {
                        assertTrue(tree.isExpanded(path(tree, "folder", "sub")));
                    }
                }
                TreePath otherPath = path(tree, "other");
                if (otherPath != null) {
                    assertFalse(tree.isExpanded(otherPath));
                }
            });
        } finally {
            ApplejuiceFassade.separator = oldSeparator;
        }
    }

    private TreePath path(JTree tree, String... names) {
        TreePath result = new TreePath(tree.getModel().getRoot());
        for (String name : names) {
            Object parent = result.getLastPathComponent();
            Object matchingChild = null;
            for (int index = 0; index < tree.getModel().getChildCount(parent); index++) {
                Object child = tree.getModel().getChild(parent, index);
                if (name.equals(child.toString())) {
                    matchingChild = child;
                    break;
                }
            }
            if (matchingChild == null) {
                return null;
            }
            result = result.pathByAddingChild(matchingChild);
        }
        return result;
    }
}
