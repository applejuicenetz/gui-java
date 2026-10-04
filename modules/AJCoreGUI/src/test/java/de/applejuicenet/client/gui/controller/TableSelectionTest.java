package de.applejuicenet.client.gui.controller;

import org.junit.Test;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TableSelectionTest {
    @Test
    public void selectsSingleEntryAndDoesNotRepeatSelectionEvents() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JTable table = new JTable(1, 1);
            AtomicInteger events = new AtomicInteger();
            table.getSelectionModel().addListSelectionListener(event -> events.incrementAndGet());
            assertTrue(TableSelection.selectOnlyRow(table));
            assertEquals(0, table.getSelectedRow());
            int initialEvents = events.get();
            assertFalse(TableSelection.selectOnlyRow(table));
            assertEquals(initialEvents, events.get());
        });
    }

    @Test
    public void keepsExistingSelectionWhenMultipleEntriesExist() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JTable table = new JTable(3, 1);
            table.setRowSelectionInterval(1, 2);
            assertFalse(TableSelection.selectOnlyRow(table));
            assertEquals(1, table.getSelectedRow());
            assertEquals(2, table.getSelectedRowCount());
            table.clearSelection();
            assertFalse(TableSelection.selectOnlyRow(table));
            assertEquals(-1, table.getSelectedRow());
        });
    }

    @Test
    public void selectsRemainingEntryAfterRefreshAndIgnoresEmptyTable() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            DefaultTableModel model = new DefaultTableModel(2, 1);
            JTable table = new JTable(model);
            table.setRowSelectionInterval(1, 1);
            model.removeRow(1);
            assertTrue(TableSelection.selectOnlyRow(table));
            assertEquals(0, table.getSelectedRow());
            model.setRowCount(0);
            assertFalse(TableSelection.selectOnlyRow(table));
            assertEquals(-1, table.getSelectedRow());
        });
    }
}
