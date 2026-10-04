package de.applejuicenet.client.shared;

import org.junit.Test;

import javax.swing.JButton;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import static org.junit.Assert.assertEquals;

public class LookAndFeelLoaderTest {
    @Test
    public void keepsLoadableSelectedTheme() throws Exception {
        check(new LookAFeel("Darcula", "com.formdev.flatlaf.FlatDarculaLaf"), null,
                "com.formdev.flatlaf.FlatDarculaLaf");
    }

    @Test
    public void missingSelectedThemeFallsBackToDefault() throws Exception {
        check(new LookAFeel("Missing skin", "missing.skin.LookAndFeel"), null,
                LookAndFeelLoader.DEFAULT.getClassName());
    }

    @Test
    public void missingSelectionUsesDefault() throws Exception {
        check(null, null, LookAndFeelLoader.DEFAULT.getClassName());
    }

    @Test
    public void unsupportedThemeFallsBackToDefault() throws Exception {
        check(new LookAFeel("Unsupported", UnsupportedTheme.class.getName()), null,
                LookAndFeelLoader.DEFAULT.getClassName());
    }

    @Test
    public void linkageFailureFallsBackToDefault() throws Exception {
        check(new LookAFeel("Broken dependency", LinkageFailureTheme.class.getName()), null,
                LookAndFeelLoader.DEFAULT.getClassName());
    }

    @Test
    public void initializationFailureFallsBackToDefault() throws Exception {
        check(new LookAFeel("Broken initialization", InitializationFailureTheme.class.getName()), null,
                LookAndFeelLoader.DEFAULT.getClassName());
    }

    @Test
    public void brokenDefaultFallsBackToBuiltInMetal() throws Exception {
        check(new LookAFeel("Missing skin", "missing.skin.LookAndFeel"),
                new LookAFeel("Missing default", "missing.default.LookAndFeel"),
                MetalLookAndFeel.class.getName());
    }

    private static void check(LookAFeel preferred, LookAFeel fallback, String expectedClass) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            LookAndFeel previous = UIManager.getLookAndFeel();
            try {
                LookAFeel active = fallback == null ? LookAndFeelLoader.install(preferred)
                        : LookAndFeelLoader.install(preferred, fallback);
                assertEquals(expectedClass, active.getClassName());
                assertEquals(expectedClass, UIManager.getLookAndFeel().getClass().getName());
                JButton button = new JButton("Start");
                button.setSize(160, 40);
                BufferedImage image = new BufferedImage(160, 40, BufferedImage.TYPE_INT_ARGB);
                Graphics2D graphics = image.createGraphics();
                try {
                    button.paint(graphics);
                } finally {
                    graphics.dispose();
                }
            } finally {
                try {
                    UIManager.setLookAndFeel(previous);
                } catch (Exception exception) {
                    throw new AssertionError(exception);
                }
            }
        });
    }

    public static class UnsupportedTheme extends MetalLookAndFeel {
        @Override
        public boolean isSupportedLookAndFeel() {
            return false;
        }
    }

    public static class LinkageFailureTheme extends MetalLookAndFeel {
        @Override
        public void initialize() {
            throw new NoClassDefFoundError("missing theme dependency");
        }
    }

    public static class InitializationFailureTheme extends MetalLookAndFeel {
        @Override
        public void initialize() {
            throw new IllegalStateException("broken theme initialization");
        }
    }
}
