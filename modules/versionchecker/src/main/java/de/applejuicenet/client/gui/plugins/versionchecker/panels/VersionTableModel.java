package de.applejuicenet.client.gui.plugins.versionchecker.panels;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class VersionTableModel extends AbstractTableModel {
    private List<VersionStats.Row> rows = List.of();
    private int total;
    private static final String[] COLUMNS = {"Core-Version", "Kontakte", "Anteil", "Betriebssysteme"};

    public void setSnapshot(VersionStats.Snapshot snapshot) {
        rows = snapshot.versions();
        total = snapshot.total();
        fireTableDataChanged();
    }

    @Override public int getRowCount() { return rows.size(); }
    @Override public int getColumnCount() { return COLUMNS.length; }
    @Override public String getColumnName(int column) { return COLUMNS[column]; }
    @Override public Class<?> getColumnClass(int column) {
        return column == 1 ? Integer.class : column == 2 ? Double.class : String.class;
    }
    @Override public Object getValueAt(int row, int column) {
        var entry = rows.get(row);
        return switch (column) {
            case 0 -> entry.version();
            case 1 -> entry.count();
            case 2 -> total == 0 ? 0.0 : entry.count() * 100.0 / total;
            case 3 -> entry.systems().entrySet().stream().sorted(java.util.Map.Entry.comparingByKey())
                    .map(e -> MainPanel.systemName(e.getKey()) + ": " + e.getValue()).collect(java.util.stream.Collectors.joining(" · "));
            default -> throw new IndexOutOfBoundsException();
        };
    }
}
