/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.components.table;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/components/table/NormalHeaderRenderer.java,v 1.3 2009/01/21 14:45:10 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 *
 */
public class NormalHeaderRenderer extends JButton implements TableCellRenderer
{
   private static Font textFont = new JTable().getFont();

   public NormalHeaderRenderer()
   {
      setMargin(new Insets(0, 0, 0, 0));
      setHorizontalTextPosition(LEFT);
   }

   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                                  int column)
   {
      setText((value == null) ? "" : value.toString());
      setFont(textFont);
      return this;
   }
}
