package de.applejuicenet.client.gui.plugins.sharetreemap;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Size-weighted binary treemap, drawn without per-file Swing components. */
public class TreemapView extends JPanel {
    private final JButton back = new JButton("Zurück");
    private final JLabel path = new JLabel("Share /");
    private final JComboBox<String> mode = new JComboBox<>(new String[]{"Dateityp", "Upload-Aktivität · Sitzung"});
    private final JLabel details = new JLabel("Share-Daten werden geladen …");
    private final JLabel legend = new JLabel();
    private final Canvas canvas = new Canvas();
    private final ArrayDeque<ShareTree> history = new ArrayDeque<>();
    private ShareTree current;
    private Map<Integer, Long> activity = Map.of();
    private static final Color VIDEO = new Color(53, 110, 156), AUDIO = new Color(73, 122, 96),
            ARCHIVE = new Color(152, 113, 57), OTHER = new Color(115, 123, 133);

    public TreemapView() {
        super(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JPanel top = new JPanel(new BorderLayout(10, 0));
        JPanel navigation = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        navigation.add(back); navigation.add(path);
        top.add(navigation, BorderLayout.CENTER); top.add(mode, BorderLayout.EAST);
        add(top, BorderLayout.NORTH); add(canvas, BorderLayout.CENTER);
        JPanel bottom = new JPanel(new GridLayout(3, 1, 0, 4));
        bottom.add(legend); bottom.add(details);
        bottom.add(new JLabel("Fläche = Dateigröße · Ordner anklicken · Datei: Rechtsklick für Link"));
        add(bottom, BorderLayout.SOUTH);
        back.setEnabled(false); back.addActionListener(e -> back());
        mode.addActionListener(e -> { updateLegend(); canvas.repaint(); });
        updateLegend();
    }

    public void setTree(ShareTree root) {
        String oldPath = current == null ? "" : current.path();
        history.clear(); current = root;
        // Preserve zoom when a fresh snapshot replaces nodes.
        ArrayList<ShareTree> chain = new ArrayList<>();
        if (!oldPath.isEmpty() && find(root, oldPath, chain)) {
            for (int i = 0; i < chain.size() - 1; i++) history.push(chain.get(i));
            current = chain.getLast();
        }
        details.setText(root.bytes() == 0 ? "Keine freigegebenen Dateien mit bekannter Größe." : "Ordner anklicken zum Öffnen.");
        updateNavigation();
    }

    private boolean find(ShareTree node, String target, List<ShareTree> chain) {
        chain.add(node);
        if (node.path().equals(target)) return true;
        for (ShareTree child : node.children()) if (child.directory() && find(child, target, chain)) return true;
        chain.removeLast(); return false;
    }

    public ShareTree current() { return current; }
    public void open(ShareTree node) {
        if (node.directory() && current != null && current.children().contains(node)) {
            history.push(current); current = node; updateNavigation();
        }
    }
    public void back() {
        if (!history.isEmpty()) { current = history.pop(); updateNavigation(); }
    }
    public void setActivity(Map<Integer, Long> bytes) { activity = Map.copyOf(bytes); canvas.repaint(); }
    public void showMessage(String message) { details.setText(message); }
    private void updateNavigation() {
        path.setText(current.path().isEmpty() ? "Share /" : current.path());
        back.setEnabled(!history.isEmpty()); canvas.repaint();
    }
    private void updateLegend() {
        legend.setText(mode.getSelectedIndex() == 0
                ? "Blau: Videos · Grün: Audio · Ocker: Archive · Grau: Sonstige"
                : "Blaugrau: keine Beobachtung · Gelb bis Rot: mehr beobachtete Upload-Bytes · nur bei aktivem Treemap-Tab · diese Verbindung");
    }
    static String format(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exponent = Math.min(6, (int) (Math.log(bytes) / Math.log(1024)));
        return String.format(Locale.GERMAN, "%.1f %s", bytes / Math.pow(1024, exponent),
                new String[]{"B", "KiB", "MiB", "GiB", "TiB", "PiB", "EiB"}[exponent]);
    }
    private Color typeColor(ShareTree node) {
        if (node.directory()) return node.children().isEmpty() ? OTHER : typeColor(node.children().getFirst());
        String name = node.name().toLowerCase(Locale.ROOT);
        if (name.matches(".*\\.(mkv|mp4|avi|mov|webm|mpeg|mpg)$")) return VIDEO;
        if (name.matches(".*\\.(mp3|flac|ogg|wav|m4a|aac|opus)$")) return AUDIO;
        if (name.matches(".*\\.(zip|7z|rar|tar|gz|xz|bz2|iso)$")) return ARCHIVE;
        return OTHER;
    }
    private long uploaded(ShareTree node) {
        if (!node.directory()) return activity.getOrDefault(node.id(), 0L);
        long total = 0;
        for (ShareTree child : node.children()) {
            long count = uploaded(child);
            total = Long.MAX_VALUE - total < count ? Long.MAX_VALUE : total + count;
        }
        return total;
    }
    private Color color(ShareTree node, long maximum) {
        if (mode.getSelectedIndex() == 0) return typeColor(node);
        long bytes = uploaded(node);
        if (bytes <= 0 || maximum <= 0) return new Color(77, 101, 112);
        float fraction = (float) (Math.log1p(bytes) / Math.log1p(maximum));
        return Color.getHSBColor((1 - fraction) * .15f, .68f, .72f);
    }
    private String describe(ShareTree node) {
        return node.path() + " · " + format(node.bytes()) + (node.directory() ? " · " + node.children().size() + " Einträge" : "")
                + (mode.getSelectedIndex() == 1 ? " · beobachteter Upload: " + format(uploaded(node)) : "");
    }

    private class Canvas extends JPanel {
        private final List<Tile> tiles = new ArrayList<>();
        Canvas() {
            setPreferredSize(new Dimension(840, 410));
            setToolTipText("");
            MouseAdapter mouse = new MouseAdapter() {
                @Override public void mouseMoved(MouseEvent e) {
                    ShareTree node = hit(e.getPoint());
                    if (node != null) { details.setText(describe(node)); setToolTipText(describe(node)); }
                    else setToolTipText(null);
                    setCursor(Cursor.getPredefinedCursor(node != null && node.directory() ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
                }
                @Override public void mouseClicked(MouseEvent e) {
                    ShareTree node = hit(e.getPoint());
                    if (node != null && SwingUtilities.isLeftMouseButton(e)) open(node);
                }
                @Override public void mousePressed(MouseEvent e) { popup(e); }
                @Override public void mouseReleased(MouseEvent e) { popup(e); }
            };
            addMouseListener(mouse); addMouseMotionListener(mouse);
        }
        private void popup(MouseEvent e) {
            if (!e.isPopupTrigger()) return;
            ShareTree node = hit(e.getPoint());
            if (node == null || node.directory() || node.link().isEmpty()) return;
            JPopupMenu menu = new JPopupMenu();
            JMenuItem copy = new JMenuItem("ajfsp-Link kopieren");
            copy.addActionListener(event -> {
                try { Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(node.link()), null); }
                catch (IllegalStateException | HeadlessException ex) { showMessage("Zwischenablage nicht verfügbar."); }
            });
            menu.add(copy); menu.show(this, e.getX(), e.getY());
        }
        private ShareTree hit(Point p) {
            for (Tile tile : tiles) if (tile.bounds.contains(p)) return tile.node;
            return null;
        }
        @Override protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics); tiles.clear();
            if (current == null || current.bytes() <= 0) {
                graphics.drawString("Keine Share-Daten verfügbar", 16, 28); return;
            }
            List<ShareTree> nodes = current.children().stream().filter(n -> n.bytes() > 0).toList();
            if (nodes.isEmpty()) return;
            split(nodes, 0, nodes.size(), new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
            long max = nodes.stream().mapToLong(TreemapView.this::uploaded).max().orElse(0);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            for (Tile tile : tiles) {
                Rectangle2D b = tile.bounds;
                g.setColor(color(tile.node, max));
                g.fill(new Rectangle2D.Double(b.getX() + 1, b.getY() + 1, Math.max(0, b.getWidth() - 2), Math.max(0, b.getHeight() - 2)));
                if (b.getWidth() > 85 && b.getHeight() > 48) {
                    Shape clip = g.getClip(); g.clip(b); g.setColor(Color.WHITE);
                    String name = tile.node.name();
                    while (name.length() > 1 && g.getFontMetrics().stringWidth(name + "…") > b.getWidth() - 20) name = name.substring(0, name.length() - 1);
                    if (!name.equals(tile.node.name())) name += "…";
                    g.drawString(name, (int) b.getX() + 10, (int) b.getY() + 22);
                    g.drawString(format(tile.node.bytes()), (int) b.getX() + 10, (int) b.getY() + 40);
                    g.setClip(clip);
                }
            }
            g.dispose();
        }
        private void split(List<ShareTree> nodes, int from, int to, Rectangle2D.Double b) {
            if (to - from == 1) { tiles.add(new Tile(nodes.get(from), b)); return; }
            double total = 0;
            for (int i = from; i < to; i++) total += nodes.get(i).bytes();
            int cut = from + 1; double first = nodes.get(from).bytes();
            while (cut < to - 1 && first < total / 2) first += nodes.get(cut++).bytes();
            double ratio = first / total;
            if (b.width >= b.height) {
                split(nodes, from, cut, new Rectangle2D.Double(b.x, b.y, b.width * ratio, b.height));
                split(nodes, cut, to, new Rectangle2D.Double(b.x + b.width * ratio, b.y, b.width * (1 - ratio), b.height));
            } else {
                split(nodes, from, cut, new Rectangle2D.Double(b.x, b.y, b.width, b.height * ratio));
                split(nodes, cut, to, new Rectangle2D.Double(b.x, b.y + b.height * ratio, b.width, b.height * (1 - ratio)));
            }
        }
    }
    private record Tile(ShareTree node, Rectangle2D.Double bounds) { }
}
