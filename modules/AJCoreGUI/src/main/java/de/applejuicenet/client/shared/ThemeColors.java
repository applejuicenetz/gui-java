package de.applejuicenet.client.shared;

import javax.swing.UIManager;
import java.awt.Color;

/** Helpers for fixed accent colors that must stay readable on light and dark themes. */
public final class ThemeColors {
    private static final int DARK_THRESHOLD = 384;

    private ThemeColors() {
    }

    /** True when the color is dark; a missing color counts as light. */
    public static boolean isDark(Color background) {
        return background != null && background.getRed() + background.getGreen() + background.getBlue() < DARK_THRESHOLD;
    }

    /** True when the current look and feel has a dark text background. */
    public static boolean isDarkTheme() {
        return isDark(UIManager.getColor("TextField.background"));
    }

    /** Picks the variant matching the current theme. */
    public static Color pick(Color light, Color dark) {
        return isDarkTheme() ? dark : light;
    }
}
