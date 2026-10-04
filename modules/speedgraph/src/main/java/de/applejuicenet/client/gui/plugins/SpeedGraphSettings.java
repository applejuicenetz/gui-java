package de.applejuicenet.client.gui.plugins;

import javax.swing.*;
import java.awt.*;
import java.util.Properties;

public class SpeedGraphSettings extends JPanel {
    SpeedGraphSettings(Properties settings, Runnable save) {
        super(new GridLayout(0, 1, 8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(new JLabel("Zeitraum, Einheiten und Limitlinien direkt im SpeedGraph wählen."));
        add(colorButton("Download-Farbe", "DownloadColor", new Color(40, 145, 210), settings, save));
        add(colorButton("Upload-Farbe", "UploadColor", new Color(220, 130, 40), settings, save));
        JButton reset = new JButton("Standardfarben wiederherstellen");
        reset.addActionListener(e -> {
            settings.remove("UploadColor");
            settings.remove("DownloadColor");
            save.run();
        });
        add(reset);
        add(new JLabel("Hintergrund folgt GUI-Theme; alte Verlaufseinstellungen werden nicht mehr verwendet."));
    }

    private JButton colorButton(String title, String key, Color fallback, Properties settings, Runnable save) {
        JButton button = new JButton(title);
        button.addActionListener(e -> {
            Color selected = JColorChooser.showDialog(this, title, color(settings, key, fallback));
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
