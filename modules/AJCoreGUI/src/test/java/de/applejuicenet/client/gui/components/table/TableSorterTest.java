package de.applejuicenet.client.gui.components.table;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TableSorterTest {
    private record Row(Object key, String id) {
    }

    private static class Model implements SortableTableModel<Row> {
        final List<Row> rows = new ArrayList<>();

        public int getRowCount() {
            return rows.size();
        }

        public List<Row> getContent() {
            return rows;
        }

        public Object getValueForSortAt(int row, int column) {
            return rows.get(row).key();
        }

        public Class getColumnClass(int column) {
            return Object.class;
        }

        public void sortByColumn(int column, boolean isAscent) {
        }

        public void forceResort() {
        }
    }

    private static List<String> ids(Model model) {
        return model.rows.stream().map(Row::id).toList();
    }

    @Test
    public void sortsNumbersAscendingAndDescending() {
        Model model = new Model();
        model.rows.addAll(List.of(new Row(3L, "c"), new Row(1L, "a"), new Row(2L, "b")));
        TableSorter<Row> sorter = new TableSorter<>(model);
        sorter.sort(0, true);
        assertEquals(List.of("a", "b", "c"), ids(model));
        sorter.sort(0, false);
        assertEquals(List.of("c", "b", "a"), ids(model));
    }

    @Test
    public void nullsComeFirstAscending() {
        Model model = new Model();
        model.rows.addAll(List.of(new Row("b", "b"), new Row(null, "n"), new Row("a", "a")));
        new TableSorter<>(model).sort(0, true);
        assertEquals(List.of("n", "a", "b"), ids(model));
    }

    @Test
    public void textIgnoresCase() {
        Model model = new Model();
        model.rows.addAll(List.of(new Row("b", "1"), new Row("A", "2"), new Row("c", "3")));
        new TableSorter<>(model).sort(0, true);
        assertEquals(List.of("2", "1", "3"), ids(model));
    }

    @Test
    public void equalKeysKeepPreviousOrderInBothDirections() {
        Model model = new Model();
        model.rows.addAll(List.of(new Row(1L, "x1"), new Row(1L, "x2"), new Row(0L, "z"), new Row(1L, "x3")));
        TableSorter<Row> sorter = new TableSorter<>(model);
        sorter.sort(0, true);
        assertEquals(List.of("z", "x1", "x2", "x3"), ids(model));
        sorter.sort(0, false);
        assertEquals(List.of("x1", "x2", "x3", "z"), ids(model));
    }

    @Test
    public void longValuesKeepPrecision() {
        Model model = new Model();
        model.rows.addAll(List.of(new Row(Long.MAX_VALUE, "big"), new Row(Long.MAX_VALUE - 1, "small")));
        new TableSorter<>(model).sort(0, true);
        assertEquals(List.of("small", "big"), ids(model));
    }

    @Test
    public void forceResortAppliesToChangedContent() {
        Model model = new Model();
        model.rows.addAll(List.of(new Row(2L, "b"), new Row(1L, "a")));
        TableSorter<Row> sorter = new TableSorter<>(model);
        sorter.sort(0, true);
        model.rows.add(new Row(0L, "z"));
        sorter.forceResort();
        assertEquals(List.of("z", "a", "b"), ids(model));
    }

    @Test(timeout = 10000)
    public void largeListSortsFast() {
        Model model = new Model();
        Random random = new Random(1);
        for (int i = 0; i < 50000; i++) {
            model.rows.add(new Row((long) random.nextInt(1000000), Integer.toString(i)));
        }
        new TableSorter<>(model).sort(0, true);
        long previous = Long.MIN_VALUE;
        for (Row row : model.rows) {
            long value = (Long) row.key();
            assertTrue(value >= previous);
            previous = value;
        }
        assertEquals(50000, model.rows.size());
    }
}
