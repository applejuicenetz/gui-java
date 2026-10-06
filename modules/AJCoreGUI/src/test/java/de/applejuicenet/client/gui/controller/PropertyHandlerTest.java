package de.applejuicenet.client.gui.controller;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class PropertyHandlerTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void savesAndReloadsUmlautsAndKeys() throws Exception {
        Path file = folder.getRoot().toPath().resolve("ajgui.properties");
        PropertyHandler handler = new PropertyHandler(file.toString(), "test", false);
        handler.put("options_nick", "Jürgen äöü ß");
        handler.put("options_old_key", "1");
        handler.save();

        PropertyHandler reloaded = new PropertyHandler(file.toString(), "test", true);
        assertEquals("Jürgen äöü ß", reloaded.get("options_nick", ""));
        assertEquals("1", reloaded.get("options_old_key", ""));
    }

    @Test
    public void saveLeavesNoTempFileAndReplacesContent() throws Exception {
        Path file = folder.getRoot().toPath().resolve("ajgui.properties");
        PropertyHandler handler = new PropertyHandler(file.toString(), "test", false);
        handler.put("a", "1");
        handler.save();
        handler.put("a", "2");
        handler.save();

        try (Stream<Path> files = Files.list(folder.getRoot().toPath())) {
            assertEquals(1, files.count());
        }
        assertEquals("2", new PropertyHandler(file.toString(), "test", true).get("a", ""));
    }

    @Test
    public void failedSaveKeepsLastValidFileAndReportsError() throws Exception {
        File directory = folder.newFolder("settings");
        Path file = directory.toPath().resolve("ajgui.properties");
        PropertyHandler handler = new PropertyHandler(file.toString(), "test", false);
        handler.put("a", "valid");
        handler.save();
        String before = Files.readString(file, StandardCharsets.ISO_8859_1);

        assertTrue(directory.setWritable(false));
        try {
            org.junit.Assume.assumeFalse("root ignoriert Schreibschutz", Files.isWritable(directory.toPath()));
            handler.put("a", "new");
            assertThrows(IllegalArgumentException.class, handler::save);
            assertEquals(before, Files.readString(file, StandardCharsets.ISO_8859_1));
        } finally {
            directory.setWritable(true);
        }
    }

    @Test
    public void savePreservesFilePermissions() throws Exception {
        Path file = folder.getRoot().toPath().resolve("ajgui.properties");
        Files.writeString(file, "a=1\n");
        try {
            Files.setPosixFilePermissions(file, Set.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE));
        } catch (UnsupportedOperationException e) {
            return;
        }
        PropertyHandler handler = new PropertyHandler(file.toString(), "test", true);
        handler.put("a", "2");
        handler.save();
        assertEquals(Set.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE),
                Files.getPosixFilePermissions(file));
    }

    @Test
    public void missingFileLeavesEmptyPropertiesWithDefaults() {
        Path file = folder.getRoot().toPath().resolve("missing.properties");
        PropertyHandler handler = new PropertyHandler(file.toString(), "test", true);
        assertEquals("default", handler.get("x", "default"));
        assertEquals(7, (int) handler.getAsInt("x", 7));
    }
}
