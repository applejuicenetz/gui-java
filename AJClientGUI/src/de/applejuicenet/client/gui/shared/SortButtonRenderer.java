package de.applejuicenet.client.gui.shared;

import java.util.Hashtable;

import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/shared/Attic/SortButtonRenderer.java,v 1.4 2004/02/05 23:11:27 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: SortButtonRenderer.java,v $
 * Revision 1.4  2004/02/05 23:11:27  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.3  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.2  2003/10/12 15:57:55  maj0r
 * Kleinere Bugs behoben.
 * Sortiert wird nun nur noch bei Klick auf den Spaltenkopf um CPU-Zeit zu sparen.
 *
 * Revision 1.1  2003/06/24 14:32:27  maj0r
 * Klassen zum Sortieren von Tabellen eingefügt.
 * Servertabelle kann nun spaltenweise sortiert werden.
 *
 *
 *
 */

public class SortButtonRenderer
    extends JButton
    implements TableCellRenderer {

    public static final int NONE = 0;
    public static final int DOWN = 1;
    public static final int UP = 2;

    private Font textFont;

    private int pushedColumn;
    private Hashtable state;
    private JButton downButton, upButton;
    private boolean isPressed;

    public SortButtonRenderer() {
        pushedColumn = -1;
        state = new Hashtable();

        setMargin(new Insets(0, 0, 0, 0));
        setHorizontalTextPosition(LEFT);
        setIcon(new BlankIcon());

        downButton = new JButton();
        downButton.setMargin(new Insets(0, 0, 0, 0));
        downButton.setHorizontalTextPosition(LEFT);
        downButton.setIcon(new BevelArrowIcon(BevelArrowIcon.DOWN, false, false));
        downButton.setPressedIcon(new BevelArrowIcon(BevelArrowIcon.DOWN, false, true));

        upButton = new JButton();
        upButton.setMargin(new Insets(0, 0, 0, 0));
        upButton.setHorizontalTextPosition(LEFT);
        upButton.setIcon(new BevelArrowIcon(BevelArrowIcon.UP, false, false));
        upButton.setPressedIcon(new BevelArrowIcon(BevelArrowIcon.UP, false, true));

        textFont = new JTable().getFont();
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected,
        boolean hasFocus, int row,
        int column) {
        JButton button = this;
        Object obj = state.get(new Integer(column));
        if (obj != null) {
            if ( ( (Integer) obj).intValue() == DOWN) {
                button = downButton;
            }
            else {
                button = upButton;
            }
        }
        button.setText( (value == null) ? "" : value.toString());
        button.setFont(textFont);
        isPressed = (column == pushedColumn);
        button.getModel().setPressed(isPressed);
        button.getModel().setArmed(isPressed);
        return button;
    }

    public void setPressedColumn(int col) {
        pushedColumn = col;
    }

    public void setSelectedColumn(int col) {
        if (col < 0) {
            return;
        }
        Integer value = null;
        Object obj = state.get(new Integer(col));
        if (obj == null) {
            value = new Integer(DOWN);
        }
        else {
            if ( ( (Integer) obj).intValue() == DOWN) {
                value = new Integer(UP);
            }
            else {
                value = new Integer(DOWN);
            }
        }
        state.clear();
        state.put(new Integer(col), value);
    }

    public int getState(int col) {
        int retValue;
        Object obj = state.get(new Integer(col));
        if (obj == null) {
            retValue = NONE;
        }
        else {
            if ( ( (Integer) obj).intValue() == DOWN) {
                retValue = DOWN;
            }
            else {
                retValue = UP;
            }
        }
        return retValue;
    }
}