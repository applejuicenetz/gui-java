package de.applejuicenet.client.gui.plugins.ircplugin;

import javax.swing.ListCellRenderer;
import java.awt.Component;
import javax.swing.JList;
import javax.swing.JLabel;
import java.awt.Color;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class UserListCellRenderer implements ListCellRenderer {
  public Component getListCellRendererComponent(
         JList list,
         Object value,
         int index,
         boolean isSelected,
         boolean cellHasFocus){


    list.getModel().getElementAt(index);
    JLabel aLabel = null;
    aLabel = new JLabel();
    aLabel.setFont(list.getFont());
    aLabel.setOpaque(true);
    String inhalt = (String)value;
    aLabel.setText(inhalt);
    if (isSelected){
      aLabel.setBackground(list.getSelectionBackground());
      aLabel.setForeground(list.getSelectionForeground());
    }
    else{
      aLabel.setForeground(list.getForeground());
      if (inhalt.substring(0, 1).compareTo("!")==0)
        aLabel.setBackground(Color.RED);
      else  if (inhalt.substring(0, 1).compareTo("@")==0)
        aLabel.setBackground(Color.YELLOW);
      else  if (inhalt.substring(0, 1).compareTo("%")==0)
        aLabel.setBackground(Color.GREEN);
      else if (inhalt.substring(0, 1).compareTo("+")==0)
        aLabel.setBackground(list.getBackground());
      else
        aLabel.setBackground(Color.LIGHT_GRAY);
    }
    return aLabel;
  }
}
