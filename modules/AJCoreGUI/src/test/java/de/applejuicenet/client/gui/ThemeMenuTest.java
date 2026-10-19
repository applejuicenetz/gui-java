package de.applejuicenet.client.gui;

import de.applejuicenet.client.shared.LookAFeel;
import org.junit.Test;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

public class ThemeMenuTest {
    @Test
    public void groupsThemesDeduplicatesClassesAndKeepsOneSelection() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JMenu menu = new JMenu();
            List<String> activated = new ArrayList<>();
            LookAFeel darcula = new LookAFeel("Darcula", "com.formdev.flatlaf.FlatDarculaLaf");
            ThemeMenu.populate(menu, new LookAFeel[]{
                    new LookAFeel("Metal", "javax.swing.plaf.metal.MetalLookAndFeel"),
                    new LookAFeel("Nord", "com.formdev.flatlaf.intellijthemes.FlatNordIJTheme"),
                    darcula, darcula,
                    new LookAFeel("Plastic", "com.jgoodies.looks.plastic.Plastic3DLookAndFeel")
            }, "Darcula", key -> key, activated::add);
            int headers = 0;
            List<JCheckBoxMenuItem> choices = new ArrayList<>();
            for (java.awt.Component component : menu.getMenuComponents()) {
                if (component instanceof JCheckBoxMenuItem choice) choices.add(choice);
                else if (component instanceof JMenuItem header) {
                    headers++;
                    assertFalse(header.isEnabled());
                }
            }
            assertEquals(3, headers);
            assertEquals("javagui.themes.group.system", menu.getItem(0).getText());
            assertEquals(List.of("Metal", "Plastic", "Darcula", "Nord"),
                    choices.stream().map(JMenuItem::getText).toList());
            assertTrue(choices.get(2).isSelected());
            assertTrue(activated.isEmpty());
            choices.get(3).doClick();
            assertEquals(List.of("Nord"), activated);
            assertFalse(choices.get(2).isSelected());
            assertTrue(choices.get(3).isSelected());
        });
    }
}
