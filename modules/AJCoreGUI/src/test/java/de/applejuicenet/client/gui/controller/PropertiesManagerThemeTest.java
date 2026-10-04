package de.applejuicenet.client.gui.controller;

import de.applejuicenet.client.shared.LookAFeel;
import de.applejuicenet.client.shared.LookAndFeelLoader;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;

public class PropertiesManagerThemeTest {
    @Rule
    public TemporaryFolder files = new TemporaryFolder();

    @Test
    public void removedSkinFallsBackToDefaultAndRepairsPreference() throws Exception {
        check("SkinLF", LookAndFeelLoader.DEFAULT.getName());
    }

    @Test
    public void knownThemeRemainsSelected() throws Exception {
        check("Darcula", "Darcula");
    }

    private void check(String savedName, String expectedName) throws Exception {
        File file = files.newFile("ajgui.properties");
        Files.writeString(file.toPath(), "options_lookandfeels_default_name=" + savedName + "\n");
        Field path = PropertiesManager.class.getDeclaredField("path");
        path.setAccessible(true);
        Object previousPath = path.get(null);
        try {
            Constructor<PropertiesManager> constructor = PropertiesManager.class.getDeclaredConstructor(String.class);
            constructor.setAccessible(true);
            PropertiesManager manager = constructor.newInstance(file.getPath());
            LookAFeel selected = manager.getDefaultLookAndFeel();
            assertEquals(expectedName, selected.getName());
            Field handler = PropertiesManager.class.getDeclaredField("propertyHandler");
            handler.setAccessible(true);
            assertEquals(expectedName, ((PropertyHandler) handler.get(manager))
                    .get("options_lookandfeels_default_name", ""));
        } finally {
            path.set(null, previousPath);
        }
    }
}
