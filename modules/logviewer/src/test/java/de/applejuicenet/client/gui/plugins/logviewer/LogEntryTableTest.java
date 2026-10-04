package de.applejuicenet.client.gui.plugins.logviewer;

import org.junit.Test;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.List;
import static org.junit.Assert.*;

public class LogEntryTableTest {
    private static LogParser.Entry entry(long ms, LogParser.Level level, String message, String exception) {
        return new LogParser.Entry(ms, "main", level, "de.applejuicenet.client.Foo", 7, message, exception);
    }

    @Test public void filtersByLevelAndTextWithoutLosingEntries() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            LogEntryTable table = new LogEntryTable();
            table.setEntries(List.of(entry(1, LogParser.Level.INFO, "start", ""),
                    entry(2, LogParser.Level.WARN, "langsam", ""),
                    entry(3, LogParser.Level.ERROR, "kaputt", "java.io.IOException: x\n at A.b(A.java:1)")));
            assertEquals(3, table.visibleCount());
            table.setMinimumLevel(LogParser.Level.WARN);
            assertEquals(2, table.visibleCount());
            table.setFilterText("KAPUTT");
            assertEquals(1, table.visibleCount());
            table.setFilterText("IOException");
            assertEquals(1, table.visibleCount());
            table.setFilterText("");
            table.setMinimumLevel(LogParser.Level.TRACE);
            assertEquals(3, table.visibleCount());
        });
    }

    @Test public void firstProblemIsSelectedAutomaticallyAndShowsItsException() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            LogEntryTable table = new LogEntryTable();
            table.setEntries(List.of(entry(1, LogParser.Level.INFO, "start", ""),
                    entry(3, LogParser.Level.ERROR, "kaputt", "java.io.IOException: x")));
            assertTrue(table.detailText().contains("java.io.IOException: x"));
            assertTrue(table.detailText().contains("kaputt"));
            table.setEntries(List.of(entry(1, LogParser.Level.INFO, "nur info", "")));
            assertEquals("", table.detailText());
            table.selectRow(0);
            assertTrue(table.detailText().contains("nur info"));
            table.setEntries(List.of());
            assertEquals("", table.detailText());
        });
    }

    @Test public void rendersEmptyHugeAndTinyViewsWithoutThrowing() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            LogEntryTable table = new LogEntryTable();
            table.setSize(700, 300);
            var image = new BufferedImage(700, 300, BufferedImage.TYPE_INT_RGB);
            var g = image.createGraphics();
            table.doLayout();
            table.printAll(g);
            StringBuilder huge = new StringBuilder();
            for (int i = 0; i < 5000; i++) huge.append("zeile ").append(i).append('\n');
            table.setEntries(List.of(entry(1, LogParser.Level.ERROR, huge.toString(), huge.toString())));
            table.selectRow(0);
            table.setSize(20, 20);
            table.doLayout();
            table.printAll(g);
            g.dispose();
        });
    }
}
