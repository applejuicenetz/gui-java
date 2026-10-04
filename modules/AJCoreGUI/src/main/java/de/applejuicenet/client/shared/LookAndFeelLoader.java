package de.applejuicenet.client.shared;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.MetalLookAndFeel;

public final class LookAndFeelLoader {
    public static final LookAFeel DEFAULT =
            new LookAFeel("JGoodies Plastic", "com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
    private static final Logger logger = LoggerFactory.getLogger(LookAndFeelLoader.class);

    private LookAndFeelLoader() {
    }

    public static LookAFeel install(LookAFeel preferred) {
        return install(preferred, DEFAULT);
    }

    static LookAFeel install(LookAFeel preferred, LookAFeel fallback) {
        LookAFeel requested = preferred == null ? fallback : preferred;
        if (tryInstall(requested)) {
            return requested;
        }
        if (!requested.getClassName().equals(fallback.getClassName()) && tryInstall(fallback)) {
            return fallback;
        }
        MetalLookAndFeel builtin = new MetalLookAndFeel();
        try {
            UIManager.setLookAndFeel(builtin);
        } catch (UnsupportedLookAndFeelException exception) {
            throw new IllegalStateException("Built-in Metal look and feel is unavailable", exception);
        }
        return new LookAFeel(builtin.getName(), builtin.getClass().getName());
    }

    private static boolean tryInstall(LookAFeel lookAndFeel) {
        try {
            UIManager.setLookAndFeel(lookAndFeel.getClassName());
            return true;
        } catch (Exception | LinkageError exception) {
            logger.warn("Cannot load theme {}; using a fallback", lookAndFeel.getName(), exception);
            return false;
        }
    }
}
