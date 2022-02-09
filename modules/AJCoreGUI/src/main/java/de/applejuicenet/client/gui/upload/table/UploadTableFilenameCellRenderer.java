/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.upload.table;

import de.applejuicenet.client.fassade.entity.Upload;
import de.applejuicenet.client.shared.IconManager;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class UploadTableFilenameCellRenderer extends DefaultTableCellRenderer
{
   @Override
   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                                  int column)
   {
      Upload upload = (Upload) value;
      JLabel label = (JLabel) super.getTableCellRendererComponent(table, upload.getDateiName(), isSelected, hasFocus, row, column);

      label.setIcon(IconManager.getInstance().getIcon("treeUebertrage"));
      return label;
   }
}
