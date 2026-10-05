package de.applejuicenet.client.gui.memorymonitor;

import de.applejuicenet.client.gui.controller.GuiText;

import javax.swing.*;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.geom.Path2D;
import java.util.Locale;

/**
 * Zeigt den Heap-Verbrauch der JavaGUI-JVM (nicht des Cores) als Verlauf.
 * Abtastung und Zeichnen laufen ausschließlich auf dem EDT; der Timer läuft nur,
 * solange die Komponente sichtbar ist.
 */
public class MemoryMonitor
    extends JPanel {

    private static final int SAMPLE_INTERVAL_MS = 1000;
    private static final int HISTORY_SIZE = 120;
    private static final double MB = 1024.0 * 1024.0;

    private final Runtime runtime = Runtime.getRuntime();
    private final long[] used = new long[HISTORY_SIZE];
    private final long[] allocated = new long[HISTORY_SIZE];
    private int count;
    private int head;

    private final Timer timer = new Timer(SAMPLE_INTERVAL_MS, e -> sample());
    private final JLabel usedLabel = valueLabel();
    private final JLabel allocatedLabel = valueLabel();
    private final JLabel maxLabel = valueLabel();
    private final JLabel usedCaption = captionLabel();
    private final JLabel allocatedCaption = captionLabel();
    private final JLabel maxCaption = captionLabel();
    private final Graph graph = new Graph();

    public MemoryMonitor() {
        super(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 14, 8, 14));
        timer.setInitialDelay(0);

        JPanel header = new JPanel(new GridLayout(1, 3, 12, 0));
        header.setOpaque(false);
        header.add(block(usedCaption, usedLabel));
        header.add(block(allocatedCaption, allocatedLabel));
        header.add(block(maxCaption, maxLabel));
        add(header, BorderLayout.NORTH);
        add(graph, BorderLayout.CENTER);

        GuiText.onLanguageChange(this, this::refreshTexts);
        refreshTexts();
        sample();

        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
                if (isShowing()) {
                    timer.start();
                } else {
                    timer.stop();
                }
            }
        });
    }

    public void startMemoryMonitor() {
        if (isShowing()) {
            timer.start();
        }
    }

    public void stopMemoryMonitor() {
        timer.stop();
    }

    private void sample() {
        long total = runtime.totalMemory();
        long inUse = total - runtime.freeMemory();
        int slot = (head + count) % HISTORY_SIZE;
        if (count == HISTORY_SIZE) {
            head = (head + 1) % HISTORY_SIZE;
        } else {
            count++;
        }
        used[slot] = inUse;
        allocated[slot] = total;
        usedLabel.setText(format(inUse));
        allocatedLabel.setText(format(total));
        maxLabel.setText(format(runtime.maxMemory()));
        graph.repaint();
    }

    private void refreshTexts() {
        usedCaption.setText(GuiText.text("javagui.memory.used"));
        allocatedCaption.setText(GuiText.text("javagui.memory.allocated"));
        maxCaption.setText(GuiText.text("javagui.memory.max"));
        usedLabel.setText(format(runtime.totalMemory() - runtime.freeMemory()));
        allocatedLabel.setText(format(runtime.totalMemory()));
        maxLabel.setText(format(runtime.maxMemory()));
        graph.repaint();
    }

    private static String format(long bytes) {
        return String.format(GuiText.locale() == null ? Locale.getDefault() : GuiText.locale(),
                             "%,.1f MB", bytes / MB);
    }

    private static JLabel valueLabel() {
        JLabel label = new JLabel(" ");
        label.setFont(label.getFont().deriveFont(Font.BOLD, label.getFont().getSize2D() + 3f));
        return label;
    }

    private static JLabel captionLabel() {
        JLabel label = new JLabel(" ");
        label.setForeground(UIManager.getColor("Label.disabledForeground"));
        label.setFont(label.getFont().deriveFont(label.getFont().getSize2D() - 1f));
        return label;
    }

    private static JPanel block(JLabel caption, JLabel value) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(caption);
        p.add(Box.createVerticalStrut(2));
        p.add(value);
        return p;
    }

    private final class Graph
        extends JComponent {

        Graph() {
            setPreferredSize(new Dimension(420, 200));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                paintGraph(g2);
            } finally {
                g2.dispose();
            }
        }

        private void paintGraph(Graphics2D g2) {
            Color fg = UIManager.getColor("Label.foreground");
            if (fg == null) {
                fg = Color.GRAY;
            }
            Color accent = UIManager.getColor("Component.accentColor");
            if (accent == null) {
                accent = UIManager.getColor("Button.default.background");
            }
            if (accent == null) {
                accent = new Color(46, 139, 87);
            }
            Color grid = new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 40);
            Color text = new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 150);

            g2.setFont(getFont().deriveFont(getFont().getSize2D() - 1f));
            FontMetrics fm = g2.getFontMetrics();
            long scale = 1;
            for (int i = 0; i < count; i++) {
                scale = Math.max(scale, allocated[(head + i) % HISTORY_SIZE]);
            }
            scale = Math.max(scale, 1);

            int axisW = fm.stringWidth(format(scale)) + 8;
            int x0 = axisW;
            int y0 = 4;
            int w = getWidth() - x0 - 2;
            int h = getHeight() - y0 - fm.getHeight() - 6;
            if (w < 20 || h < 20) {
                return;
            }

            g2.setStroke(new BasicStroke(1f));
            for (int i = 0; i <= 4; i++) {
                int y = y0 + Math.round(h * i / 4f);
                g2.setColor(grid);
                g2.drawLine(x0, y, x0 + w, y);
                g2.setColor(text);
                String label = format(scale - scale * i / 4);
                g2.drawString(label, axisW - 6 - fm.stringWidth(label), y + fm.getAscent() / 2 - 1);
            }
            g2.setColor(text);
            String left = GuiText.text("javagui.memory.timespan");
            g2.drawString(left, x0, y0 + h + fm.getAscent() + 4);
            String right = GuiText.text("javagui.memory.now");
            g2.drawString(right, x0 + w - fm.stringWidth(right), y0 + h + fm.getAscent() + 4);

            if (count < 2) {
                return;
            }
            float step = w / (float) (HISTORY_SIZE - 1);
            float startX = x0 + w - step * (count - 1);

            g2.setColor(new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 90));
            g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f,
                                         new float[] {4f, 4f}, 0f));
            Path2D.Float allocLine = new Path2D.Float();
            Path2D.Float usedLine = new Path2D.Float();
            for (int i = 0; i < count; i++) {
                int idx = (head + i) % HISTORY_SIZE;
                float x = startX + step * i;
                float ya = y0 + h - h * (allocated[idx] / (float) scale);
                float yu = y0 + h - h * (used[idx] / (float) scale);
                if (i == 0) {
                    allocLine.moveTo(x, ya);
                    usedLine.moveTo(x, yu);
                } else {
                    allocLine.lineTo(x, ya);
                    usedLine.lineTo(x, yu);
                }
            }
            g2.draw(allocLine);

            Path2D.Float area = new Path2D.Float(usedLine);
            area.lineTo(startX + step * (count - 1), y0 + h);
            area.lineTo(startX, y0 + h);
            area.closePath();
            g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 60));
            g2.fill(area);
            g2.setColor(accent);
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(usedLine);
        }
    }
}
