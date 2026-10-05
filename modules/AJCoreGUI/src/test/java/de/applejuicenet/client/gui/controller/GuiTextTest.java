package de.applejuicenet.client.gui.controller;

import org.junit.Test;
import javax.swing.*;
import java.nio.file.Paths;
import static org.junit.Assert.*;

public class GuiTextTest {
    @Test
    public void updatesOpenControlsWithoutLosingComboSelection() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            language("deutsch");
            JLabel label = GuiText.label("plugins.logviewer.empty");
            JButton button = GuiText.button("plugins.treemap.back");
            JComboBox<String> combo = GuiText.combo("plugins.logviewer.all", "plugins.logviewer.errorsonly");
            combo.setSelectedIndex(1);
            language("english");
            assertEquals("No log file selected", label.getText());
            assertEquals("Back", button.getText());
            assertEquals(1, combo.getSelectedIndex());
            assertEquals("Errors only", combo.getSelectedItem());
            language("deutsch");
            assertEquals("Zurück", button.getText());
        });
    }

    private static void language(String name) {
        LanguageSelector.getInstance(Paths.get("../../resources/language/" + name + ".properties")
                .toAbsolutePath().normalize().toString());
    }
}
