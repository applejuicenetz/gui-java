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
    public void splitsLinksBySpaceNewlineAndConcatenation() {
        String a = "ajfsp://file|a.bin|0123456789abcdef0123456789abcdef|1/";
        String b = "web+ajfsp://file|b.bin|0123456789abcdef0123456789abcdef|2/";
        String c = "ajfsp://server|host.de|9854/";
        assertEquals(java.util.List.of(a, b, c), DownloadlinkPanel.splitLinks(a + " \n" + b + "\r\n\t" + c + "\n"));
        assertEquals(java.util.List.of(a, b), DownloadlinkPanel.splitLinks(a + b));
        assertEquals(java.util.List.of(a), DownloadlinkPanel.splitLinks("  " + a + "  "));
        assertTrue(DownloadlinkPanel.splitLinks(" \n ").isEmpty());
    }

    @Test
    public void multipleLinksValidOnlyIfAllValid() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            DownloadlinkPanel panel = new DownloadlinkPanel();
            String ok = "ajfsp://file|a.bin|0123456789abcdef0123456789abcdef|1/";
            panel.getTxtDownloadLink().setText(ok + "\n" + ok + " " + ok);
            assertEquals(false, panel.isLinkInvalid());
            panel.getTxtDownloadLink().setText(ok + "\najfsp://file|broken");
            assertEquals(true, panel.isLinkInvalid());
        });
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
