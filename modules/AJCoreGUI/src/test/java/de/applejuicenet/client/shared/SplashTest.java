package de.applejuicenet.client.shared;

import de.applejuicenet.client.gui.components.listener.KeyStates;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeFalse;

public class SplashTest {
    @Before
    public void requireDisplay() {
        assumeFalse(GraphicsEnvironment.isHeadless());
    }

    @Test
    public void showsOnlyOriginalLogoAtNaturalSize() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            ImageIcon icon = new ImageIcon(new BufferedImage(320, 180, BufferedImage.TYPE_INT_ARGB));
            Splash splash = new Splash(null, icon);
            try {
                assertTrue(splash.isUndecorated());
                assertFalse(splash.isModal());
                assertEquals(new Dimension(320, 180), splash.getSize());
                assertTrue(splash.getContentPane() instanceof JLabel);
                assertSame(icon, ((JLabel) splash.getContentPane()).getIcon());
                assertEquals(0, splash.getContentPane().getComponentCount());
                Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
                assertEquals(screen.x + (screen.width - 320) / 2, splash.getX());
                assertEquals(screen.y + (screen.height - 180) / 3, splash.getY());
                splash.setVisible(true);
                assertTrue(splash.isShowing());
                splash.setVisible(false);
                assertFalse(splash.isShowing());
                splash.setVisible(true);
                assertTrue(splash.isShowing());
            } finally {
                splash.dispose();
            }
            assertFalse(splash.isDisplayable());
        });
    }

    @Test
    public void stillSupportsShiftKeyListener() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            Splash splash = new Splash(null,
                    new ImageIcon(new BufferedImage(320, 180, BufferedImage.TYPE_INT_ARGB)));
            try {
                KeyStates keys = new KeyStates();
                splash.addKeyListener(keys);
                assertSame(keys, splash.getKeyListeners()[0]);
                keys.keyPressed(new KeyEvent(splash, KeyEvent.KEY_PRESSED, 0,
                        KeyEvent.SHIFT_DOWN_MASK, KeyEvent.VK_SHIFT, KeyEvent.CHAR_UNDEFINED));
                assertTrue(keys.isKeyDown(KeyEvent.VK_SHIFT));
                keys.keyReleased(new KeyEvent(splash, KeyEvent.KEY_RELEASED, 0,
                        0, KeyEvent.VK_SHIFT, KeyEvent.CHAR_UNDEFINED));
                assertFalse(keys.isKeyDown(KeyEvent.VK_SHIFT));
            } finally {
                splash.dispose();
            }
        });
    }
}
