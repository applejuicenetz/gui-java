package de.applejuicenet.client.gui.plugins;

import org.junit.Test;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Properties;
import static org.junit.Assert.*;

public class SpeedGraphViewTest {
    @Test public void rendersWithoutMutatingHistoryAndShowsStaleValuesHonestly() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            SpeedHistory history = new SpeedHistory(3600000, 100);
            history.add(1000, 1024, 1048576);
            GraphPanel view = new GraphPanel(history, new Properties(), () -> {});
            view.refresh(1000);
            assertTrue(view.downloadText().contains("1 MiB/s"));
            view.setSize(900, 450);
            view.doLayout();
            BufferedImage image = new BufferedImage(900, 450, BufferedImage.TYPE_INT_RGB);
            var graphics = image.createGraphics();
            view.paint(graphics);
            view.paint(graphics);
            graphics.dispose();
            assertEquals(1, history.snapshot(1000, 3600000).size());
            view.refresh(20000);
            assertEquals("—", view.downloadText());
        });
    }
    @Test public void chartHandlesEmptyHugeAndTinyViewsAndTooltip() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            UpDownChart chart = new UpDownChart();
            chart.setSize(800, 300);
            chart.setData(List.of(new SpeedHistory.Sample(1000, Long.MAX_VALUE, 1048576)), 1000, 60000, SpeedFormat.BINARY);
            var image = new BufferedImage(800, 300, BufferedImage.TYPE_INT_RGB);
            var graphics = image.createGraphics();
            chart.paint(graphics);
            assertTrue(chart.getToolTipText(new MouseEvent(chart, MouseEvent.MOUSE_MOVED, 0, 0, 780, 100, 0, false)).contains("MiB/s"));
            chart.setData(List.of(), 20000, 60000, SpeedFormat.BITS);
            chart.setSize(10, 10);
            chart.paint(graphics);
            graphics.dispose();
        });
    }
}
