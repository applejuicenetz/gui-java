package de.applejuicenet.client.gui;

import de.applejuicenet.client.gui.controller.LanguageSelector;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DownloadlinkPanelTest {
    @BeforeClass
    public static void loadLanguage() {
        LanguageSelector.getInstance(Paths.get("../../resources/language/deutsch.properties").toAbsolutePath().normalize().toString());
    }

    @Test
    public void linkFieldFillsRemainingWidthBetweenLabelAndButton() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            DownloadlinkPanel panel = new DownloadlinkPanel();
            JFrame frame = new JFrame();
            try {
                frame.add(panel);
                frame.setSize(900, 120);
                frame.setVisible(true);
                panel.doLayout();
                int label = panel.getComponent(0).getWidth();
                int field = panel.getTxtDownloadLink().getWidth();
                int button = panel.getBtnStartDownload().getWidth();
                assertTrue(field > label + button);
                assertEquals(5, panel.getComponent(0).getX());
                assertEquals(900, panel.getBtnStartDownload().getX() + button + 5, frame.getInsets().left + frame.getInsets().right);
                assertEquals(panel.getComponent(0).getHeight(), panel.getTxtDownloadLink().getHeight());
                assertEquals(panel.getComponent(0).getHeight(), panel.getBtnStartDownload().getHeight());
            } finally {
                frame.dispose();
            }
        });
    }
}
