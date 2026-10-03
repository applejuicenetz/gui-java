package de.applejuicenet.client.gui.controller;

import javax.swing.JTable;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;
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
    private static final int MAX_FIT_ROWS = 200;
    private static final int MAX_FIT_WIDTH = 400;
    private static final int FIT_MARGIN = 12;
    private static final int MIN_NAME_WIDTH = 150;

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
        List<TableColumn> unsaved = new ArrayList<>();
        for (TableColumn column : columns) {
            int width = read.applyAsInt(key(column, "width"));
            if (width > 0) {
                column.setPreferredWidth(width);
                column.setWidth(width);
            } else {
                unsaved.add(column);
            }
        }
        boolean[] autoFitting = new boolean[1];
        visible.sort(Comparator.comparingInt(column -> {
            int index = read.applyAsInt(key(column, "index"));
            return index < 0 ? column.getModelIndex() : index;
        }));
        for (int index = 0; index < visible.size(); index++) {
            model.moveColumn(model.getColumnIndex(visible.get(index).getIdentifier()), index);
        }
        for (TableColumn column : columns) {
            column.addPropertyChangeListener(event -> {
                if (!table.isShowing() || autoFitting[0]) {
                    return;
                }
                if ("width".equals(event.getPropertyName()) && table.getTableHeader() != null
                        && table.getTableHeader().getResizingColumn() == column) {
                    unsaved.remove(column);
                    write.accept(key(column, "width"), column.getWidth());
                } else if ("preferredWidth".equals(event.getPropertyName())) {
                    write.accept(key(column, "width"), column.getPreferredWidth());
                }
            });
        }
        if (!unsaved.isEmpty()) {
            installAutoFit(table, unsaved, autoFitting);
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

    private static void installAutoFit(JTable table, List<TableColumn> unsaved, boolean[] autoFitting) {
        boolean[] done = new boolean[1];
        Runnable fitAll = () -> {
            if (done[0] || table.getRowCount() == 0) {
                return;
            }
            done[0] = true;
            autoFitting[0] = true;
            try {
                for (TableColumn column : unsaved) {
                    fit(table, column);
                }
                fillWithNameColumn(table, unsaved);
            } finally {
                autoFitting[0] = false;
            }
        };

        table.getModel().addTableModelListener(event -> SwingUtilities.invokeLater(fitAll));
        ComponentAdapter onResize = new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent event) {
                if (!done[0] || unsaved.isEmpty()) {
                    return;
                }
                autoFitting[0] = true;
                try {
                    fillWithNameColumn(table, unsaved);
                } finally {
                    autoFitting[0] = false;
                }
            }
        };
        table.addHierarchyListener(event -> {
            if (table.getParent() != null) {
                table.getParent().removeComponentListener(onResize);
                table.getParent().addComponentListener(onResize);
            }
        });
        if (table.getParent() != null) {
            table.getParent().addComponentListener(onResize);
        }
        SwingUtilities.invokeLater(fitAll);
    }

    private static void fillWithNameColumn(JTable table, List<TableColumn> unsaved) {
        TableColumn nameColumn = null;
        int othersWidth = 0;
        for (int index = 0; index < table.getColumnModel().getColumnCount(); index++) {
            TableColumn column = table.getColumnModel().getColumn(index);
            if (column.getModelIndex() == 0 && unsaved.contains(column)) {
                nameColumn = column;
            } else {
                othersWidth += column.getWidth();
            }
        }
        if (nameColumn != null) {
            int available = (table.getParent() != null ? table.getParent().getWidth() : table.getWidth()) - othersWidth;
            int width = Math.max(MIN_NAME_WIDTH, available);
            nameColumn.setPreferredWidth(width);
            nameColumn.setWidth(width);
        }
    }

    private static void fit(JTable table, TableColumn column) {
        int viewIndex = -1;
        for (int index = 0; index < table.getColumnModel().getColumnCount(); index++) {
            if (table.getColumnModel().getColumn(index) == column) {
                viewIndex = index;
                break;
            }
        }
        if (viewIndex < 0) {
            return;
        }
        TableCellRenderer headerRenderer = column.getHeaderRenderer() != null ? column.getHeaderRenderer()
                : table.getTableHeader().getDefaultRenderer();
        int width = headerRenderer.getTableCellRendererComponent(table, column.getHeaderValue(), false, false, -1, viewIndex)
                .getPreferredSize().width;
        int rows = Math.min(table.getRowCount(), MAX_FIT_ROWS);
        for (int row = 0; row < rows; row++) {
            width = Math.max(width, table.prepareRenderer(table.getCellRenderer(row, viewIndex), row, viewIndex)
                    .getPreferredSize().width);
        }
        width = Math.min(width + FIT_MARGIN, column.getModelIndex() == 0 ? Integer.MAX_VALUE : MAX_FIT_WIDTH);
        column.setPreferredWidth(width);
        column.setWidth(width);
    }

    private static String key(TableColumn column, String setting) {
        return "column" + column.getModelIndex() + "_" + setting;
    }
}
