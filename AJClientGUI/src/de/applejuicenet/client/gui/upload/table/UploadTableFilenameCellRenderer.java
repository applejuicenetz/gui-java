/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.upload.table;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import de.applejuicenet.client.fassade.entity.Upload;
import de.applejuicenet.client.shared.IconManager;

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
