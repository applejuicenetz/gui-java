package de.applejuicenet.client.shared;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import org.junit.Test;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Color;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ThemeColorsTest {

    @Test
    public void pickFollowsTheTheme() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            javax.swing.LookAndFeel previous = UIManager.getLookAndFeel();
            try {
                Color light = Color.WHITE;
                Color dark = Color.BLACK;
                UIManager.setLookAndFeel(new FlatLightLaf());
                assertFalse(ThemeColors.isDarkTheme());
                assertEquals(light, ThemeColors.pick(light, dark));
                UIManager.setLookAndFeel(new FlatDarkLaf());
                assertTrue(ThemeColors.isDarkTheme());
                assertEquals(dark, ThemeColors.pick(light, dark));
                UIManager.setLookAndFeel(previous);
            } catch (Exception failure) {
                throw new AssertionError(failure);
            }
        });
    }
}
