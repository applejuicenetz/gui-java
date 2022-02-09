/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.upload.table;

import de.applejuicenet.client.fassade.entity.Upload;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.shared.IconManager;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class UploadTableWaitingStatusCellRenderer extends DefaultTableCellRenderer
{
   private static Icon      verbindungUnbekanntIcon = IconManager.getInstance().getIcon("verbindungUnbekannt");
   private static Icon      indirektVerbundenIcon = IconManager.getInstance().getIcon("treeWarteschlange");
   private static Icon      versucheIndirektIcon  = IconManager.getInstance().getIcon("treeIndirekt");
   private static ImageIcon direktVerbundenIcon   = IconManager.getInstance().getIcon("treeUebertrage");

   @Override
   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                                  int column)
   {
      Icon             icon;
      String           text;
      LanguageSelector ls = LanguageSelector.getInstance();

      switch((Integer) value)
      {

         case Upload.STATE_DIREKT_VERBUNDEN:
            icon = direktVerbundenIcon;
            text = ls.getFirstAttrbuteByTagName("mainform.uploads.direkteverbindung");
            break;

         case Upload.STATE_INDIREKT_VERBUNDEN:
            icon = indirektVerbundenIcon;
            text = ls.getFirstAttrbuteByTagName("mainform.uploads.indirekteverbindung");
            break;

         default:
            icon = verbindungUnbekanntIcon;
            text = ls.getFirstAttrbuteByTagName("mainform.uploads.verbindungunbekannt");
      }

      JLabel label = (JLabel) super.getTableCellRendererComponent(table, text, isSelected, hasFocus, row, column);

      label.setIcon(icon);
      return label;
   }
}
