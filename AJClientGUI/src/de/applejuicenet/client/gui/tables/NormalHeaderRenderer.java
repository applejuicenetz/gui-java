package de.applejuicenet.client.gui.tables;

import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/Attic/NormalHeaderRenderer.java,v 1.2 2004/10/06 12:29:14 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class NormalHeaderRenderer
        extends JButton
        implements TableCellRenderer {

        private static final long serialVersionUID = 5947008673356832365L;
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