package de.applejuicenet.client.gui.components.table;

import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/components/table/NormalHeaderRenderer.java,v 1.2 2004/11/22 16:25:26 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class NormalHeaderRenderer
        extends JButton
        implements TableCellRenderer {

		private static Font textFont = new JTable().getFont();

        public NormalHeaderRenderer(){
            setMargin(new Insets(0, 0, 0, 0));
            setHorizontalTextPosition(LEFT);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected,
            boolean hasFocus, int row,
            int column) {
        setText( (value == null) ? "" : value.toString());
        setFont(textFont);
        return this;
    }
}