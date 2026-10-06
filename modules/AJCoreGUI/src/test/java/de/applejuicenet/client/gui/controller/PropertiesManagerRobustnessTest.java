package de.applejuicenet.client.gui.controller;

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

/** Ein einzelner ungueltiger Wert darf weder die Anwendung beenden noch die Datei zuruecksetzen. */
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
    public void invalidColorFallsBackToDefaultWithoutExiting() throws Exception {
        File file = write("options_farben_hintergrund_quelle=nichtsZahl\noptions_farben_aktiv=false\n");
        PropertiesManager manager = manager(file);
        Settings settings = manager.getSettings();
        assertNotNull(settings);
        assertFalse(settings.isFarbenAktiv());
        assertEquals(new java.awt.Color(-205), settings.getQuelleHintergrundColor());
        assertFalse(AppleJuiceDialog.rewriteProperties);
    }

    @Test(timeout = 20000)
    public void otherSettingsSurviveOneBadValue() throws Exception {
        File file = write("options_remote_port=kaputt\noptions_sprache=deutsch\noptions_sound=false\n"
                + "options_columns_download_column1_width=222\n");
        PropertiesManager manager = manager(file);
        manager.getRemoteSettings();
        assertEquals("deutsch", manager.getSprache());
        assertFalse(manager.isSoundEnabled());
        String content = Files.readString(file.toPath());
        assertTrue(content, content.contains("options_columns_download_column1_width=222"));
        assertTrue(content, content.contains("options_sprache=deutsch"));
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
