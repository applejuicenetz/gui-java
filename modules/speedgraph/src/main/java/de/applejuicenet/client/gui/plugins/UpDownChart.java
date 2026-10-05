package de.applejuicenet.client.gui.plugins;

import de.applejuicenet.client.gui.controller.GuiText;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/** Paints immutable snapshots; never edits history or persists settings. */
public class UpDownChart extends JPanel {
    static final long STALE_AFTER = 15000;
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault());
    private List<SpeedHistory.Sample> samples = List.of();
    private long now;
    private long window = 300000;
    private SpeedFormat format = SpeedFormat.BINARY;
    private double ceiling = 1;
    private long smallerSince;
    private Color upload = new Color(220, 130, 40);
    private Color download = new Color(40, 145, 210);
    private long uploadLimit;
    private long downloadLimit;
    private boolean showLimits;
    private int left = 100;

    public UpDownChart() {
        setPreferredSize(new Dimension(700, 320));
        setToolTipText("");
    }

    void setColors(Color upload, Color download) {
        this.upload = upload;
        this.download = download;
    }

    void setLimits(long upload, long download, boolean visible) {
        uploadLimit = Math.max(0, upload);
        downloadLimit = Math.max(0, download);
        showLimits = visible;
    }

    void setData(List<SpeedHistory.Sample> samples, long now, long window, SpeedFormat format) {
        this.samples = List.copyOf(samples);
        this.now = now;
        this.window = window;
        this.format = format;
        double max = samples.stream().mapToDouble(s -> Math.max(s.upload(), s.download())).max().orElse(1);
        if (showLimits) max = Math.max(max, Math.max(uploadLimit, downloadLimit));
        double step = niceStep(Math.max(1, max) / 5);
        double target = Math.ceil(Math.max(1, max) / step) * step;
        if (target >= ceiling) {
            ceiling = Math.max(1, target);
            smallerSince = 0;
        } else if (target < ceiling / 2) {
            if (smallerSince == 0) smallerSince = now;
            if (now - smallerSince >= 10000) { ceiling = Math.max(1, target); smallerSince = 0; }
        } else smallerSince = 0;
        repaint();
    }

    private static double niceStep(double value) {
        double power = Math.pow(10, Math.floor(Math.log10(value)));
        double fraction = value / power;
        return (fraction <= 1 ? 1 : fraction <= 2 ? 2 : fraction <= 5 ? 5 : 10) * power;
    }

    @Override protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int unit = format.unit(ceiling);
            left = Math.max(90, g.getFontMetrics().stringWidth(format.format(ceiling, unit, GuiText.locale())) + 18);
            int width = getWidth() - left - 20;
            int height = getHeight() - 65;
            if (width <= 0 || height <= 0) return;
            Color foreground = getForeground();
            g.setColor(new Color(foreground.getRed(), foreground.getGreen(), foreground.getBlue(), 35));
            for (int i = 0; i <= 5; i++) {
                int y = 25 + height - height * i / 5;
                g.drawLine(left, y, left + width, y);
            }
            g.setColor(foreground);
            for (int i = 0; i <= 5; i++) {
                String text = format.format(ceiling * i / 5, unit, GuiText.locale());
                g.drawString(text, left - g.getFontMetrics().stringWidth(text) - 10, 30 + height - height * i / 5);
            }
            for (int i = 0; i <= 4; i++) {
                String text = TIME.format(Instant.ofEpochMilli(now - window + window * i / 4));
                int x = left + width * i / 4;
                g.drawString(text, Math.min(getWidth() - g.getFontMetrics().stringWidth(text), Math.max(0, x - g.getFontMetrics().stringWidth(text) / 2)), 48 + height);
            }
            g.clipRect(left, 24, width + 1, height + 2);
            drawSeries(g, true, width, height);
            drawSeries(g, false, width, height);
            if (showLimits) {
                drawLimit(g, uploadLimit, upload, width, height);
                drawLimit(g, downloadLimit, download, width, height);
            }
        } finally { g.dispose(); }
    }

    private void drawSeries(Graphics2D g, boolean up, int width, int height) {
        g.setColor(up ? upload : download);
        g.setStroke(up ? new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, new float[]{6, 4}, 0) : new BasicStroke(2));
        Path2D path = new Path2D.Double();
        long previous = Long.MIN_VALUE;
        for (var sample : samples) {
            double x = left + (sample.time() - (now - window)) * (double) width / window;
            double y = 25 + height - (up ? sample.upload() : sample.download()) * (double) height / ceiling;
            if (previous == Long.MIN_VALUE || sample.startsSegment() || sample.time() - previous > STALE_AFTER) path.moveTo(x, y);
            else path.lineTo(x, y);
            g.fillOval((int) x - 2, (int) y - 2, 4, 4);
            previous = sample.time();
        }
        g.draw(path);
    }

    private void drawLimit(Graphics2D g, long limit, Color color, int width, int height) {
        if (limit <= 0) return;
        int y = 25 + height - (int) (limit * (double) height / ceiling);
        g.setColor(color);
        g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[]{2, 6}, 0));
        g.drawLine(left, y, left + width, y);
    }

    @Override public String getToolTipText(MouseEvent event) {
        int width = getWidth() - left - 20;
        if (samples.isEmpty() || width <= 0 || event.getX() < left || event.getX() > left + width) return null;
        long time = now - window + (long) ((event.getX() - left) * (double) window / width);
        var closest = samples.stream().min(java.util.Comparator.comparingLong(s -> Math.abs(s.time() - time))).orElseThrow();
        if (Math.abs(closest.time() - time) > STALE_AFTER) return GuiText.text("plugins.speed.nodata");
        return TIME.format(Instant.ofEpochMilli(closest.time())) + " · Download: " + format.format(closest.download(), GuiText.locale())
                + " · Upload: " + format.format(closest.upload(), GuiText.locale());
    }
}
