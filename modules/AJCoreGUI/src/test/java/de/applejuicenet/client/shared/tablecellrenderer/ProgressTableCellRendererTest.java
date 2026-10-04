package de.applejuicenet.client.shared.tablecellrenderer;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.util.UIScale;
import org.junit.Test;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ProgressTableCellRendererTest {
    @Test
    public void paddedUploadBarsLeaveClearEdgesAtEveryProgressValue() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            LookAndFeel previous = UIManager.getLookAndFeel();
            try {
                for (LookAndFeel theme : new LookAndFeel[] {new MetalLookAndFeel(), new FlatDarculaLaf()}) {
                    UIManager.setLookAndFeel(theme);
                    JTable table = new JTable(1, 2);
                    for (int fontSize : new int[] {13, 22}) {
                        table.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, fontSize));
                        ProgressTableCellRenderer renderer = new ProgressTableCellRenderer(4);
                        for (boolean selected : new boolean[] {false, true}) {
                            for (double percent : new double[] {0, 50, 100}) {
                                JComponent cell = (JComponent) renderer.getTableCellRendererComponent(
                                        table, percent, selected, false, 0, 0);
                                int padding = UIScale.scale(4);
                                assertEquals(padding, cell.getInsets().left);
                                assertEquals(padding, cell.getInsets().right);
                                assertEquals(renderer.getPreferredSize().width + 2 * padding,
                                        cell.getPreferredSize().width);
                                cell.setSize(cell.getPreferredSize().width, fontSize + 12);
                                cell.doLayout();
                                assertEquals(padding, renderer.getX());
                                assertEquals(cell.getWidth() - 2 * padding, renderer.getWidth());
                                assertTrue(renderer.getWidth() >= renderer.getPreferredSize().width);
                                assertEquals(table.getFont(), renderer.getFont());
                                BufferedImage image = paint(cell);
                                Color background = selected ? table.getSelectionBackground() : table.getBackground();
                                for (int edge = 0; edge < padding; edge++) {
                                    for (int height = 0; height < cell.getHeight(); height++) {
                                        assertEquals(background.getRGB(), image.getRGB(edge, height));
                                        assertEquals(background.getRGB(), image.getRGB(cell.getWidth() - edge - 1, height));
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception error) {
                throw new AssertionError(error);
            } finally {
                try {
                    UIManager.setLookAndFeel(previous);
                } catch (Exception error) {
                    throw new AssertionError(error);
                }
            }
        });
    }

    @Test
    public void downloadRendererKeepsExistingUnpaddedLayout() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            ProgressTableCellRenderer renderer = new ProgressTableCellRenderer();
            assertSame(renderer, renderer.getTableCellRendererComponent(new JTable(1, 1), 100d, false, false, 0, 0));
        });
    }

    private static BufferedImage paint(JComponent cell) {
        BufferedImage image = new BufferedImage(cell.getWidth(), cell.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try {
            cell.paint(graphics);
        } finally {
            graphics.dispose();
        }
        return image;
    }
}
