package de.applejuicenet.client.gui.share.tree;

import de.applejuicenet.client.gui.components.treetable.Node;

import javax.swing.*;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;
import com.formdev.flatlaf.util.UIScale;
import java.awt.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/share/tree/ShareSelectionTreeCellRenderer.java,v 1.2 2004/11/22 16:25:26 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r [aj@tkl-soft.de]
 *
 */

public class ShareSelectionTreeCellRenderer
    extends DefaultTreeCellRenderer
    implements TreeCellRenderer {
    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean sel, boolean expanded,
                                                  boolean leaf,
                                                  int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        setFont(tree.getFont());
        Icon icon = value instanceof Node ? ((Node) value).getConvenientIcon() : getIcon();
        if (value instanceof DirectoryNode) {
            Icon shareIcon = ((DirectoryNode) value).getShareModeIcon();
            setIcon(new ShareIcons(shareIcon, icon));
        } else {
            setIcon(icon);
        }
        return this;
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
