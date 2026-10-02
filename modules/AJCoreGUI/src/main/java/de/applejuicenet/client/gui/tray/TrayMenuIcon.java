package de.applejuicenet.client.gui.tray;

import javax.swing.Icon;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

final class TrayMenuIcon implements Icon {
    private static final int MAX_SIZE = 16;
    private final Icon source;
    private final int width;
    private final int height;

    private TrayMenuIcon(Icon source) {
        this.source = source;
        double scale = Math.min((double) MAX_SIZE / source.getIconWidth(),
                (double) MAX_SIZE / source.getIconHeight());
        width = Math.max(1, (int) Math.round(source.getIconWidth() * scale));
        height = Math.max(1, (int) Math.round(source.getIconHeight() * scale));
    }

    static Icon fit(Icon source) {
        if (source == null || source.getIconWidth() <= MAX_SIZE && source.getIconHeight() <= MAX_SIZE) {
            return source;
        }
        return new TrayMenuIcon(source);
    }

    @Override
    public int getIconWidth() {
        return width;
    }

    @Override
    public int getIconHeight() {
        return height;
    }

    @Override
    public void paintIcon(Component component, Graphics graphics, int locationX, int locationY) {
        Graphics2D scaled = (Graphics2D) graphics.create(locationX, locationY, width, height);
        try {
            scaled.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            scaled.scale((double) width / source.getIconWidth(), (double) height / source.getIconHeight());
            source.paintIcon(component, scaled, 0, 0);
        } finally {
            scaled.dispose();
        }
    }
}
