package de.applejuicenet.client.gui.trees.share;

import de.applejuicenet.client.gui.tables.Node;

import javax.swing.tree.TreeCellRenderer;
import javax.swing.*;
import java.awt.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/trees/share/Attic/ShareSelectionTreeCellRenderer.java,v 1.5 2003/08/26 19:46:35 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: ShareSelectionTreeCellRenderer.java,v $
 * Revision 1.5  2003/08/26 19:46:35  maj0r
 * Sharebereich weiter vervollstaendigt.
 *
 * Revision 1.4  2003/08/26 14:04:23  maj0r
 * ShareTree-Event-Behandlung fertiggestellt.
 *
 * Revision 1.3  2003/08/26 09:49:01  maj0r
 * ShareTree weitgehend fertiggestellt.
 *
 * Revision 1.2  2003/08/26 06:20:10  maj0r
 * Anpassungen an muhs neuen Tree.
 *
 * Revision 1.1  2003/08/15 14:43:38  maj0r
 * DirectoryTree eingefügt, aber noch nicht fertiggestellt.
 *
 *
 */

public class ShareSelectionTreeCellRenderer extends JPanel implements TreeCellRenderer {
    private JLabel iconLabel1 = new JLabel();
    private JLabel iconLabel2 = new JLabel();
    private JLabel text = new JLabel();

    public ShareSelectionTreeCellRenderer() {
        iconLabel1.setOpaque(true);
        iconLabel2.setOpaque(true);
        setLayout(new FlowLayout(FlowLayout.LEFT));
        text.setOpaque(true);
        add(iconLabel1);
        add(iconLabel2);
        add(text);
        setBackground(UIManager.getColor("Tree.textBackground"));
        setForeground(UIManager.getColor("Tree.textForeground"));
        iconLabel1.setBackground(UIManager.getColor("Tree.textBackground"));
        iconLabel2.setBackground(UIManager.getColor("Tree.textBackground"));
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean sel, boolean expanded,
                                                  boolean leaf,
                                                  int row, boolean hasFocus) {

        String stringValue = tree.convertValueToText(value, sel,
                                                     expanded, leaf, row, hasFocus);
        setEnabled(tree.isEnabled());
        text.setFont(tree.getFont());
        text.setText(stringValue);
        if (sel)
        {
            Color lineColor = UIManager.getColor("Tree.selectionBorderColor");
            text.setBackground(UIManager.getColor("Tree.selectionBackground"));
            text.setBorder(BorderFactory.createLineBorder(lineColor));
        }
        else
        {
            text.setBackground(UIManager.getColor("Tree.textBackground"));
            text.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
        }
        Icon icon = ((Node) value).getConvenientIcon();
        iconLabel2.setIcon(icon);
        if (value instanceof DirectoryNode)
        {
            Icon icon1 = ((DirectoryNode) value).getShareModeIcon();
            iconLabel1.setIcon(icon1);
        }
        return this;
    }

    public Dimension getPreferredSize() {
        Dimension dimension1 = iconLabel1.getPreferredSize();
        int height = dimension1.height;
        int width = dimension1.width;
        Dimension dimension2 = iconLabel2.getPreferredSize();
        if (dimension2.height > height)
            height = dimension2.height;
        width += dimension2.width;
        Dimension dimension3 = text.getPreferredSize();
        if (dimension3.height > height)
            height = dimension3.height;
        width += dimension3.width;
        return new Dimension(width+15, height+4);
    }
}
