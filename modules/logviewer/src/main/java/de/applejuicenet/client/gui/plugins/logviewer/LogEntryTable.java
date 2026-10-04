package de.applejuicenet.client.gui.plugins.logviewer;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;
import java.util.Locale;

/** Filterable entry table with the stack trace shown only for the selected entry. */
final class LogEntryTable extends JPanel {
    private static final String[] COLUMNS = {"Zeit", "Stufe", "Quelle", "Meldung"};
    private final EntryModel model = new EntryModel();
    private final JTable table = new JTable(model);
    private final TableRowSorter<EntryModel> sorter = new TableRowSorter<>(model);
    private final JTextArea detail = new JTextArea();
    private LogParser.Level minimum = LogParser.Level.TRACE;
    private String text = "";

    LogEntryTable() {
        super(new BorderLayout());
        table.setRowSorter(sorter);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(table.getFontMetrics(table.getFont()).getHeight() + 10);
        table.getColumnModel().getColumn(0).setPreferredWidth(120);
        table.getColumnModel().getColumn(0).setMinWidth(110);
        table.getColumnModel().getColumn(1).setPreferredWidth(64);
        table.getColumnModel().getColumn(2).setPreferredWidth(190);
        table.getColumnModel().getColumn(3).setPreferredWidth(600);
        table.setDefaultRenderer(Object.class, new Cells());
        detail.setEditable(false);
        detail.setLineWrap(false);
        detail.setFont(new Font(Font.MONOSPACED, Font.PLAIN, table.getFont().getSize()));
        detail.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) showDetail();
        });
        JScrollPane detailScroll = new JScrollPane(detail);
        detailScroll.setPreferredSize(new Dimension(100, 150));
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(table), detailScroll);
        split.setResizeWeight(0.7);
        add(split, BorderLayout.CENTER);
    }

    void setEntries(List<LogParser.Entry> entries) {
        model.set(entries);
        detail.setText("");
        int firstProblem = -1;
        for (int i = 0; i < entries.size() && firstProblem < 0; i++) {
            if (entries.get(i).level().compareTo(LogParser.Level.WARN) >= 0) firstProblem = i;
        }
        if (firstProblem >= 0) selectRow(table.convertRowIndexToView(firstProblem));
        else showDetail();
    }

    void setMinimumLevel(LogParser.Level level) { minimum = level; applyFilter(); }
    void setFilterText(String value) { text = value == null ? "" : value.toLowerCase(Locale.ROOT); applyFilter(); }
    int visibleCount() { return table.getRowCount(); }
    String detailText() { return detail.getText(); }

    void selectRow(int viewRow) {
        if (viewRow >= 0 && viewRow < table.getRowCount()) table.setRowSelectionInterval(viewRow, viewRow);
        showDetail();
    }

    private void applyFilter() {
        sorter.setRowFilter(new RowFilter<>() {
            @Override public boolean include(Entry<? extends EntryModel, ? extends Integer> row) {
                LogParser.Entry entry = model.at(row.getIdentifier());
                return entry.level().compareTo(minimum) >= 0
                        && (text.isEmpty() || haystack(entry).contains(text));
            }
        });
        detail.setText("");
    }

    private static String haystack(LogParser.Entry e) {
        return (e.message() + ' ' + e.logger() + ' ' + e.thread() + ' ' + e.exception()).toLowerCase(Locale.ROOT);
    }

    private void showDetail() {
        int row = table.getSelectedRow();
        if (row < 0) { detail.setText(""); return; }
        LogParser.Entry e = model.at(table.convertRowIndexToModel(row));
        StringBuilder out = new StringBuilder();
        out.append(e.level()).append("  ").append(e.logger()).append(':').append(e.line())
                .append("  [").append(e.thread()).append("]\n\n").append(e.message());
        if (!e.exception().isEmpty()) out.append("\n\n").append(e.exception());
        detail.setText(out.toString());
        detail.setCaretPosition(0);
    }

    static Color levelColor(LogParser.Level level, boolean dark) {
        return switch (level) {
            case ERROR -> dark ? new Color(255, 120, 110) : new Color(190, 30, 30);
            case WARN -> dark ? new Color(235, 180, 70) : new Color(170, 100, 0);
            case DEBUG, TRACE -> dark ? new Color(150, 150, 150) : new Color(110, 110, 110);
            default -> null;
        };
    }

    private static final class EntryModel extends AbstractTableModel {
        private List<LogParser.Entry> entries = List.of();
        void set(List<LogParser.Entry> value) { entries = List.copyOf(value); fireTableDataChanged(); }
        LogParser.Entry at(int index) { return entries.get(index); }
        @Override public int getRowCount() { return entries.size(); }
        @Override public int getColumnCount() { return COLUMNS.length; }
        @Override public String getColumnName(int column) { return COLUMNS[column]; }
        @Override public Object getValueAt(int row, int column) {
            LogParser.Entry e = entries.get(row);
            return switch (column) {
                case 0 -> String.format(Locale.GERMANY, "+%d,%03d s", e.relativeMillis() / 1000, e.relativeMillis() % 1000);
                case 1 -> e.level();
                case 2 -> e.shortLogger();
                default -> e.message().lines().findFirst().orElse("") + (e.exception().isEmpty() ? "" : "  (Stacktrace)");
            };
        }
    }

    private final class Cells extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object value, boolean selected,
                                                                 boolean focus, int row, int column) {
            super.getTableCellRendererComponent(t, value, selected, focus, row, column);
            setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
            LogParser.Entry e = model.at(t.convertRowIndexToModel(row));
            boolean dark = t.getBackground().getRed() + t.getBackground().getGreen() + t.getBackground().getBlue() < 384;
            Color tint = levelColor(e.level(), dark);
            boolean loud = e.level().compareTo(LogParser.Level.WARN) >= 0;
            setFont(t.getFont().deriveFont(loud && column == 1 ? Font.BOLD : Font.PLAIN));
            if (!selected) {
                setForeground(tint != null && (loud || column == 1) ? tint : t.getForeground());
                setBackground(loud ? blend(t.getBackground(), tint, 0.12f) : t.getBackground());
            }
            if (column == 0) setToolTipText(e.relativeMillis() + " ms nach Start");
            else if (column == 3) setToolTipText(e.message().isEmpty() ? null : "<html>" + escape(e.message()).replace("\n", "<br>") + "</html>");
            else setToolTipText(null);
            return this;
        }

        private Color blend(Color base, Color tint, float amount) {
            return new Color(Math.round(base.getRed() * (1 - amount) + tint.getRed() * amount),
                    Math.round(base.getGreen() * (1 - amount) + tint.getGreen() * amount),
                    Math.round(base.getBlue() * (1 - amount) + tint.getBlue() * amount));
        }

        private String escape(String value) {
            return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        }
    }
}
