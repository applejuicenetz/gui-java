package de.applejuicenet.client.gui.plugins.ircplugin;

import javax.swing.ListCellRenderer;
import java.awt.Component;
import javax.swing.JList;
import javax.swing.JLabel;
import java.awt.Color;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/ircplugin/src/de/applejuicenet/client/gui/plugins/ircplugin/UserListCellRenderer.java,v 1.1 2003/09/12 06:32:17 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: UserListCellRenderer.java,v $
 * Revision 1.1  2003/09/12 06:32:17  maj0r
 * Nur verschoben.
 *
 * Revision 1.2  2003/08/28 15:53:02  maj0r
 * NullPointer behoben und Header eingefuegt.
 *
 *
 */


public class UserListCellRenderer implements ListCellRenderer {
    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {

        JLabel aLabel = null;
        aLabel = new JLabel();
        aLabel.setFont(list.getFont());
        aLabel.setOpaque(true);
        String inhalt = (String) value;
        aLabel.setText(inhalt);
        if (isSelected)
        {
            aLabel.setBackground(list.getSelectionBackground());
            aLabel.setForeground(list.getSelectionForeground());
        }
        else
        {
            aLabel.setForeground(list.getForeground());
            if (inhalt.substring(0, 1).compareTo("!") == 0)
                aLabel.setBackground(Color.RED);
            else if (inhalt.substring(0, 1).compareTo("@") == 0)
                aLabel.setBackground(Color.YELLOW);
            else if (inhalt.substring(0, 1).compareTo("%") == 0)
                aLabel.setBackground(Color.GREEN);
            else if (inhalt.substring(0, 1).compareTo("+") == 0)
                aLabel.setBackground(list.getBackground());
            else
                aLabel.setBackground(Color.LIGHT_GRAY);
        }
        return aLabel;
    }
}
