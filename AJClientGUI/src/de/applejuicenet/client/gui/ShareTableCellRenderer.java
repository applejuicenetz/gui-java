package de.applejuicenet.client.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

import de.applejuicenet.client.shared.dac.*;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class ShareTableCellRenderer
    implements TableCellRenderer {
  public Component getTableCellRendererComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 boolean hasFocus,
                                                 int row,
                                                 int column) {

    ShareDO share = (ShareDO) ( (ShareTableModel) table.getModel()).getRow(row);
    JLabel aLabel = null;
    if (column == 1) {
      aLabel = new JLabel();
      aLabel.setFont(table.getFont());
      aLabel.setHorizontalAlignment(SwingConstants.RIGHT);
      aLabel.setOpaque(true);
      aLabel.setText( (String) value);
    }
    if (isSelected) {
      aLabel.setBackground(table.getSelectionBackground());
      aLabel.setForeground(table.getSelectionForeground());
    }
    else {
      aLabel.setBackground(table.getBackground());
      aLabel.setForeground(table.getForeground());
    }
    return aLabel;
  }
}
