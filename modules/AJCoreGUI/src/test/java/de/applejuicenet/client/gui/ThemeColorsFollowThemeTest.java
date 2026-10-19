package de.applejuicenet.client.gui;

import com.formdev.flatlaf.FlatDarkLaf;
import de.applejuicenet.client.gui.components.table.SortButtonRenderer;
import org.junit.Test;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.Color;
import java.awt.Component;
import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ThemeColorsFollowThemeTest {

    @Test
    public void statusBarSeparatorFollowsTheTheme() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            LookAndFeel previous = UIManager.getLookAndFeel();
            try {
                UIManager.setLookAndFeel(new MetalLookAndFeel());
                StatusLabel label = new StatusLabel(5);
                Color metalSeparator = UIManager.getColor("Separator.foreground");
                assertEquals(metalSeparator, separatorColor(label));

                UIManager.setLookAndFeel(new FlatDarkLaf());
                label.updateUI();

                Color flatSeparator = UIManager.getColor("Separator.foreground");
                assertNotEquals(metalSeparator, flatSeparator);
                assertEquals(flatSeparator, separatorColor(label));
            } catch (Exception failure) {
                throw new AssertionError(failure);
            } finally {
                try {
                    UIManager.setLookAndFeel(previous);
                } catch (Exception failure) {
                    throw new AssertionError(failure);
                }
            }
        });
    }

    @Test
    public void sortArrowsFollowTheTheme() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            LookAndFeel previous = UIManager.getLookAndFeel();
            try {
                UIManager.setLookAndFeel(new MetalLookAndFeel());
                JTable table = new JTable(2, 2);
                SortButtonRenderer renderer = new SortButtonRenderer();
                renderer.setSelectedColumn(0, true);
                assertEquals(UIManager.getColor("control"), arrowFill(renderer, table));

                UIManager.setLookAndFeel(new FlatDarkLaf());
                assertEquals(UIManager.getColor("control"), arrowFill(renderer, table));
            } catch (Exception failure) {
                throw new AssertionError(failure);
            } finally {
                try {
                    UIManager.setLookAndFeel(previous);
                } catch (Exception failure) {
                    throw new AssertionError(failure);
                }
            }
        });
    }

    private static Color separatorColor(StatusLabel label) {
        return ((MatteBorder) ((javax.swing.border.CompoundBorder) label.getBorder()).getOutsideBorder()).getMatteColor();
    }

    private static Object arrowFill(SortButtonRenderer renderer, JTable table) throws Exception {
        Component component = renderer.getTableCellRendererComponent(table, "H", false, false, -1, 0);
        Icon icon = ((AbstractButton) component).getIcon();
        Field fill = icon.getClass().getDeclaredField("fill");
        fill.setAccessible(true);
        return fill.get(icon);
    }
}
