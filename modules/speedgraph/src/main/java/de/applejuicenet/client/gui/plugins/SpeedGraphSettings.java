package de.applejuicenet.client.gui.plugins;

import de.applejuicenet.client.gui.controller.GuiText;
import javax.swing.*;
import java.awt.*;
import java.util.Properties;

public class SpeedGraphSettings extends JPanel {
    SpeedGraphSettings(Properties settings, Runnable save) {
        super(new GridLayout(0, 1, 8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(GuiText.label("plugins.speed.settings"));
        add(colorButton(GuiText.text("plugins.speed.downloadcolor"), "DownloadColor", new Color(40, 145, 210), settings, save));
        add(colorButton(GuiText.text("plugins.speed.uploadcolor"), "UploadColor", new Color(220, 130, 40), settings, save));
        JButton reset = GuiText.button("plugins.speed.resetcolors");
        reset.addActionListener(e -> {
            settings.remove("UploadColor");
            settings.remove("DownloadColor");
            save.run();
        });
        add(reset);
        add(GuiText.label("plugins.speed.theme"));
    }

    private JButton colorButton(String title, String key, Color fallback, Properties settings, Runnable save) {
        JButton button = new JButton(title);
        GuiText.setText(button, key.equals("DownloadColor") ? "plugins.speed.downloadcolor" : "plugins.speed.uploadcolor");
        button.addActionListener(e -> {
            Color selected = JColorChooser.showDialog(this, button.getText(), color(settings, key, fallback));
            if (selected != null) {
                settings.setProperty(key, "0x" + Integer.toHexString(selected.getRGB()));
                save.run();
            }
        });
        return button;
    }

    static Color color(Properties settings, String key, Color fallback) {
        try { return new Color((int) Long.decode(settings.getProperty(key)).longValue()); }
        catch (NumberFormatException | NullPointerException ignored) { return fallback; }
    }
}
