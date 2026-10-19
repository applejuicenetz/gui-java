package de.applejuicenet.client.gui.options;

import com.formdev.flatlaf.FlatLightLaf;
import de.applejuicenet.client.facade.shared.AJSettings;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.shared.ConnectionSettings;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

public class OptionsFormLayoutTest {
    @Test
    public void coreFormsUseAlignedRowsAndProxyFieldHeight() throws Exception {
        String previousHome = System.getProperty("user.home");
        String previousDirectory = System.getProperty("user.dir");
        System.setProperty("user.dir", Path.of("../../resources").toAbsolutePath().normalize().toString());
        Path home = Files.createTempDirectory("options-form-test-");
        System.setProperty("user.home", home.toString());
        try {
            SwingUtilities.invokeAndWait(() -> {
                LookAndFeel previous = UIManager.getLookAndFeel();
                try {
                    UIManager.setLookAndFeel(new FlatLightLaf());
                    LanguageSelector.getInstance(Path.of("../../resources/language/deutsch.properties")
                            .toAbsolutePath().normalize().toString());
                    ODViewPanel general = new ODViewPanel();
                    general.setSize(650, 650);
                    layout(general);
                    Container groups = (Container) general.getComponent(0);
                    assertEquals(3, groups.getComponentCount());
                    Dimension groupSize = groups.getComponent(0).getSize();
                    for (Component group : groups.getComponents()) {
                        assertEquals(groupSize.width, group.getWidth());
                        assertEquals(group.getPreferredSize().height, group.getHeight());
                    }
                    assertTrue(groups.getComponent(0).getHeight() > groups.getComponent(2).getHeight());
                    JComboBox<?> logging = (JComboBox<?>) component(general, "cmbLog");
                    assertEquals("Logging", logging.getItemAt(0).toString());
                    Object selectedLevel = logging.getSelectedItem();
                    logging.setSelectedIndex(0);
                    assertSame(selectedLevel, logging.getSelectedItem());
                    Container logRow = logging.getParent();
                    assertEquals(2, logRow.getComponentCount());
                    Component closeToTray = component(general, "cmbCloseToTray");
                    assertSame(logRow, closeToTray.getParent());
                    assertEquals(closeToTray.getY(), logging.getY());
                    assertEquals(closeToTray.getHeight(), logging.getHeight());
                    assertEquals(logRow.getWidth(), logging.getX() + logging.getWidth());
                    ODProxyPanel proxy = new ODProxyPanel();
                    assertTrue("Proxy explanation missing", containsText(proxy, "XML-API"));
                    JTabbedPane tabs = OptionsDialog.createTabs(new OptionsRegister[] {general, proxy});
                    assertEquals(JTabbedPane.TOP, tabs.getTabPlacement());
                    assertEquals(2, tabs.getTabCount());
                    assertEquals(general.getMenuText(), tabs.getTitleAt(0));
                    assertSame(general.getIcon(), tabs.getIconAt(0));
                    assertNotNull(tabs.getIconAt(0));
                    assertNull(tabs.getToolTipTextAt(0));
                    assertNull(tabs.getToolTipTextAt(1));
                    // Tab construction reparents the panels; use standalone layout below.
                    general.getParent().remove(general);
                    proxy.getParent().remove(proxy);
                    assertEquals(new Dimension(700, 630), OptionsDialog.compactSize(new Dimension(1000, 700)));
                    ODStandardPanel standard = new ODStandardPanel(null, new AJSettings("test", 9850, 9851, 102400, 0, 3, "", "", java.util.Set.of(), 50, false, 5, 500), new ConnectionSettings()) {
                        @Override public void reloadSettings() { }
                    };
                    ODCoreConnectionPanel connection = new ODCoreConnectionPanel(null, new AJSettings("test", 9850, 9851, 102400, 0, 3, "", "", java.util.Set.of(), 50, false, 5, 500)) {
                        @Override public void reloadSettings() { }
                    };
                    for (JPanel panel : new JPanel[] {proxy, standard, connection}) {
                        panel.setSize(650, 420);
                        layout(panel);
                    }
                    int height = component(proxy, "host").getHeight();
                    assertRows(standard, height, "temp", "incoming", "port", "xmlPort", "nick");
                    assertRows(connection, height, "maxConnections", "maxUpload", "maxDownload",
                            "maxConnectionsPerTurn", "maxSourcesPerFile");
                    for (String[] row : new String[][] {{"label1", "hint1", "temp"},
                            {"label3", "hint2", "port"}, {"label6", "hint6", "xmlPort"},
                            {"label4", "hint3", "nick"}}) {
                        Rectangle label = bounds(standard, row[0]);
                        Rectangle hint = bounds(standard, row[1]);
                        Rectangle input = bounds(standard, row[2]);
                        assertTrue("Hint must follow its label", hint.x >= label.x + label.width);
                        assertTrue("Hint must precede input", hint.x + hint.width <= input.x);
                        assertEquals("Label must have input height", height, label.height);
                    }
                    String image = System.getProperty("options.layout.image");
                    if (image != null) {
                        JPanel preview = new JPanel(new GridLayout(1, 3, 12, 0));
                        preview.add(standard);
                        preview.add(connection);
                        preview.add(proxy);
                        preview.setSize(1650, 420);
                        layout(preview);
                        BufferedImage rendered = new BufferedImage(1650, 420, BufferedImage.TYPE_INT_RGB);
                        Graphics2D graphics = rendered.createGraphics();
                        preview.printAll(graphics);
                        graphics.dispose();
                        javax.imageio.ImageIO.write(rendered, "png", Path.of(image).toFile());
                        JTabbedPane fullTabs = OptionsDialog.createTabs(new OptionsRegister[] {standard,
                                connection, general, proxy});
                        fullTabs.setSize(640, 690);
                        for (int index : new int[] {2, 3}) {
                            fullTabs.setSelectedIndex(index);
                            layout(fullTabs);
                            BufferedImage tabImage = new BufferedImage(640, 690, BufferedImage.TYPE_INT_RGB);
                            Graphics2D tabGraphics = tabImage.createGraphics();
                            fullTabs.printAll(tabGraphics);
                            tabGraphics.dispose();
                            javax.imageio.ImageIO.write(tabImage, "png", Path.of(image + index + ".png").toFile());
                        }
                    }
                } catch (Exception error) {
                    throw new AssertionError(error);
                } finally {
                    try { UIManager.setLookAndFeel(previous); }
                    catch (Exception error) { throw new AssertionError(error); }
                }
            });
        } finally {
            System.setProperty("user.home", previousHome);
            System.setProperty("user.dir", previousDirectory);
        }
    }

    private static void assertRows(JPanel panel, int height, String... fields) throws Exception {
        int x = bounds(panel, fields[0]).x;
        int width = component(panel, fields[0]).getWidth();
        for (String field : fields) {
            Component input = component(panel, field);
            assertEquals(field + " left edge", x, bounds(panel, field).x);
            assertEquals(field + " width", width, input.getWidth());
            assertEquals(field + " height", height, input.getHeight());
        }
    }

    private static Component component(Object panel, String name) throws Exception {
        Class<?> type = panel.getClass();
        while (type != null) {
            try {
                Field field = type.getDeclaredField(name);
                field.setAccessible(true);
                return (Component) field.get(panel);
            } catch (NoSuchFieldException ignored) {
                type = type.getSuperclass();
            }
        }
        throw new NoSuchFieldException(name);
    }

    private static Rectangle bounds(JPanel panel, String field) throws Exception {
        Component component = component(panel, field);
        return SwingUtilities.convertRectangle(component.getParent(), component.getBounds(), panel);
    }

    private static boolean containsText(Container container, String text) {
        for (Component child : container.getComponents()) {
            if (child instanceof JLabel label && label.getText() != null && label.getText().contains(text)) return true;
            if (child instanceof Container nested && containsText(nested, text)) return true;
        }
        return false;
    }

    private static void layout(Container container) {
        container.doLayout();
        for (Component child : container.getComponents()) {
            if (child instanceof Container nested) layout(nested);
        }
    }
}
