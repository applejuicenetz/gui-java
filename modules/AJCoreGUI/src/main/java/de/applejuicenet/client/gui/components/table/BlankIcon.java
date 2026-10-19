package de.applejuicenet.client.gui.components.table;

import javax.swing.*;
import java.awt.*;

/**
 * Copyright: General Public License
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
