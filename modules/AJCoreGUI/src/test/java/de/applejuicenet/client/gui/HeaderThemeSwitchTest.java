package de.applejuicenet.client.gui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import de.applejuicenet.client.gui.components.table.NormalHeaderRenderer;
import de.applejuicenet.client.gui.components.table.SortButtonRenderer;
import org.junit.Test;
import javax.swing.*;
import java.awt.*;
import static org.junit.Assert.*;

public class HeaderThemeSwitchTest {
    @Test
    public void existingHeaderButtonsFollowThemeSwitch() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            LookAndFeel previous = UIManager.getLookAndFeel();
            try {
                UIManager.setLookAndFeel(new FlatLightLaf());
                JTable table = new JTable(1, 3);
                SortButtonRenderer sorted = new SortButtonRenderer();
                NormalHeaderRenderer normal = new NormalHeaderRenderer();
                for (LookAndFeel theme : new LookAndFeel[]{new FlatDarkLaf(), new FlatLightLaf()}) {
                    UIManager.setLookAndFeel(theme);
                    SwingUtilities.updateComponentTreeUI(table);
                    JButton reference = new JButton();
                    for (boolean ascending : new boolean[]{true, false}) {
                        sorted.setSelectedColumn(0, ascending);
                        for (int column = 0; column < 2; column++) {
                            Component header = sorted.getTableCellRendererComponent(table, "File", false, false, -1, column);
                            assertEquals("Sort header background", reference.getBackground(), header.getBackground());
                            assertEquals("Sort header foreground", reference.getForeground(), header.getForeground());
                            assertEquals(table.getTableHeader().getFont(), header.getFont());
                        }
                    }
                    Component header = normal.getTableCellRendererComponent(table, "File", false, false, -1, 0);
                    assertEquals(reference.getBackground(), header.getBackground());
                    assertEquals(reference.getForeground(), header.getForeground());
                    assertEquals(table.getTableHeader().getFont(), header.getFont());
                }
            } catch (Exception failure) {
                throw new AssertionError(failure);
            } finally {
                try { UIManager.setLookAndFeel(previous); }
                catch (UnsupportedLookAndFeelException failure) { throw new AssertionError(failure); }
            }
        });
    }
}
