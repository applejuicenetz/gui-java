package de.applejuicenet.client.gui.share.table;

import de.applejuicenet.client.fassade.entity.Share;
import de.applejuicenet.client.gui.components.treetable.JTreeTable;

import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

final class ShareTreeState {
    private ShareTreeState() {
    }

    static void refresh(JTreeTable table, Collection<Share> shares) {
        JTree tree = table.getTree();
        ShareTableModel model = (ShareTableModel) tree.getModel();
        List<TreePath> expandedPaths = new ArrayList<>();
        Enumeration<TreePath> expanded = tree.getExpandedDescendants(new TreePath(model.getRoot()));
        if (expanded != null) {
            while (expanded.hasMoreElements()) {
                expandedPaths.add(expanded.nextElement());
            }
        }
        TreePath[] selectedPaths = tree.getSelectionPaths();
        TreePath leadPath = tree.getLeadSelectionPath();
        JViewport viewport = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, table);
        Point viewPosition = viewport == null ? null : viewport.getViewPosition();

        model.setShares(shares);
        Object newRoot = model.getRoot();
        for (TreePath expandedPath : expandedPaths) {
            TreePath restored = resolve(model, expandedPath, false);
            if (restored != null) {
                tree.expandPath(restored);
            }
        }
        List<TreePath> restoredSelection = new ArrayList<>();
        if (selectedPaths != null) {
            for (TreePath selectedPath : selectedPaths) {
                TreePath restored = resolve(model, selectedPath, true);
                if (restored != null && !restoredSelection.contains(restored)) {
                    restoredSelection.add(restored);
                }
            }
        }
        TreePath restoredLead = leadPath == null ? null : resolve(model, leadPath, true);
        boolean leadRemoved = leadPath != null && resolve(model, leadPath, false) == null;
        SwingUtilities.invokeLater(() -> {
            if (model.getRoot() != newRoot) {
                return;
            }
            tree.setSelectionPaths(restoredSelection.toArray(new TreePath[0]));
            tree.setLeadSelectionPath(restoredLead);
            if (viewport != null) {
                int maxX = Math.max(0, table.getPreferredSize().width - viewport.getExtentSize().width);
                int maxY = Math.max(0, table.getPreferredSize().height - viewport.getExtentSize().height);
                viewport.setViewPosition(new Point(Math.min(viewPosition.x, maxX), Math.min(viewPosition.y, maxY)));
            }
            if (leadRemoved && restoredLead != null) {
                int row = tree.getRowForPath(restoredLead);
                if (row >= 0) {
                    table.scrollRectToVisible(table.getCellRect(row, 0, true));
                }
            }
        });
    }

    private static TreePath resolve(ShareTableModel model, TreePath oldPath, boolean useAncestor) {
        TreePath result = new TreePath(model.getRoot());
        Object[] components = oldPath.getPath();
        for (int index = 1; index < components.length; index++) {
            ShareNode oldNode = (ShareNode) components[index];
            ShareNode matchingNode = null;
            Object parent = result.getLastPathComponent();
            for (int childIndex = 0; childIndex < model.getChildCount(parent); childIndex++) {
                ShareNode child = (ShareNode) model.getChild(parent, childIndex);
                if (child.isLeaf() == oldNode.isLeaf() && child.toString().equals(oldNode.toString())) {
                    matchingNode = child;
                    break;
                }
            }
            if (matchingNode == null) {
                return useAncestor && result.getPathCount() > 1 ? result : null;
            }
            result = result.pathByAddingChild(matchingNode);
        }
        return result;
    }
}
