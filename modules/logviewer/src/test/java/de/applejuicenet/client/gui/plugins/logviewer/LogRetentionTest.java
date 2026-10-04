package de.applejuicenet.client.gui.plugins.logviewer;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import static org.junit.Assert.*;

public class LogRetentionTest {
    @Rule public TemporaryFolder folder = new TemporaryFolder();
    @Test public void keepsTwentyNewestIncludingProtectedActiveFile() throws Exception {
        Path directory = folder.getRoot().toPath();
        for (int i = 0; i < 25; i++) {
            Path file = Files.writeString(directory.resolve(i + ".html"), "log");
            Files.setLastModifiedTime(file, FileTime.fromMillis(1000 + i));
        }
        Files.writeString(directory.resolve("other.txt"), "keep");
        Files.createDirectory(directory.resolve("directory.html"));
        var result = LogRetention.clean(directory, 20, file -> file.getName().equals("0.html"));
        assertEquals(5, result.deleted());
        assertEquals(20, result.files().size());
        assertTrue(Files.exists(directory.resolve("0.html")));
        assertFalse(Files.exists(directory.resolve("1.html")));
        assertTrue(Files.exists(directory.resolve("other.txt")));
        assertTrue(Files.isDirectory(directory.resolve("directory.html")));
    }
    @Test public void ignoresSymlinksAndMissingDirectories() throws Exception {
        Path directory = folder.newFolder().toPath();
        Path outside = Files.writeString(folder.getRoot().toPath().resolve("outside.html"), "keep");
        Files.createSymbolicLink(directory.resolve("link.html"), outside);
        assertTrue(LogRetention.clean(directory, 20, file -> false).files().isEmpty());
        assertTrue(Files.exists(outside));
        assertTrue(LogRetention.clean(directory.resolve("missing"), 20, file -> false).files().isEmpty());
    }
}
