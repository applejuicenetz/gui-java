package de.applejuicenet.client;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;

public class ApplicationPathsTest {
    @Rule public TemporaryFolder files = new TemporaryFolder();

    @Test
    public void osLaunchFromDifferentDirectoryFindsBundledResources() throws Exception {
        Path installed = files.newFolder("GUI mit Leerzeichen und Grüße").toPath();
        Path jar = Files.createFile(installed.resolve("AJCoreGUI.jar"));
        Files.createFile(installed.resolve("presets.json"));
        Path cwd = files.newFolder("Browser").toPath();
        assertEquals(installed, ApplicationPaths.resourceDirectory(jar, cwd,
                new String[]{"ajfsp://file|Grüße.bin|hash|5368709120/"}));
    }

    @Test
    public void explicitPathOverridesBundleAndResolvesAgainstLaunchDirectory() throws Exception {
        Path cwd = files.newFolder("cwd").toPath();
        assertEquals(cwd.resolve("custom"), ApplicationPaths.resourceDirectory(
                cwd.resolve("AJCoreGUI.jar"), cwd, new String[]{"-path=custom"}));
    }

    @Test
    public void ideStartKeepsWorkingDirectory() throws Exception {
        Path classes = files.newFolder("classes").toPath();
        Path cwd = files.newFolder("resources").toPath();
        assertEquals(cwd, ApplicationPaths.resourceDirectory(classes, cwd, null));
    }
}
