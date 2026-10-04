package de.applejuicenet.client.gui.plugins.versionchecker.panels;

import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.fassade.entity.Upload;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/** Session observations with theme-native controls and explicit counting semantics. */
public class MainPanel extends JPanel {
    private final VersionStats stats;
    private final VersionTableModel model = new VersionTableModel();
    private final JTable table = new JTable(model);
    private final JTextField filter = new JTextField(20);
    private final JLabel contacts = new JLabel("0");
    private final JLabel versions = new JLabel("0");
    private final JLabel leading = new JLabel("—");
    private final JPanel systems = new JPanel(new GridLayout(0, 1, 0, 10));
    private final JLabel empty = new JLabel("Noch keine Transferkontakte beobachtet", SwingConstants.CENTER);
    private final CardLayout contentLayout = new CardLayout();
    private final JPanel content = new JPanel(contentLayout);

    public MainPanel() { this(new VersionStats()); }
    public MainPanel(VersionStats stats) {
        super(new BorderLayout(0, 16));
        this.stats = stats;
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        JPanel header = new JPanel(new BorderLayout(0, 16));
        JPanel title = new JPanel(new GridLayout(0, 1, 0, 4));
        JLabel heading = new JLabel("Versionen im Transfernetz");
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 22f));
        title.add(heading);
        title.add(new JLabel("Core-Versionen und Systeme beobachteter Download-Quellen und Uploads"));
        header.add(title, BorderLayout.NORTH);
        JPanel cards = new JPanel(new GridLayout(1, 3, 16, 0));
        cards.add(card("Beobachtete Kontakte", contacts));
        cards.add(card("Core-Versionen", versions));
        cards.add(card("Häufigste Version", leading));
        header.add(cards, BorderLayout.CENTER);
        JPanel tools = new JPanel(new BorderLayout(12, 0));
        JPanel search = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JLabel label = new JLabel("Version filtern:");
        label.setLabelFor(filter);
        search.add(label);
        search.add(filter);
        tools.add(search, BorderLayout.CENTER);
        JButton reset = new JButton("Statistik zurücksetzen");
        reset.addActionListener(e -> reset());
        tools.add(reset, BorderLayout.EAST);
        header.add(tools, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);
        table.setFillsViewportHeight(true);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setRowHeight(table.getFontMetrics(table.getFont()).getHeight() + 16);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        TableRowSorter<VersionTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        filter.getDocument().addDocumentListener(new DocumentListener() {
            private void changed() {
                sorter.setRowFilter(filter.getText().isBlank() ? null : RowFilter.regexFilter("(?i)" + Pattern.quote(filter.getText()), 0));
            }
            public void insertUpdate(DocumentEvent e) { changed(); }
            public void removeUpdate(DocumentEvent e) { changed(); }
            public void changedUpdate(DocumentEvent e) { changed(); }
        });
        DefaultTableCellRenderer text = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                                                                     boolean focus, int row, int column) {
                super.getTableCellRendererComponent(table, value, selected, focus, row, column);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return this;
            }
        };
        table.setDefaultRenderer(String.class, text);
        table.getColumnModel().getColumn(0).setPreferredWidth(170);
        table.getColumnModel().getColumn(1).setMaxWidth(100);
        table.getColumnModel().getColumn(2).setMaxWidth(130);
        table.getColumnModel().getColumn(3).setPreferredWidth(400);
        DefaultTableCellRenderer number = new DefaultTableCellRenderer();
        number.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(1).setCellRenderer(number);
        table.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override protected void setValue(Object value) {
                setHorizontalAlignment(SwingConstants.RIGHT);
                setText(String.format(Locale.GERMANY, "%.1f %%", value));
            }
        });
        content.add(new JScrollPane(table), "table");
        content.add(empty, "empty");
        JPanel body = new JPanel(new BorderLayout(18, 0));
        body.add(content, BorderLayout.CENTER);
        JPanel sidebar = new JPanel(new BorderLayout(0, 14));
        JLabel osTitle = new JLabel("Betriebssysteme");
        osTitle.setFont(osTitle.getFont().deriveFont(Font.BOLD, 16f));
        sidebar.add(osTitle, BorderLayout.NORTH);
        JPanel systemList = new JPanel(new BorderLayout());
        systemList.add(systems, BorderLayout.NORTH);
        sidebar.add(new JScrollPane(systemList), BorderLayout.CENTER);
        sidebar.setPreferredSize(new Dimension(235, 200));
        body.add(sidebar, BorderLayout.EAST);
        add(body, BorderLayout.CENTER);
        JLabel footer = new JLabel("Seit GUI-Start / Reset · maximal 50.000 Kontakt-IDs · keine eindeutige Nutzerzählung");
        footer.setToolTipText("Upload- und Quellen-IDs werden getrennt gezählt. Detaildaten werden nur bei aktiven Transferansichten geliefert.");
        add(footer, BorderLayout.SOUTH);
        refresh();
    }

    private JPanel card(String title, JLabel value) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(UIManager.getColor("Separator.foreground") == null ? Color.GRAY : UIManager.getColor("Separator.foreground")), BorderFactory.createEmptyBorder(12, 14, 12, 14)));
        panel.add(new JLabel(title), BorderLayout.NORTH);
        value.setFont(value.getFont().deriveFont(Font.BOLD, 24f));
        panel.add(value, BorderLayout.CENTER);
        return panel;
    }

    public void refresh() {
        var snapshot = stats.snapshot();
        String selected = table.getSelectedRow() < 0 ? null : table.getValueAt(table.getSelectedRow(), 0).toString();
        model.setSnapshot(snapshot);
        if (selected != null) for (int i = 0; i < table.getRowCount(); i++) {
            if (selected.equals(table.getValueAt(i, 0))) { table.setRowSelectionInterval(i, i); break; }
        }
        contacts.setText(Integer.toString(snapshot.total()));
        versions.setText(Integer.toString(snapshot.versions().size()));
        leading.setText(snapshot.versions().stream().max(java.util.Comparator.comparingInt(VersionStats.Row::count)).map(VersionStats.Row::version).orElse("—"));
        contentLayout.show(content, snapshot.total() == 0 ? "empty" : "table");
        systems.removeAll();
        for (var entry : snapshot.systems().entrySet().stream().sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey())).toList()) {
            JPanel row = new JPanel(new BorderLayout(0, 5));
            double percent = entry.getValue() * 100.0 / snapshot.total();
            row.add(new JLabel(systemName(entry.getKey()) + " · " + entry.getValue() + String.format(Locale.GERMANY, " (%.1f %%)", percent)), BorderLayout.NORTH);
            JProgressBar bar = new JProgressBar(0, snapshot.total());
            bar.setValue(entry.getValue());
            bar.setPreferredSize(new Dimension(180, 8));
            row.add(bar, BorderLayout.CENTER);
            systems.add(row);
        }
        systems.revalidate();
        systems.repaint();
    }

    public void reset() { stats.clear(); refresh(); }

    public void updateByDownload(Map<String, Download> downloads) {
        synchronized (downloads) {
            for (var download : downloads.values()) {
                if (download == null) continue;
                for (var source : download.getSources()) {
                    if (source == null || source.getVersion() == null) continue;
                    stats.observe("download:" + source.getId(), source.getVersion().getVersion(), source.getVersion().getBetriebsSystem());
                }
            }
        }
        SwingUtilities.invokeLater(this::refresh);
    }

    public void updateByUploads(Map<String, Upload> uploads) {
        synchronized (uploads) {
            for (var upload : uploads.values()) {
                if (upload == null || upload.getVersion() == null) continue;
                stats.observe("upload:" + upload.getId(), upload.getVersion().getVersion(), upload.getVersion().getBetriebsSystem());
            }
        }
        SwingUtilities.invokeLater(this::refresh);
    }

    public static String systemName(int system) {
        return switch (system) {
            case 1 -> "Windows";
            case 2 -> "Linux";
            case 3 -> "macOS";
            case 4 -> "Solaris";
            case 5 -> "OS/2";
            case 6 -> "FreeBSD";
            case 7 -> "NetWare";
            default -> "Unbekannt";
        };
    }

    JTable table() { return table; }
    JTextField filter() { return filter; }
}
