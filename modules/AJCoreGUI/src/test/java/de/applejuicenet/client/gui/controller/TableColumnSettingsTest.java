package de.applejuicenet.client.gui.controller;

import org.junit.Test;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TableColumnSettingsTest {
    @Test
    public void compactRefreshFitsChangedContentWithoutModelEventOrSelectionLoss() throws Exception {
        AtomicReference<JTable> tableReference = new AtomicReference<>();
        String[] values = {"file.bin", "1 KB/s"};
        SwingUtilities.invokeAndWait(() -> {
            JTable table = new JTable(new javax.swing.table.AbstractTableModel() {
                @Override public int getRowCount() { return 2; }
                @Override public int getColumnCount() { return 2; }
                @Override public Object getValueAt(int row, int column) { return values[column]; }
            });
            table.setSize(1200, 300);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            TableColumnSettings.install(table, columns(table), key -> -1, (key, value) -> { }, true);
            table.setRowSelectionInterval(1, 1);
            tableReference.set(table);
        });
        SwingUtilities.invokeAndWait(() -> {
            JTable table = tableReference.get();
            assertEquals(requiredWidth(table, 1), table.getColumnModel().getColumn(1).getWidth());
            values[1] = "Much longer speed value";
            TableColumnSettings.refreshCompact(table);
        });
        SwingUtilities.invokeAndWait(() -> {
            JTable table = tableReference.get();
            assertEquals(requiredWidth(table, 1), table.getColumnModel().getColumn(1).getWidth());
            assertEquals(1200 - requiredWidth(table, 1), table.getColumnModel().getColumn(0).getWidth());
            assertEquals(1, table.getSelectedRow());
        });
    }

    @Test
    public void compactDestinationStaysBoundedAndFilenameFillsRemainingWidth() throws Exception {
        AtomicReference<JTable> tableReference = new AtomicReference<>();
        AtomicReference<TableColumn> destinationReference = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> {
            JTable table = new JTable(new DefaultTableModel(
                    new Object[][] {{"file.bin", "Downloading", "/long-directory".repeat(60)}},
                    new String[] {"Dateiname", "Status", "Zielverzeichnis"}));
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.getViewport().setSize(1200, 300);
            TableColumn[] columns = columns(table);
            columns[2].setMaxWidth(280);
            destinationReference.set(columns[2]);
            tableReference.set(table);
            TableColumnSettings.install(table, columns, key -> -1, (key, value) -> { }, true);
            table.getColumnModel().moveColumn(2, 0);
        });
        SwingUtilities.invokeAndWait(() -> {
            JTable table = tableReference.get();
            assertEquals(280, destinationReference.get().getWidth());
            assertEquals(1200 - 280 - requiredWidth(table, 1),
                    table.getColumnModel().getColumn(table.convertColumnIndexToView(0)).getWidth());
            table.removeColumn(destinationReference.get());
        });
        SwingUtilities.invokeAndWait(() -> {
            JTable table = tableReference.get();
            assertEquals(1200 - requiredWidth(table, 1), table.getColumnModel().getColumn(0).getWidth());
            table.addColumn(destinationReference.get());
            ((DefaultTableModel) table.getModel()).setValueAt("/short", 0, 2);
        });
        SwingUtilities.invokeAndWait(() -> {
            JTable table = tableReference.get();
            assertEquals(requiredWidth(table, 2), destinationReference.get().getWidth());
            assertEquals(1200 - requiredWidth(table, 1) - requiredWidth(table, 2),
                    table.getColumnModel().getColumn(0).getWidth());
        });
    }

    @Test
    public void compactTablesDoNotReadOrSaveWidthsUnlessRemembered() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JFrame frame = new JFrame();
            try {
                JTable table = table();
                Properties written = new Properties();
                TableColumnSettings.install(table, columns(table), key -> {
                    assertTrue(!key.endsWith("_width"));
                    return -1;
                }, (key, value) -> written.setProperty(key, Integer.toString(value)), true);
                frame.add(new JScrollPane(table));
                frame.setSize(800, 400);
                frame.setVisible(true);
                table.getColumnModel().getColumn(1).setPreferredWidth(260);
                table.getColumnModel().moveColumn(1, 2);
                assertTrue(written.stringPropertyNames().stream().noneMatch(key -> key.endsWith("_width")));
                assertTrue(written.containsKey("column1_index"));
            } finally {
                frame.dispose();
            }
        });
    }

    @Test
    public void compactTablesReadSavedWidthsWhenRemembered() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JTable table = table();
            TableColumnSettings.install(table, columns(table), key -> "column1_width".equals(key) ? 123 : -1,
                    (key, value) -> { }, true, () -> true, () -> { });
            assertEquals(123, table.getColumnModel().getColumn(1).getPreferredWidth());
        });
    }

    @Test
    public void manualResizeActivatesRememberingSavesAllWidthsAndStopsAutoFit() throws Exception {
        AtomicReference<JTable> tableReference = new AtomicReference<>();
        String[] values = {"file.bin", "1 KB/s"};
        Properties written = new Properties();
        boolean[] remembered = new boolean[1];
        SwingUtilities.invokeAndWait(() -> {
            JTable table = new JTable(new javax.swing.table.AbstractTableModel() {
                @Override public int getRowCount() { return 2; }
                @Override public int getColumnCount() { return 2; }
                @Override public Object getValueAt(int row, int column) { return values[column]; }
            });
            table.setSize(1200, 300);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            TableColumnSettings.install(table, columns(table), key -> -1,
                    (key, value) -> written.setProperty(key, Integer.toString(value)), true,
                    () -> remembered[0], () -> remembered[0] = true);
            tableReference.set(table);
        });
        SwingUtilities.invokeAndWait(() -> {
            JTable table = tableReference.get();
            JFrame frame = new JFrame();
            frame.add(new JScrollPane(table));
            frame.setSize(1200, 400);
            frame.setVisible(true);
            try {
                resizeColumn(table, 1, 400);
                assertTrue(remembered[0]);
                assertEquals("400", written.getProperty("column1_width"));
                assertTrue(written.containsKey("column0_width"));
                values[1] = "Much longer speed value than before";
                TableColumnSettings.refreshCompact(table);
            } finally {
                frame.dispose();
            }
        });
        SwingUtilities.invokeAndWait(() -> assertEquals(400, tableReference.get().getColumnModel().getColumn(1).getWidth()));
    }

    @Test
    public void compactColumnsIgnoreSavedWidthsAndFitAllRowsAfterReordering() throws Exception {
        AtomicReference<JTable> tableReference = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> {
            Object[][] rows = new Object[220][3];
            rows[210][2] = "1234567890".repeat(12);
            JTable table = new JTable(new DefaultTableModel(rows, new String[] {"Name", "Size", "Requests"}));
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.getViewport().setSize(1600, 300);
            tableReference.set(table);
            TableColumnSettings.install(table, columns(table), key -> {
                if (key.endsWith("width")) {
                    return 500;
                }
                return "column2_index".equals(key) ? 0 : "column0_index".equals(key) ? 1 : 2;
            }, (key, value) -> { }, true);
        });
        SwingUtilities.invokeAndWait(() -> {
            JTable table = tableReference.get();
            assertEquals(2, table.getColumnModel().getColumn(0).getModelIndex());
            assertEquals(requiredWidth(table, 2), table.getColumnModel().getColumn(0).getWidth());
            assertTrue(table.getColumnModel().getColumn(0).getWidth() > 400);
            assertEquals(requiredWidth(table, 1), table.getColumnModel().getColumn(2).getWidth());
            assertEquals(1600 - requiredWidth(table, 1) - requiredWidth(table, 2),
                    table.getColumnModel().getColumn(1).getWidth());
            ((DefaultTableModel) table.getModel()).setValueAt("1", 210, 2);
        });
        SwingUtilities.invokeAndWait(() -> {
            JTable table = tableReference.get();
            assertEquals(requiredWidth(table, 2), table.getColumnModel().getColumn(0).getWidth());
            assertTrue(table.getColumnModel().getColumn(0).getWidth() < 400);
            assertEquals(1600 - requiredWidth(table, 1) - requiredWidth(table, 2),
                    table.getColumnModel().getColumn(1).getWidth());
        });
    }

    @Test
    public void compactEmptyTableFitsHeadersAndUpdatesChangedCaptions() throws Exception {
        AtomicReference<JTable> tableReference = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> {
            JTable table = table();
            table.setSize(1000, 300);
            tableReference.set(table);
            TableColumnSettings.install(table, columns(table), key -> -1, (key, value) -> { }, true);
        });
        SwingUtilities.invokeAndWait(() -> {
            JTable table = tableReference.get();
            assertEquals(requiredWidth(table, 1), table.getColumnModel().getColumn(1).getWidth());
            table.getColumnModel().getColumn(1).setHeaderValue("Much longer size caption");
        });
        SwingUtilities.invokeAndWait(() -> {
            JTable table = tableReference.get();
            assertEquals(requiredWidth(table, 1), table.getColumnModel().getColumn(1).getWidth());
            assertEquals(1000 - requiredWidth(table, 1) - requiredWidth(table, 2),
                    table.getColumnModel().getColumn(0).getWidth());
        });
    }

    private static int requiredWidth(JTable table, int modelIndex) {
        int viewIndex = table.convertColumnIndexToView(modelIndex);
        TableColumn column = table.getColumnModel().getColumn(viewIndex);
        TableCellRenderer renderer = table.getTableHeader().getDefaultRenderer();
        int width = renderer.getTableCellRendererComponent(table, column.getHeaderValue(), false, false, -1, viewIndex)
                .getPreferredSize().width;
        for (int row = 0; row < table.getRowCount(); row++) {
            width = Math.max(width, table.prepareRenderer(table.getCellRenderer(row, viewIndex), row, viewIndex)
                    .getPreferredSize().width);
        }
        return width + 12;
    }

    @Test
    public void preservesDifferentLayoutsAcrossTabChangesAndReload() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JFrame frame = new JFrame();
            try {
                Properties settings = new Properties();
                JTable downloads = table();
                JTable uploads = table();
                JTable untouched = table();
                settings.setProperty("share_column0_width", "247");
                install(downloads, "download", settings);
                install(uploads, "upload", settings);
                install(untouched, "share", settings);
                JTabbedPane tabs = new JTabbedPane();
                tabs.addTab("Downloads", new JScrollPane(downloads));
                tabs.addTab("Uploads", new JScrollPane(uploads));
                tabs.addTab("Share", new JScrollPane(untouched));
                frame.add(tabs);
                frame.setSize(640, 400);
                frame.setVisible(true);
                resizeColumn(downloads, 0, 231);
                downloads.getColumnModel().moveColumn(0, 2);
                tabs.setSelectedIndex(1);
                resizeColumn(uploads, 0, 173);
                uploads.getColumnModel().moveColumn(2, 0);
                downloads.getColumnModel().getColumn(2).setPreferredWidth(75);

                StringWriter writer = new StringWriter();
                settings.store(writer, null);
                Properties reloaded = new Properties();
                reloaded.load(new StringReader(writer.toString()));
                JTable restoredDownloads = table();
                JTable restoredUploads = table();
                JTable restoredShare = table();
                install(restoredDownloads, "download", reloaded);
                install(restoredUploads, "upload", reloaded);
                install(restoredShare, "share", reloaded);
                assertEquals(231, restoredDownloads.getColumnModel().getColumn(2).getPreferredWidth());
                assertEquals(0, restoredDownloads.getColumnModel().getColumn(2).getModelIndex());
                assertEquals(173, restoredUploads.getColumnModel().getColumn(1).getPreferredWidth());
                assertEquals(2, restoredUploads.getColumnModel().getColumn(0).getModelIndex());
                assertEquals(247, restoredShare.getColumnModel().getColumn(0).getPreferredWidth());
            } catch (Exception exception) {
                throw new AssertionError(exception);
            } finally {
                frame.dispose();
            }
        });
    }

    @Test
    public void keepsWidthsAssociatedWithHiddenModelColumns() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            Properties settings = new Properties();
            settings.setProperty("download_column0_width", "200");
            settings.setProperty("download_column1_width", "160");
            settings.setProperty("download_column2_width", "90");
            settings.setProperty("download_column0_index", "1");
            settings.setProperty("download_column2_index", "0");
            JTable table = table();
            TableColumn[] columns = columns(table);
            table.removeColumn(columns[1]);
            TableColumnSettings.install(table, columns,
                    key -> Integer.parseInt(settings.getProperty("download_" + key, "-1")),
                    (key, value) -> settings.setProperty("download_" + key, Integer.toString(value)));
            assertEquals(2, table.getColumnCount());
            assertEquals(2, table.getColumnModel().getColumn(0).getModelIndex());
            assertEquals(200, columns[0].getPreferredWidth());
            assertEquals(160, columns[1].getPreferredWidth());
            assertEquals(90, columns[2].getPreferredWidth());
        });
    }

    private static JTable table() {
        JTable table = new JTable(new Object[0][3], new String[]{"Name", "Size", "Status"});
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        return table;
    }

    private static void resizeColumn(JTable table, int index, int width) {
        TableColumn column = table.getColumnModel().getColumn(index);
        table.getTableHeader().setResizingColumn(column);
        column.setWidth(width);
        table.setSize(table.getColumnModel().getTotalColumnWidth(), table.getHeight());
        table.doLayout();
        table.getTableHeader().setResizingColumn(null);
    }

    private static TableColumn[] columns(JTable table) {
        TableColumn[] columns = new TableColumn[table.getColumnCount()];
        for (int index = 0; index < columns.length; index++) {
            columns[index] = table.getColumnModel().getColumn(index);
        }
        return columns;
    }

    private static void install(JTable table, String view, Properties settings) {
        TableColumnSettings.install(table, columns(table),
                key -> Integer.parseInt(settings.getProperty(view + "_" + key, "-1")),
                (key, value) -> settings.setProperty(view + "_" + key, Integer.toString(value)));
    }
}
