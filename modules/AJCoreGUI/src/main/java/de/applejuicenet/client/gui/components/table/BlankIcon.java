package de.applejuicenet.client.gui.components.table;

import javax.swing.*;
import java.awt.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/components/table/BlankIcon.java,v 1.1 2004/10/29 11:58:43 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 *
 */

public class BlankIcon
    implements Icon {
    private Color fillColor;
    private int size;

    public BlankIcon() {
        this(null, 11);
    }

    public BlankIcon(Color color, int size) {
        fillColor = color;
        this.size = size;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        if (fillColor != null) {
            g.setColor(fillColor);
            g.drawRect(x, y, size - 1, size - 1);
        }
    }

    public int getIconWidth() {
        return size;
    }

    public int getIconHeight() {
        return size;
    }
}
