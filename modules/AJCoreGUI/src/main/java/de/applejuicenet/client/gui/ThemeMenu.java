package de.applejuicenet.client.gui;

import de.applejuicenet.client.shared.LookAFeel;
import javax.swing.*;
import java.util.function.Consumer;
import java.util.function.Function;

final class ThemeMenu {
    private ThemeMenu() {
    }

    static void populate(JMenu menu, LookAFeel[] themes, String selected,
                         Function<String, String> text, Consumer<String> activate) {
        menu.removeAll();
        ButtonGroup selection = new ButtonGroup();
        java.util.Set<String> seen = new java.util.HashSet<>();
        String[] groups = {"system", "flatlaf", "colors"};

        for (String group : groups) {
            boolean headerAdded = false;
            for (LookAFeel theme : themes) {
                String className = theme.getClassName();
                String family = className.startsWith("com.formdev.flatlaf.intellijthemes.") ? "colors"
                        : className.startsWith("com.formdev.flatlaf.") ? "flatlaf" : "system";
                if (!group.equals(family) || !seen.add(className)) continue;
                if (!headerAdded) {
                    if (menu.getMenuComponentCount() > 0) menu.addSeparator();
                    JMenuItem header = new JMenuItem(text.apply("javagui.themes.group." + group));
                    header.setEnabled(false);
                    header.setFont(header.getFont().deriveFont(java.awt.Font.BOLD));
                    menu.add(header);
                    headerAdded = true;
                }
                JCheckBoxMenuItem item = new JCheckBoxMenuItem(theme.getName());
                selection.add(item);
                item.setSelected(theme.getName().equals(selected));
                item.addActionListener(event -> activate.accept(theme.getName()));
                menu.add(item);
            }
        }
    }
}
