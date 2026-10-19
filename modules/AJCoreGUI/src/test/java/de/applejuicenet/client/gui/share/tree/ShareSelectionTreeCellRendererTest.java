package de.applejuicenet.client.gui.share.tree;

import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class ShareSelectionTreeCellRendererTest {
    @BeforeClass
    public static void loadLanguage() {
        de.applejuicenet.client.gui.controller.LanguageSelector.getInstance(
                Paths.get("../../resources/language/deutsch.properties").toAbsolutePath().normalize().toString());
    }

    @Test
    public void sharedFoldersGetDistinctBackgroundAndBoldFont() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JTree tree = new JTree();
            ShareSelectionTreeCellRenderer renderer = new ShareSelectionTreeCellRenderer();
            Color treeBackground = tree.getBackground();

            for (int mode : new int[] {DirectoryNode.SHARED_WITH_SUB, DirectoryNode.SHARED_WITHOUT_SUB}) {
                Component cell = renderer.getTreeCellRendererComponent(tree, node(mode), false, true, false, 0, false);
                assertTrue("mode " + mode + " must be highlighted", cell.isOpaque());
                assertNotEquals("mode " + mode + " needs its own color", treeBackground, cell.getBackground());
                assertTrue("mode " + mode + " must be bold", cell.getFont().isBold());
            }

            for (int mode : new int[] {DirectoryNode.SHARED_SUB, DirectoryNode.SHARED_SOMETHING}) {
                Component cell = renderer.getTreeCellRendererComponent(tree, node(mode), false, true, false, 0, false);
                assertEquals("chain mode " + mode + " must stay plain", treeBackground, cell.getBackground());
                assertFalse("chain mode " + mode + " must not be bold", cell.getFont().isBold());
            }

            Component plain = renderer.getTreeCellRendererComponent(tree, node(DirectoryNode.NOT_SHARED),
                    false, true, false, 0, false);
            assertEquals(treeBackground, plain.getBackground());
            assertFalse(plain.getFont().isBold());

            renderer.getTreeCellRendererComponent(tree, node(DirectoryNode.SHARED_WITH_SUB),
                    true, true, false, 0, true);
            Color selectedShared = renderer.getBackground();
            renderer.getTreeCellRendererComponent(tree, node(DirectoryNode.NOT_SHARED),
                    true, true, false, 0, true);
            Color selectedPlain = renderer.getBackground();
            assertNotEquals("selection must keep the share colour", selectedPlain, selectedShared);
        });
    }

    @Test
    public void bothShareModesUseTheSameOrange() {
        Color base = Color.WHITE;
        assertNull(ShareSelectionTreeCellRenderer.shareBackground(base, DirectoryNode.NOT_SHARED));
        assertNull(ShareSelectionTreeCellRenderer.shareBackground(base, DirectoryNode.SHARED_SUB));
        assertNull(ShareSelectionTreeCellRenderer.shareBackground(base, DirectoryNode.SHARED_SOMETHING));
        assertEquals(ShareSelectionTreeCellRenderer.shareBackground(base, DirectoryNode.SHARED_WITH_SUB),
                ShareSelectionTreeCellRenderer.shareBackground(base, DirectoryNode.SHARED_WITHOUT_SUB));
        Color light = ShareSelectionTreeCellRenderer.shareBackground(Color.WHITE, DirectoryNode.SHARED_WITH_SUB);
        // matte orange: more red than green, more green than blue
        assertTrue(light.getRed() > light.getGreen() && light.getGreen() > light.getBlue());
        assertTrue(light.getRed() > 200);
        assertNotNull(ShareSelectionTreeCellRenderer.shareBackground(Color.BLACK, DirectoryNode.SHARED_WITH_SUB));
    }

    @Test
    public void riskySharesUseARedMarker() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JTree tree = new JTree();
            ShareSelectionTreeCellRenderer renderer = new ShareSelectionTreeCellRenderer();
            Component cell = renderer.getTreeCellRendererComponent(tree, warningNode(), false, true, false, 0, false);
            Color warning = cell.getBackground();
            Color share = ShareSelectionTreeCellRenderer.shareBackground(tree.getBackground(),
                    DirectoryNode.SHARED_WITH_SUB);

            assertNotEquals("warning must differ from the orange share colour", share, warning);
            assertTrue("warning must look red", warning.getRed() > warning.getGreen()
                    && warning.getGreen() >= warning.getBlue());
            assertTrue("warning must be bold", cell.getFont().isBold());
        });
    }

    @Test
    public void warningMarkerDependsOnTheBlinkPhaseOfTheShareTree() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            DirectoryTree tree = new DirectoryTree();
            ShareSelectionTreeCellRenderer renderer = new ShareSelectionTreeCellRenderer();

            assertTrue(tree.isWarningVisible());
            Component cell = renderer.getTreeCellRendererComponent(tree, warningNode(), false, true, false, 0, false);
            assertEquals(ShareSelectionTreeCellRenderer.warningBackground(tree.getBackground()), cell.getBackground());
            tree.setWarningBlinking(false);
            assertTrue(tree.isWarningVisible());
        });
    }

    private static DirectoryNode node(int mode) {
        return new DirectoryNode(null, null) {
            @Override public int getShareMode() {
                return mode;
            }
        };
    }

    private static DirectoryNode warningNode() {
        return new DirectoryNode(null, null) {
            @Override public int getShareMode() {
                return DirectoryNode.SHARED_WITH_SUB;
            }

            @Override public boolean isWarningShare() {
                return true;
            }
        };
    }
}
