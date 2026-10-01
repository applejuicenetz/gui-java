package de.applejuicenet.client.gui.options;

import de.applejuicenet.client.fassade.shared.AJSettings;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.shared.ConnectionSettings;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.Collections;

import static org.junit.Assert.*;

public class PortSettingsTest {
    @BeforeClass
    public static void loadLanguage() {
        LanguageSelector.getInstance(Paths.get("../../resources/language/deutsch.properties").toAbsolutePath().normalize().toString());
    }

    private JTextField field(ODStandardPanel panel, String name) throws Exception {
        Field field = ODStandardPanel.class.getDeclaredField(name);
        field.setAccessible(true);
        return (JTextField) field.get(panel);
    }

    private void enterPort(JTextField field, String text) {
        field.setText(text);
        FocusEvent event = new FocusEvent(field, FocusEvent.FOCUS_LOST);
        for (FocusListener listener : field.getFocusListeners()) {
            listener.focusLost(event);
        }
    }

    @Test
    public void acceptsUpperPortRangeAndRestoresInvalidValues() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            try {
                AJSettings settings = new AJSettings("test", 9852, 9851, 0, 0, 0, "", "", Collections.emptySet(), 0, false, 0, 0);
                ConnectionSettings remote = new ConnectionSettings("localhost", "", 9851);
                ODStandardPanel panel = new ODStandardPanel(null, settings, remote) {
                    @Override public void reloadSettings() {
                        // This test exercises focus validation without a running core.
                    }
                };
                JTextField port = field(panel, "port");
                JTextField xmlPort = field(panel, "xmlPort");
                for (int value : new int[]{32001, 49152, 65535}) {
                    enterPort(port, Integer.toString(value));
                    enterPort(xmlPort, Integer.toString(value));
                    assertEquals(value, settings.getPort());
                    assertEquals(value, settings.getXMLPort());
                    assertEquals(value, remote.getXmlPort());
                }
                assertTrue(panel.isXmlPortDirty());
                for (String invalid : new String[]{"65536", "1024", "", "21474836472147483647"}) {
                    enterPort(port, invalid);
                    enterPort(xmlPort, invalid);
                    assertEquals(65535, settings.getPort());
                    assertEquals(65535, settings.getXMLPort());
                    assertEquals(65535, remote.getXmlPort());
                    assertEquals("65535", port.getText());
                    assertEquals("65535", xmlPort.getText());
                }
            } catch (Exception e) {
                throw new AssertionError(e);
            }
        });
    }
}
