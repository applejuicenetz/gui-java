package de.applejuicenet.client.gui.share.tree;

import de.applejuicenet.client.gui.components.treetable.Node;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;
import com.formdev.flatlaf.util.UIScale;
import java.awt.*;

/**
 * Copyright: General Public License
 */

public class ShareSelectionTreeCellRenderer
    extends DefaultTreeCellRenderer
    implements TreeCellRenderer {
    /** Light matte orange blended over the tree background so it works in light and dark themes. */
    private static final Color SHARED_TINT = new Color(0xffb74d);
    private static final float TINT_STRENGTH = 0.32f;

    static Color shareBackground(Color background, int shareMode) {
        return blend(background, tintFor(shareMode), TINT_STRENGTH);
    }

    /**
     * Only the folders that are shared themselves get the tint. Descendants of a shared folder
     * ({@code SHARED_SUB}) and ancestors of a share ({@code SHARED_SOMETHING}) keep the plain
     * background so the colour marks the actual share entry.
     */
    private static Color tintFor(int shareMode) {
        switch (shareMode) {
            case DirectoryNode.SHARED_WITH_SUB:
            case DirectoryNode.SHARED_WITHOUT_SUB:
                return SHARED_TINT;
            default:
                return null;
        }
    }

    private static Color blend(Color background, Color tint, float strength) {
        if (tint == null) {
            return null;
        }
        int red = Math.round(background.getRed() * (1 - strength) + tint.getRed() * strength);
        int green = Math.round(background.getGreen() * (1 - strength) + tint.getGreen() * strength);
        int blue = Math.round(background.getBlue() * (1 - strength) + tint.getBlue() * strength);

        return new Color(Math.min(red, 255), Math.min(green, 255), Math.min(blue, 255));
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean isSelected, boolean expanded,
                                                  boolean leaf,
                                                  int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, isSelected, expanded, leaf, row, hasFocus);
        setFont(tree.getFont());
        Icon icon = value instanceof Node ? ((Node) value).getConvenientIcon() : getIcon();
        if (value instanceof DirectoryNode) {
            DirectoryNode directoryNode = (DirectoryNode) value;
            Icon shareIcon = directoryNode.getShareModeIcon();
            setIcon(new ShareIcons(shareIcon, icon));

            int shareMode = directoryNode.getShareMode();
            Color background = cellBackground(tree, isSelected);
            Color shareBackground = shareBackground(background, shareMode);

            setOpaque(true);
            setBackground(shareBackground != null ? shareBackground : background);
            if (shareBackground != null) {
                setFont(tree.getFont().deriveFont(Font.BOLD));
            }
        } else {
            setIcon(icon);
            setOpaque(true);
            setBackground(cellBackground(tree, isSelected));
        }
        return this;
    }

    /**
     * Background for this cell state: the theme selection colour when selected, the tree
     * background otherwise. Passed through the share tint so the marker survives selection.
     */
    private static Color cellBackground(JTree tree, boolean isSelected) {
        Color background = null;

        if (isSelected) {
            background = UIManager.getColor("Tree.selectionBackground");
        }
        if (background == null) {
            background = tree.getBackground();
        }
        if (background == null) {
            background = UIManager.getColor("Tree.background");
        }
        if (background == null) {
            background = Color.WHITE;
        }
        // DefaultTreeCellRenderer drops ColorUIResource backgrounds to keep the LAF default,
        // so hand it a plain colour that actually shows up in the tree.
        return background instanceof ColorUIResource ? new Color(background.getRGB()) : background;
    }

    private static class ShareIcons implements Icon {
        private final Icon shareIcon;
        private final Icon folderIcon;
        private final int gap = UIScale.scale(4);

        private ShareIcons(Icon shareIcon, Icon folderIcon) {
            this.shareIcon = shareIcon;
            this.folderIcon = folderIcon;
        }

        public int getIconWidth() {
            return (shareIcon == null ? 0 : shareIcon.getIconWidth())
                + (folderIcon == null ? 0 : folderIcon.getIconWidth())
                + (shareIcon == null || folderIcon == null ? 0 : gap);
        }

        public int getIconHeight() {
            return Math.max(shareIcon == null ? 0 : shareIcon.getIconHeight(),
                folderIcon == null ? 0 : folderIcon.getIconHeight());
        }

        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            if (shareIcon != null) {
                shareIcon.paintIcon(component, graphics, x,
                    y + (getIconHeight() - shareIcon.getIconHeight()) / 2);
            }
            if (folderIcon != null) {
                int offset = shareIcon == null ? 0 : shareIcon.getIconWidth() + gap;
                folderIcon.paintIcon(component, graphics, x + offset,
                    y + (getIconHeight() - folderIcon.getIconHeight()) / 2);
            }
        }
    }
}
