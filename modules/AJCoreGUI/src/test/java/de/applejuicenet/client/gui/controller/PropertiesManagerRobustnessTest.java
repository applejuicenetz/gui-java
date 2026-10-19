package de.applejuicenet.client.gui.controller;

import de.applejuicenet.client.facade.shared.RefreshIntervals;
import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.shared.ConnectionSettings;
import de.applejuicenet.client.shared.Settings;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.*;

/** A single invalid value must neither terminate the application nor reset the file. */
public class PropertiesManagerRobustnessTest {
    @Rule
    public TemporaryFolder files = new TemporaryFolder();

    @After
    public void resetFlag() {
        AppleJuiceDialog.rewriteProperties = false;
    }

    private PropertiesManager manager(File file) throws Exception {
        Field path = PropertiesManager.class.getDeclaredField("path");
        path.setAccessible(true);
        Constructor<PropertiesManager> constructor = PropertiesManager.class.getDeclaredConstructor(String.class);
        constructor.setAccessible(true);
        return constructor.newInstance(file.getPath());
    }

    private File write(String content) throws Exception {
        File file = files.newFile();
        Files.writeString(file.toPath(), content);
        return file;
    }

    @Test(timeout = 20000)
    public void additionalColorThemesAreSelectableAndRender() throws Exception {
        PropertiesManager manager = manager(write("options_sound=false\n"));
        var themes = java.util.Arrays.asList(manager.getLookAndFeels());
        for (String name : List.of("Nord", "Arc Dark", "Dracula", "Solarized Light", "Solarized Dark",
                "FlatLaf Light", "FlatLaf Dark", "FlatLaf IntelliJ", "FlatLaf macOS Light", "FlatLaf macOS Dark")) {
            var theme = themes.stream().filter(candidate -> name.equals(candidate.getName())).findFirst();
            assertTrue("Missing theme: " + name, theme.isPresent());
            javax.swing.SwingUtilities.invokeAndWait(() -> {
                javax.swing.LookAndFeel previous = javax.swing.UIManager.getLookAndFeel();
                try {
                    javax.swing.UIManager.setLookAndFeel(theme.get().getClassName());
                    javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(0, 1));
                    panel.add(new javax.swing.JButton("Download"));
                    panel.add(new javax.swing.JTextField("Filter"));
                    panel.add(new javax.swing.JScrollPane(new javax.swing.JTable(
                            new Object[][]{{"example.bin", "50 %"}}, new Object[]{"File", "Progress"})));
                    panel.add(new javax.swing.JScrollPane(new javax.swing.JTree()));
                    panel.setSize(500, 400);
                    panel.doLayout();
                    java.awt.Graphics2D graphics = new java.awt.image.BufferedImage(500, 400,
                            java.awt.image.BufferedImage.TYPE_INT_ARGB).createGraphics();
                    try {
                        panel.printAll(graphics);
                    } finally {
                        graphics.dispose();
                    }
                } catch (Exception failure) {
                    throw new AssertionError("Cannot render theme: " + name, failure);
                } finally {
                    try {
                        javax.swing.UIManager.setLookAndFeel(previous);
                    } catch (javax.swing.UnsupportedLookAndFeelException failure) {
                        throw new AssertionError(failure);
                    }
                }
            });
        }
    }

    @Test(timeout = 20000)
    public void invalidRemotePortFallsBackToDefaultWithoutExiting() throws Exception {
        File file = write("options_remote_host=example.org\noptions_remote_port=abc\noptions_sound=false\n");
        PropertiesManager manager = manager(file);
        ConnectionSettings settings = manager.getRemoteSettings();
        assertNotNull(settings);
        assertEquals("example.org", settings.getHost());
        assertEquals(9851, settings.getXmlPort());
        assertFalse(AppleJuiceDialog.rewriteProperties);
        assertFalse(manager.isSoundEnabled());
    }

    @Test(timeout = 20000)
    public void otherSettingsSurviveOneBadValue() throws Exception {
        File file = write("options_remote_port=broken\noptions_sprache=deutsch\noptions_sound=false\n"
                + "options_columns_download_column1_width=222\n");
        PropertiesManager manager = manager(file);
        manager.getRemoteSettings();
        assertEquals("deutsch", manager.getLanguage());
        assertFalse(manager.isSoundEnabled());
        String content = Files.readString(file.toPath());
        assertTrue(content, content.contains("options_columns_download_column1_width=222"));
        assertTrue(content, content.contains("options_sprache=deutsch"));
    }

    @Test(timeout = 20000)
    public void refreshIntervalsDefaultToTheBuiltInValues() throws Exception {
        PropertiesManager manager = manager(write("options_sound=true\n"));
        assertEquals(RefreshIntervals.CORE_POLL_DEFAULT_MS, manager.getCorePollIntervalMs());
        assertEquals(RefreshIntervals.PARTLIST_REFRESH_DEFAULT_MS, manager.getPartlistRefreshIntervalMs());
    }

    @Test(timeout = 20000)
    public void refreshIntervalsAreClampedToTheAllowedRange() throws Exception {
        PropertiesManager manager = manager(write("options_core_poll_ms=5\noptions_partlist_refresh_ms=99999999\n"));
        assertEquals(RefreshIntervals.CORE_POLL_MIN_MS, manager.getCorePollIntervalMs());
        assertEquals(RefreshIntervals.PARTLIST_REFRESH_MAX_MS, manager.getPartlistRefreshIntervalMs());
    }

    @Test(timeout = 20000)
    public void invalidRefreshIntervalFallsBackToDefault() throws Exception {
        PropertiesManager manager = manager(write("options_core_poll_ms=fast\noptions_partlist_refresh_ms=\n"));
        assertEquals(RefreshIntervals.CORE_POLL_DEFAULT_MS, manager.getCorePollIntervalMs());
        assertEquals(RefreshIntervals.PARTLIST_REFRESH_DEFAULT_MS, manager.getPartlistRefreshIntervalMs());
        assertFalse(AppleJuiceDialog.rewriteProperties);
    }

    @Test(timeout = 20000)
    public void savedRefreshIntervalsSurviveReload() throws Exception {
        File file = write("options_sound=true\n");
        PropertiesManager manager = manager(file);
        manager.setCorePollIntervalMs(2500);
        manager.setPartlistRefreshIntervalMs(4000);
        PropertiesManager reloaded = manager(file);
        assertEquals(2500, reloaded.getCorePollIntervalMs());
        assertEquals(4000, reloaded.getPartlistRefreshIntervalMs());
    }

    @Test(timeout = 20000)
    public void restoreKeepsABackupOfTheOldFile() throws Exception {
        File dir = files.newFolder("home");
        Path file = dir.toPath().resolve("ajgui.properties");
        Files.writeString(file, "options_sprache=deutsch\noptions_nick=wichtig\n");
        PropertiesManager.backupBeforeRestore(file);
        List<Path> backups;
        try (Stream<Path> list = Files.list(dir.toPath())) {
            backups = list.filter(p -> p.getFileName().toString().startsWith("ajgui.properties.bak")).toList();
        }
        assertEquals(1, backups.size());
        assertEquals("options_sprache=deutsch\noptions_nick=wichtig\n", Files.readString(backups.get(0)));
        assertTrue(Files.exists(file));
        PropertiesManager.backupBeforeRestore(dir.toPath().resolve("gibtsnicht.properties"));
    }
}
