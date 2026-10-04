package de.applejuicenet.client.gui.plugins.versionchecker.panels;

import org.junit.Test;
import javax.swing.*;
import java.awt.image.BufferedImage;
import static org.junit.Assert.*;

public class MainPanelTest {
    @Test public void showsNumericCountsAndFiltersWithoutChangingTotals() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            VersionStats stats = new VersionStats();
            stats.observe("d:1", "0.31.10", 2);
            stats.observe("u:1", "0.31.9", 1);
            MainPanel panel = new MainPanel(stats);
            panel.refresh();
            assertEquals(2, panel.table().getRowCount());
            assertEquals(Integer.class, panel.table().getModel().getColumnClass(1));
            assertEquals("0.31.10", panel.table().getValueAt(0, 0));
            panel.filter().setText("0.31.9");
            assertEquals(1, panel.table().getRowCount());
            assertEquals(2, stats.snapshot().total());
            panel.setSize(1000, 600);
            panel.doLayout();
            var image = new BufferedImage(1000, 600, BufferedImage.TYPE_INT_RGB);
            var g = image.createGraphics();
            panel.printAll(g);
            g.dispose();
            panel.reset();
            assertEquals(0, panel.table().getRowCount());
        });
    }
}
