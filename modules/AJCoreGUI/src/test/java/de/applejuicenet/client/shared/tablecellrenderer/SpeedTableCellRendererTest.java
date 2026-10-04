package de.applejuicenet.client.shared.tablecellrenderer;

import org.junit.Test;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import static org.junit.Assert.assertEquals;

public class SpeedTableCellRendererTest {
    @Test
    public void changesUnitsAtKilobyteMegabyteAndGigabyteBoundaries() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            SpeedTableCellRenderer renderer = new SpeedTableCellRenderer();
            JTable table = new JTable(1, 1);
            assertSpeed(renderer, table, 1, "1 Bytes/s");
            assertSpeed(renderer, table, 1023, "1023 Bytes/s");
            assertSpeed(renderer, table, 1024, "1,0 KB/s");
            assertSpeed(renderer, table, (1 << 20) - 1, "1023,99 KB/s");
            assertSpeed(renderer, table, 1 << 20, "1,0 MB/s");
            assertSpeed(renderer, table, (1 << 30) - 1, "1023,99 MB/s");
            assertSpeed(renderer, table, 1 << 30, "1,0 GB/s");
            assertSpeed(renderer, table, 1L << 40, "1,0 TB/s");
        });
    }

    @Test
    public void formatsFractionalRatesAndKeepsRightAlignment() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            SpeedTableCellRenderer renderer = new SpeedTableCellRenderer();
            JTable table = new JTable(1, 1);
            assertSpeed(renderer, table, 1536, "1,5 KB/s");
            assertSpeed(renderer, table, 3 * (1 << 19), "1,5 MB/s");
            assertEquals(SwingConstants.RIGHT, renderer.getHorizontalAlignment());
        });
    }

    @Test
    public void zeroAndEmptyCellsClearPreviousSpeed() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            SpeedTableCellRenderer renderer = new SpeedTableCellRenderer();
            JTable table = new JTable(1, 1);
            assertSpeed(renderer, table, 1 << 20, "1,0 MB/s");
            assertSpeed(renderer, table, 0, "");
            assertSpeed(renderer, table, 0L, "");
            assertSpeed(renderer, table, null, "");
        });
    }

    private static void assertSpeed(SpeedTableCellRenderer renderer, JTable table, Number speed, String expected) {
        JLabel cell = (JLabel) renderer.getTableCellRendererComponent(table, speed, false, false, 0, 0);
        assertEquals(expected, cell.getText());
    }
}
