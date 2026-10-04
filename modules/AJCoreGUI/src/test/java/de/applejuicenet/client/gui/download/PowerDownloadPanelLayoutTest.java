package de.applejuicenet.client.gui.download;

import com.formdev.flatlaf.FlatDarculaLaf;
import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.listener.DataUpdateListener.DATALISTENER_TYPE;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class PowerDownloadPanelLayoutTest {
    @BeforeClass
    public static void loadLanguage() {
        LanguageSelector.getInstance(Paths.get("../../resources/language/deutsch.properties")
                .toAbsolutePath().normalize().toString());
    }

    @Test
    public void shortPanelScrollsWithoutClippingTextOrOverlappingNeighbor() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            LookAndFeel previous = UIManager.getLookAndFeel();
            try {
                UIManager.setLookAndFeel(new FlatDarculaLaf());
                for (float fontSize : new float[] {13f, 22f}) {
                    PowerDownloadPanel panel = new PowerDownloadPanel(null);
                    try {
                        setFont(panel, panel.getFont().deriveFont(fontSize));
                        panel.fireLanguageChanged();
                        addHintAndArrowIcons(panel);
                        JScrollPane scrollPane = (JScrollPane) panel.getComponent(0);
                        Component content = scrollPane.getViewport().getView();
                        Insets border = scrollPane.getInsets();
                        int requiredWidth = content.getPreferredSize().width + border.left + border.right
                                + scrollPane.getVerticalScrollBar().getPreferredSize().width;
                        assertTrue(panel.getPreferredSize().width >= requiredWidth);
                        assertEquals(panel.getPreferredSize().width, panel.getMinimumSize().width);
                        JPanel bottomPanel = new JPanel(new BorderLayout());
                        JPanel overview = new JPanel();
                        bottomPanel.add(panel, BorderLayout.WEST);
                        bottomPanel.add(overview, BorderLayout.CENTER);
                        bottomPanel.setSize(panel.getPreferredSize().width + 200, 180);
                        layout(bottomPanel);
                        assertEquals(panel.getX() + panel.getWidth(), overview.getX());
                        assertTrue(scrollPane.getY() + scrollPane.getHeight() <= panel.getHeight());
                        assertTrue(scrollPane.getVerticalScrollBar().isVisible());
                        assertTrue(scrollPane.getViewport().getExtentSize().height < content.getPreferredSize().height);
                        assertTrue(scrollPane.getViewport().getExtentSize().width >= content.getPreferredSize().width);
                        assertLabelsFit((Container) content);
                        scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
                        assertTrue(scrollPane.getViewport().getViewRect().y
                                + scrollPane.getViewport().getViewRect().height >= content.getHeight());
                        bottomPanel.setSize(bottomPanel.getWidth(), content.getPreferredSize().height + 100);
                        layout(bottomPanel);
                        assertFalse(scrollPane.getVerticalScrollBar().isVisible());
                        assertEquals(0, ((Container) content).getComponent(0).getY());
                    } finally {
                        removeListeners(panel);
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
    public void inputFieldHeightsFollowFontInsteadOfFixedPixels() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            PowerDownloadPanel panel = new PowerDownloadPanel(null);
            try {
                setFont(panel, panel.getFont().deriveFont(24f));
                assertFieldHeights(panel);
            } finally {
                removeListeners(panel);
            }
        });
    }

    private static void removeListeners(PowerDownloadPanel panel) {
        LanguageSelector.getInstance().removeLanguageListener(panel);
        AppleJuiceClient.getAjFassade().removeDataUpdateListener(panel, DATALISTENER_TYPE.INFORMATION_CHANGED);
        AppleJuiceClient.getAjFassade().removeDataUpdateListener(panel, DATALISTENER_TYPE.DOWNLOAD_CHANGED);
    }

    private static void addHintAndArrowIcons(PowerDownloadPanel panel) throws Exception {
        ImageIcon icon = new ImageIcon(new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB));
        for (String name : new String[] {"btnHint", "btnHint2", "btnHint3", "btnPdlUp", "btnPdlDown"}) {
            Field field = PowerDownloadPanel.class.getDeclaredField(name);
            field.setAccessible(true);
            ((JLabel) field.get(panel)).setIcon(icon);
        }
    }

    private static void setFont(Component component, Font font) {
        component.setFont(font);
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                setFont(child, font);
            }
        }
    }

    private static void layout(Container container) {
        container.doLayout();
        for (Component child : container.getComponents()) {
            if (child instanceof Container childContainer) {
                layout(childContainer);
            }
        }
    }

    private static void assertLabelsFit(Container container) {
        for (Component child : container.getComponents()) {
            if (child instanceof JLabel label && label.isVisible()) {
                assertTrue(label.getText(), label.getWidth() >= label.getPreferredSize().width);
                assertTrue(label.getX() + label.getWidth() <= container.getWidth());
            }
            if (child instanceof Container childContainer) {
                assertLabelsFit(childContainer);
            }
        }
    }

    private static void assertFieldHeights(Container container) {
        for (Component child : container.getComponents()) {
            if (child instanceof JTextField field) {
                Insets insets = field.getInsets();
                Dimension preferred = field.getPreferredSize();
                assertTrue(preferred.height >= field.getFontMetrics(field.getFont()).getHeight()
                        + insets.top + insets.bottom);
            }
            if (child instanceof Container childContainer) {
                assertFieldHeights(childContainer);
            }
        }
    }
}
