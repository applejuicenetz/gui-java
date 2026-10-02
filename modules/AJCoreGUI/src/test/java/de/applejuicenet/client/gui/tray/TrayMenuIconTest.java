package de.applejuicenet.client.gui.tray;

import org.junit.Test;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class TrayMenuIconTest {
    @Test
    public void limitsAppIconWithoutChangingOriginal() {
        Icon original = icon(128, 128);
        Icon menuIcon = TrayMenuIcon.fit(original);
        assertEquals(16, menuIcon.getIconWidth());
        assertEquals(16, menuIcon.getIconHeight());
        assertEquals(128, original.getIconWidth());
        assertEquals(128, original.getIconHeight());
    }

    @Test
    public void preservesSmallAndMissingIcons() {
        Icon original = icon(16, 16);
        assertSame(original, TrayMenuIcon.fit(original));
        assertNull(TrayMenuIcon.fit(null));
    }

    @Test
    public void preservesAspectRatio() {
        Icon menuIcon = TrayMenuIcon.fit(icon(128, 64));
        assertEquals(16, menuIcon.getIconWidth());
        assertEquals(8, menuIcon.getIconHeight());
    }

    @Test
    public void paintsOnlyInsideMenuIconBounds() {
        BufferedImage rendered = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = rendered.createGraphics();
        try {
            TrayMenuIcon.fit(icon(128, 128)).paintIcon(null, graphics, 4, 4);
        } finally {
            graphics.dispose();
        }
        assertEquals(Color.RED.getRGB(), rendered.getRGB(4, 4));
        assertEquals(Color.RED.getRGB(), rendered.getRGB(19, 19));
        assertEquals(0, rendered.getRGB(20, 4));
        assertEquals(0, rendered.getRGB(3, 4));
    }

    @Test
    public void respectsHighDpiGraphicsTransform() {
        BufferedImage rendered = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = rendered.createGraphics();
        try {
            graphics.scale(2, 2);
            TrayMenuIcon.fit(icon(128, 128)).paintIcon(null, graphics, 0, 0);
        } finally {
            graphics.dispose();
        }
        assertEquals(Color.RED.getRGB(), rendered.getRGB(31, 31));
        assertEquals(0, rendered.getRGB(32, 0));
    }

    @Test
    public void keepsMenuItemAtNormalSize() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JMenuItem normal = new JMenuItem("Zeigen", icon(16, 16));
            JMenuItem scaled = new JMenuItem("Zeigen", TrayMenuIcon.fit(icon(128, 128)));
            assertEquals(normal.getPreferredSize(), scaled.getPreferredSize());
        });
    }

    private ImageIcon icon(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try {
            graphics.setColor(Color.RED);
            graphics.fillRect(0, 0, width, height);
        } finally {
            graphics.dispose();
        }
        return new ImageIcon(image);
    }
}
