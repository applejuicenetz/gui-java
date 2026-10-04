package de.applejuicenet.client.gui.plugins;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/** EDT-owned controls and summary above a shared time axis. */
public class GraphPanel extends JPanel {
    private static final int[] WINDOWS = {60000, 300000, 900000, 3600000};
    private final SpeedHistory history;
    private final Properties settings;
    private final UpDownChart chart = new UpDownChart();
    private final JLabel download = new JLabel("—");
    private final JLabel upload = new JLabel("—");
    private final JLabel downStats = new JLabel(" ");
    private final JLabel upStats = new JLabel(" ");
    private final JLabel status = new JLabel("Warte auf Messdaten");
    private final JComboBox<String> period = new JComboBox<>(new String[]{"1 min", "5 min", "15 min", "1 h"});
    private final JComboBox<SpeedFormat> units = new JComboBox<>(SpeedFormat.values());
    private final JCheckBox limits = new JCheckBox("Limitlinien");
    private boolean disconnected;
    private long uploadLimit;
    private long downloadLimit;

    GraphPanel(SpeedHistory history, Properties settings, Runnable save) {
        super(new BorderLayout(0, 8));
        this.history = history;
        this.settings = settings;
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JPanel top = new JPanel(new BorderLayout(0, 12));
        JPanel summary = new JPanel(new GridLayout(1, 2, 24, 0));
        summary.add(card("Download — durchgezogen", download, downStats));
        summary.add(card("Upload — gestrichelt", upload, upStats));
        top.add(summary, BorderLayout.NORTH);
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JLabel periodLabel = new JLabel("Zeitraum:");
        periodLabel.setLabelFor(period);
        JLabel unitLabel = new JLabel("Einheiten:");
        unitLabel.setLabelFor(units);
        controls.add(periodLabel);
        controls.add(period);
        controls.add(unitLabel);
        controls.add(units);
        controls.add(limits);
        period.setSelectedIndex(windowIndex(settings.getProperty("Window", "300000")));
        try { units.setSelectedItem(SpeedFormat.valueOf(settings.getProperty("Units", "BINARY"))); }
        catch (IllegalArgumentException ignored) { units.setSelectedItem(SpeedFormat.BINARY); }
        limits.setSelected(Boolean.parseBoolean(settings.getProperty("ShowLimits", "false")));
        top.add(controls, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);
        add(chart, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);
        Runnable changed = () -> {
            settings.setProperty("Window", Integer.toString(WINDOWS[period.getSelectedIndex()]));
            settings.setProperty("Units", ((SpeedFormat) units.getSelectedItem()).name());
            settings.setProperty("ShowLimits", Boolean.toString(limits.isSelected()));
            save.run();
            refresh(System.currentTimeMillis());
        };
        period.addActionListener(e -> changed.run());
        units.addActionListener(e -> changed.run());
        limits.addActionListener(e -> changed.run());
    }

    private JPanel card(String title, JLabel value, JLabel stats) {
        JPanel panel = new JPanel(new GridLayout(3, 1, 0, 4));
        panel.add(new JLabel(title));
        value.setFont(value.getFont().deriveFont(Font.BOLD, 26f));
        panel.add(value);
        stats.setToolTipText("Arithmetischer Mittelwert und Spitze der empfangenen Messwerte im gewählten Zeitraum");
        panel.add(stats);
        return panel;
    }

    private static int windowIndex(String value) {
        for (int i = 0; i < WINDOWS.length; i++) if (Integer.toString(WINDOWS[i]).equals(value)) return i;
        return 1;
    }

    void setDisconnected(boolean value) { disconnected = value; }

    void setLimits(long upload, long download) {
        uploadLimit = upload;
        downloadLimit = download;
    }

    void refresh(long now) {
        long window = WINDOWS[period.getSelectedIndex()];
        List<SpeedHistory.Sample> samples = history.snapshot(now, window);
        SpeedFormat format = (SpeedFormat) units.getSelectedItem();
        boolean fresh = !disconnected && !samples.isEmpty() && now - samples.getLast().time() <= UpDownChart.STALE_AFTER;
        download.setText(fresh ? format.format(samples.getLast().download(), Locale.GERMANY) : "—");
        upload.setText(fresh ? format.format(samples.getLast().upload(), Locale.GERMANY) : "—");
        downStats.setText(stats(samples, false, format));
        upStats.setText(stats(samples, true, format));
        status.setText(disconnected ? "Keine Verbindung zum Core" : fresh ? "Aktuelle Messwerte · Verlauf seit Öffnen der GUI" : "Keine aktuellen Messdaten");
        Color upColor = SpeedGraphSettings.color(settings, "UploadColor", new Color(220, 130, 40));
        Color downColor = SpeedGraphSettings.color(settings, "DownloadColor", new Color(40, 145, 210));
        upload.setForeground(upColor);
        download.setForeground(downColor);
        chart.setColors(upColor, downColor);
        chart.setLimits(uploadLimit, downloadLimit, limits.isSelected());
        chart.setData(samples, now, window, format);
    }

    private String stats(List<SpeedHistory.Sample> samples, boolean up, SpeedFormat format) {
        if (samples.isEmpty()) return "Messwert-Ø: — · Spitze: —";
        double average = samples.stream().mapToDouble(s -> up ? s.upload() : s.download()).average().orElse(0);
        long max = samples.stream().mapToLong(s -> up ? s.upload() : s.download()).max().orElse(0);
        return "Messwert-Ø: " + format.format(average, Locale.GERMANY) + " · Spitze: " + format.format(max, Locale.GERMANY);
    }

    String downloadText() { return download.getText(); }
}
