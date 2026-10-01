package de.applejuicenet.client.gui.controller;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.ObjIntConsumer;
import java.util.function.ToIntFunction;

public final class TableColumnSettings {
    private TableColumnSettings() {
    }

    public static void install(JTable table, String view, TableColumn[] columns) {
        PropertiesManager manager = PropertiesManager.getInstance();
        install(table, columns,
                key -> manager.getTableColumnSetting("options_columns_" + view + "_" + key),
                (key, value) -> manager.setTableColumnSetting("options_columns_" + view + "_" + key, value));
    }

    static void install(JTable table, TableColumn[] columns, ToIntFunction<String> read,
                        ObjIntConsumer<String> write) {
        TableColumnModel model = table.getColumnModel();
        List<TableColumn> visible = new ArrayList<>();
        for (int index = 0; index < model.getColumnCount(); index++) {
            visible.add(model.getColumn(index));
        }
        for (TableColumn column : columns) {
            int width = read.applyAsInt(key(column, "width"));
            if (width > 0) {
                column.setPreferredWidth(width);
                column.setWidth(width);
            }
        }
        visible.sort(Comparator.comparingInt(column -> {
            int index = read.applyAsInt(key(column, "index"));
            return index < 0 ? column.getModelIndex() : index;
        }));
        for (int index = 0; index < visible.size(); index++) {
            model.moveColumn(model.getColumnIndex(visible.get(index).getIdentifier()), index);
        }
        for (TableColumn column : columns) {
            column.addPropertyChangeListener(event -> {
                if (!table.isShowing()) {
                    return;
                }
                if ("width".equals(event.getPropertyName()) && table.getTableHeader() != null
                        && table.getTableHeader().getResizingColumn() == column) {
                    write.accept(key(column, "width"), column.getWidth());
                } else if ("preferredWidth".equals(event.getPropertyName())) {
                    write.accept(key(column, "width"), column.getPreferredWidth());
                }
            });
        }
        model.addColumnModelListener(new TableColumnModelListener() {
            private void saveOrder() {
                if (!table.isShowing()) {
                    return;
                }
                for (int index = 0; index < model.getColumnCount(); index++) {
                    write.accept(key(model.getColumn(index), "index"), index);
                }
            }

            @Override public void columnAdded(TableColumnModelEvent event) { saveOrder(); }
            @Override public void columnRemoved(TableColumnModelEvent event) { saveOrder(); }
            @Override public void columnMoved(TableColumnModelEvent event) { saveOrder(); }
            @Override public void columnMarginChanged(javax.swing.event.ChangeEvent event) { }
            @Override public void columnSelectionChanged(ListSelectionEvent event) { }
        });
    }

    private static String key(TableColumn column, String setting) {
        return "column" + column.getModelIndex() + "_" + setting;
    }
}
