package de.applejuicenet.client.gui.tables.share;

import java.awt.*;
import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/share/Attic/ShareTreeCellRenderer.java,v 1.1 2003/08/25 19:28:52 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: ShareTreeCellRenderer.java,v $
 * Revision 1.1  2003/08/25 19:28:52  maj0r
 * Anpassungen an muhs neuen Tree.
 *
 * Revision 1.2  2003/07/02 13:54:34  maj0r
 * JTreeTable komplett überarbeitet.
 *
 * Revision 1.1  2003/07/01 18:41:39  maj0r
 * Struktur verändert.
 *
 * Revision 1.3  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class ShareTreeCellRenderer
    extends DefaultTreeCellRenderer {
  public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean selected, boolean expanded,
                                                  boolean leaf, int row, boolean hasFocus) {
    Component c = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

/*
    JLabel aLabel = new JLabel();
    if (column == 1) {
      aLabel.setFont(table.getFont());
      aLabel.setHorizontalAlignment(SwingConstants.RIGHT);
      aLabel.setOpaque(true);
      if (node.getPath().length()==0){
          aLabel.setText( (String) value);
      }
    }
    if (isSelected) {
      aLabel.setBackground(table.getSelectionBackground());
      aLabel.setForeground(table.getSelectionForeground());
    }
    else {
      aLabel.setBackground(table.getBackground());
      aLabel.setForeground(table.getForeground());
    }
    return aLabel;*/
      return c;
  }
}
