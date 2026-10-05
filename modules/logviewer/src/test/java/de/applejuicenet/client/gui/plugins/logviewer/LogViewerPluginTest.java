package de.applejuicenet.client.gui.plugins.logviewer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javax.swing.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class LogViewerPluginTest {
    @Rule public TemporaryFolder folder = new TemporaryFolder();

    private static final String LOG = "<html><body><p>Log session start time X</p><table>"
            + "<tr class=\"error even\"><td class=\"RelativeTime\">1</td><td class=\"Thread\">t</td><td class=\"Level\">ERROR</td>"
            + "<td class=\"Logger\">a.B</td><td class=\"LineOfCaller\">1</td><td class=\"Message\">fehler</td></tr></table></body></html>";

    private Path logs(int count) throws Exception {
        Path directory = folder.newFolder().toPath();
        for (int i = 0; i < count; i++) {
            Path file = Files.writeString(directory.resolve(String.format("2026-%02d.html", i)), LOG);
            Files.setLastModifiedTime(file, FileTime.fromMillis(1_000_000L + i * 1000L));
        }
        return directory;
    }

    private LogViewerPlugin open(Path directory, java.util.function.Predicate<File> active) throws Exception {
        LogViewerPlugin[] plugin = new LogViewerPlugin[1];
        SwingUtilities.invokeAndWait(() -> plugin[0] = new LogViewerPlugin(new Properties(), Map.of(), new ImageIcon(), Map.of(), directory, active));
        return plugin[0];
    }

    private static long htmlFiles(Path directory) throws Exception {
        try (var s = Files.list(directory)) { return s.filter(p -> p.toString().endsWith(".html")).count(); }
    }

    @Test public void openingDeletesEverythingBeyondTwentyNewest() throws Exception {
        Path directory = logs(25);
        LogViewerPlugin plugin = open(directory, f -> false);
        assertEquals(20, htmlFiles(directory));
        assertEquals(20, plugin.fileList().getModel().getSize());
        assertFalse(Files.exists(directory.resolve("2026-00.html")));
        assertTrue(Files.exists(directory.resolve("2026-24.html")));
    }

    @Test public void runningLogSurvivesEvenWhenOldest() throws Exception {
        Path directory = logs(25);
        open(directory, f -> f.getName().equals("2026-00.html"));
        assertTrue(Files.exists(directory.resolve("2026-00.html")));
        assertEquals(20, htmlFiles(directory));
    }

    @Test public void upToTwentyFilesAreLeftAlone() throws Exception {
        Path directory = logs(20);
        open(directory, f -> false);
        assertEquals(20, htmlFiles(directory));
    }

    @Test public void showsParsedEntriesOfSelectedLogAndHandlesMissingFolder() throws Exception {
        LogViewerPlugin plugin = open(logs(1), f -> false);
        long deadline = System.currentTimeMillis() + 5000;
        while (plugin.entryTable().visibleCount() == 0 && System.currentTimeMillis() < deadline) TimeUnit.MILLISECONDS.sleep(50);
        assertEquals(1, plugin.entryTable().visibleCount());
        LogViewerPlugin empty = open(folder.getRoot().toPath().resolve("missing"), f -> false);
        assertEquals(0, empty.fileList().getModel().getSize());
        assertEquals(0, empty.entryTable().visibleCount());
    }

    @Test public void changesOpenPluginControlsToEnglishAndBack() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            var selector = de.applejuicenet.client.gui.controller.LanguageSelector.getInstance(
                    java.nio.file.Paths.get("../../resources/language/deutsch.properties").toAbsolutePath().normalize().toString());
            var plugin = new LogViewerPlugin(new Properties(), Map.of(), new ImageIcon(), Map.of(), folder.getRoot().toPath(), f -> false);
            de.applejuicenet.client.gui.controller.LanguageSelector.getInstance(
                    java.nio.file.Paths.get("../../resources/language/english.properties").toAbsolutePath().normalize().toString());
            plugin.fireLanguageChanged();
            assertTrue(containsText(plugin, "Refresh"));
            de.applejuicenet.client.gui.controller.LanguageSelector.getInstance(
                    java.nio.file.Paths.get("../../resources/language/deutsch.properties").toAbsolutePath().normalize().toString());
            assertFalse(containsText(plugin, "Refresh"));
        });
    }
    private static boolean containsText(java.awt.Container parent, String text) {
        for (java.awt.Component c : parent.getComponents()) {
            if (c instanceof javax.swing.AbstractButton b && text.equals(b.getText())) return true;
            if (c instanceof java.awt.Container child && containsText(child, text)) return true;
        }
        return false;
    }
}
