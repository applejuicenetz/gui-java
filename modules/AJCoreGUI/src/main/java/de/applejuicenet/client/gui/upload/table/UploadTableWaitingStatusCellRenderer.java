/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.upload.table;

import de.applejuicenet.client.facade.entity.Upload;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.shared.IconManager;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class UploadTableWaitingStatusCellRenderer extends DefaultTableCellRenderer
{
   private static Icon      connectionUnknownIcon = IconManager.getInstance().getIcon("verbindungUnbekannt");
   private static Icon      indirectConnectedIcon = IconManager.getInstance().getIcon("treeWarteschlange");
   private static Icon      tryingIndirectIcon  = IconManager.getInstance().getIcon("treeIndirekt");
   private static ImageIcon directConnectedIcon   = IconManager.getInstance().getIcon("treeUebertrage");

   @Override
   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                                  int column)
   {
      Icon             icon;
      String           text;
      LanguageSelector ls = LanguageSelector.getInstance();

      switch((Integer) value)
      {

         case Upload.STATE_DIRECT_CONNECTED:
            icon = directConnectedIcon;
            text = ls.getFirstAttributeByTagName("mainform.uploads.directconnection");
            break;

         case Upload.STATE_INDIRECT_CONNECTED:
            icon = indirectConnectedIcon;
            text = ls.getFirstAttributeByTagName("mainform.uploads.indirectconnection");
            break;

         default:
            icon = connectionUnknownIcon;
            text = ls.getFirstAttributeByTagName("mainform.uploads.connectionunknown");
      }

      JLabel label = (JLabel) super.getTableCellRendererComponent(table, text, isSelected, hasFocus, row, column);

      label.setIcon(icon);
      return label;
   }
}
