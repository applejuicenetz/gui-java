package de.applejuicenet.client.gui.plugins;

import de.applejuicenet.client.gui.controller.GuiText;
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
    private final JLabel status = GuiText.label("plugins.speed.waiting");
    private final JComboBox<String> period = new JComboBox<>(new String[]{"1 min", "5 min", "15 min", "1 h"});
    private final JComboBox<SpeedFormat> units = new JComboBox<>(SpeedFormat.values());
    private final JCheckBox limits = GuiText.checkBox("plugins.speed.limits");
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
        summary.add(card(GuiText.text("plugins.speed.download"), download, downStats));
        summary.add(card(GuiText.text("plugins.speed.upload"), upload, upStats));
        top.add(summary, BorderLayout.NORTH);
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JLabel periodLabel = GuiText.label("plugins.speed.period");
        periodLabel.setLabelFor(period);
        JLabel unitLabel = GuiText.label("plugins.speed.units");
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
        GuiText.onLanguageChange(this, () -> refresh(System.currentTimeMillis()));
    }

    private JPanel card(String title, JLabel value, JLabel stats) {
        JPanel panel = new JPanel(new GridLayout(3, 1, 0, 4));
        JLabel heading = new JLabel(title);
        GuiText.setText(heading, title.contains("Download") ? "plugins.speed.download" : "plugins.speed.upload");
        panel.add(heading);
        value.setFont(value.getFont().deriveFont(Font.BOLD, 26f));
        panel.add(value);
        GuiText.tooltip(stats, "plugins.speed.statshint");
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
        download.setText(fresh ? format.format(samples.getLast().download(), GuiText.locale()) : "—");
        upload.setText(fresh ? format.format(samples.getLast().upload(), GuiText.locale()) : "—");
        downStats.setText(stats(samples, false, format));
        upStats.setText(stats(samples, true, format));
        GuiText.clearTextBinding(status);
        status.setText(disconnected ? GuiText.text("plugins.speed.disconnected") : fresh ? GuiText.text("plugins.speed.current") : GuiText.text("plugins.speed.stale"));
        Color upColor = SpeedGraphSettings.color(settings, "UploadColor", new Color(220, 130, 40));
        Color downColor = SpeedGraphSettings.color(settings, "DownloadColor", new Color(40, 145, 210));
        upload.setForeground(upColor);
        download.setForeground(downColor);
        chart.setColors(upColor, downColor);
        chart.setLimits(uploadLimit, downloadLimit, limits.isSelected());
        chart.setData(samples, now, window, format);
    }

    private String stats(List<SpeedHistory.Sample> samples, boolean up, SpeedFormat format) {
        if (samples.isEmpty()) return GuiText.text("plugins.speed.stats", "—", "—");
        double average = samples.stream().mapToDouble(s -> up ? s.upload() : s.download()).average().orElse(0);
        long max = samples.stream().mapToLong(s -> up ? s.upload() : s.download()).max().orElse(0);
        return GuiText.text("plugins.speed.stats", format.format(average, GuiText.locale()), format.format(max, GuiText.locale()));
    }

    String downloadText() { return download.getText(); }
}
