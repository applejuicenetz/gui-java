package de.applejuicenet.client.gui.share.tree;

import de.applejuicenet.client.gui.components.treetable.Node;

import javax.swing.*;
import javax.swing.tree.TreeCellRenderer;
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
    extends JPanel
    implements TreeCellRenderer {
	private JLabel iconLabel1 = new JLabel();
    private JLabel iconLabel2 = new JLabel();
    private JLabel text = new JLabel();

    public ShareSelectionTreeCellRenderer() {
        iconLabel1.setOpaque(true);
        iconLabel2.setOpaque(true);
        setLayout(new FlowLayout(FlowLayout.LEFT));
        text.setOpaque(true);
        setOpaque(true);
        add(iconLabel1);
        add(iconLabel2);
        add(text);
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean sel, boolean expanded,
                                                  boolean leaf,
                                                  int row, boolean hasFocus) {
        setBackground(UIManager.getColor("Tree.textBackground"));
        iconLabel1.setBackground(UIManager.getColor("Tree.textBackground"));
        iconLabel2.setBackground(UIManager.getColor("Tree.textBackground"));
        String stringValue = tree.convertValueToText(value, sel,
            expanded, leaf, row, hasFocus);
        setEnabled(tree.isEnabled());
        text.setFont(tree.getFont());
        text.setText(stringValue);
        if (sel) {
            Color lineColor = UIManager.getColor("Tree.selectionBorderColor");
            text.setBackground(UIManager.getColor("Tree.selectionBackground"));
            text.setBorder(BorderFactory.createLineBorder(lineColor));
        }
        else {
            text.setBackground(UIManager.getColor("Tree.textBackground"));
            text.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        }
        Icon icon = ( (Node) value).getConvenientIcon();
        iconLabel2.setIcon(icon);
        if (value instanceof DirectoryNode) {
            Icon icon1 = ( (DirectoryNode) value).getShareModeIcon();
            iconLabel1.setIcon(icon1);
        }
        return this;
    }
}
