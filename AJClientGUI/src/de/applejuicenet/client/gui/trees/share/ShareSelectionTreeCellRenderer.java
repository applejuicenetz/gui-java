package de.applejuicenet.client.gui.trees.share;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.TreeCellRenderer;

import de.applejuicenet.client.gui.tables.Node;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/trees/share/Attic/ShareSelectionTreeCellRenderer.java,v 1.10 2004/10/14 08:57:55 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */

public class ShareSelectionTreeCellRenderer
    extends JPanel
    implements TreeCellRenderer {
    private static final long serialVersionUID = -4457414372324613269L;
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
