package de.applejuicenet.client.gui.controller;

import de.applejuicenet.client.gui.components.table.AutoRowHeightTable;
import de.applejuicenet.client.gui.download.table.DownloadTableFilenameCellRenderer;
import de.applejuicenet.client.gui.options.directorytree.DirectoryChooserTreeCellRenderer;
import org.junit.Test;

import javax.swing.*;
import java.awt.Font;
import java.awt.image.BufferedImage;

import static org.junit.Assert.*;

public class ViewRenderingTest {
    @Test
    public void rowHeightFitsIconsAndTracksFontChanges() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            AutoRowHeightTable table = new AutoRowHeightTable();
            Font original = table.getFont();
            Icon icon = new ImageIcon(new BufferedImage(24, 48, BufferedImage.TYPE_INT_ARGB));
            table.setRowIcons(null, icon);
            assertTrue(table.getRowHeight() >= 48);
            table.updateUI();
            assertTrue(table.getRowHeight() >= 48);
            table.setRowIcons();
            table.setFont(original.deriveFont(64f));
            assertTrue(table.getRowHeight() >= table.getFontMetrics(table.getFont()).getHeight());
            int largeHeight = table.getRowHeight();
            table.setFont(original);
            assertTrue(table.getRowHeight() < largeHeight);
        });
    }

    @Test
    public void emptyFilenameDoesNotRetainPreviousIcon() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            DownloadTableFilenameCellRenderer renderer = new DownloadTableFilenameCellRenderer();
            renderer.setIcon(new ImageIcon(new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB)));
            JLabel label = (JLabel) renderer.getTableCellRendererComponent(new JTable(), null,
                    false, false, 0, 0);
            assertNull(label.getIcon());
        });
    }

    @Test
    public void directoryRendererAcceptsOrdinaryTreeNodes() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JTree tree = new JTree();
            DirectoryChooserTreeCellRenderer renderer = new DirectoryChooserTreeCellRenderer();
            assertSame(renderer, renderer.getTreeCellRendererComponent(tree, tree.getModel().getRoot(),
                    false, true, false, 0, false));
        });
    }
}
