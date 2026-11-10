package de.applejuicenet.client.gui.search.table;

import de.applejuicenet.client.facade.entity.FileName;
import de.applejuicenet.client.facade.entity.SearchEntry;
import de.applejuicenet.client.facade.shared.FileType;
import org.junit.Test;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import java.awt.Color;

import static org.junit.Assert.assertEquals;

public class SearchEntryIconRendererTest {
    @Test
    public void sharedResultDoesNotChangeFollowingResultColors() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JTable table = new JTable(1, 1);
            table.setForeground(Color.WHITE);
            table.setBackground(Color.DARK_GRAY);
            table.setSelectionForeground(Color.YELLOW);
            table.setSelectionBackground(Color.BLUE);
            SearchEntryIconRenderer renderer = new SearchEntryIconRenderer();
            String checksum = "renderer-regression-shared";
            SearchEntryIconRenderer.addMd5Sum(checksum);
            try {
                JLabel shared = render(renderer, table, checksum, false);
                assertEquals(Color.BLACK, shared.getForeground());
                assertEquals(Color.GREEN, shared.getBackground());
                JLabel normal = render(renderer, table, null, false);
                assertEquals(table.getForeground(), normal.getForeground());
                assertEquals(table.getBackground(), normal.getBackground());
                render(renderer, table, checksum, false);
                JLabel selected = render(renderer, table, null, true);
                assertEquals(table.getSelectionForeground(), selected.getForeground());
                assertEquals(table.getSelectionBackground(), selected.getBackground());
                normal = render(renderer, table, null, false);
                assertEquals(table.getForeground(), normal.getForeground());
            } finally {
                SearchEntryIconRenderer.removeMd5Sum(checksum);
            }
        });
    }

    private static JLabel render(SearchEntryIconRenderer renderer, JTable table, String checksum, boolean selected) {
        SearchEntry entry = new SearchEntry() {
            public int getId() { return 1; }
            public int getSearchId() { return 1; }
            public String getChecksum() { return checksum; }
            public long getSize() { return 1; }
            public FileType getFileType() { return FileType.TEXT; }
            public FileName[] getFileNames() {
                return new FileName[] { new FileName() {
                    public String getFileName() { return "result.txt"; }
                    public int getFrequency() { return 1; }
                    public FileType getFileType() { return FileType.TEXT; }
                } };
            }
        };
        return (JLabel) renderer.getTableCellRendererComponent(table, entry, selected, false, 0, 0);
    }
}
