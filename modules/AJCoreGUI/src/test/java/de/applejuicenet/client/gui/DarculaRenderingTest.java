package de.applejuicenet.client.gui;

import com.formdev.flatlaf.FlatDarculaLaf;
import org.junit.Test;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class DarculaRenderingTest {
    @Test
    public void paintsButtonsAndMenuWithoutInternalSwingAccess() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            LookAndFeel previous = UIManager.getLookAndFeel();
            try {
                UIManager.setLookAndFeel(new FlatDarculaLaf());
                JMenuBar menuBar = new JMenuBar();
                menuBar.add(new JMenu("Datei"));
                JComponent[] components = {
                        new JButton("Verbinden"), new JCheckBox("Aktiviert", true), menuBar
                };
                for (JComponent component : components) {
                    component.setSize(240, 40);
                    component.doLayout();
                    BufferedImage image = new BufferedImage(240, 40, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D graphics = image.createGraphics();
                    try {
                        component.paint(graphics);
                    } finally {
                        graphics.dispose();
                    }
                }
            } catch (Exception exception) {
                throw new AssertionError(exception);
            } finally {
                try {
                    UIManager.setLookAndFeel(previous);
                } catch (Exception exception) {
                    throw new AssertionError(exception);
                }
            }
        });
    }
}
