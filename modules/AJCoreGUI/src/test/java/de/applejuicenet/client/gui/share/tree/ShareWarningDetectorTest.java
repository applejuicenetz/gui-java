package de.applejuicenet.client.gui.share.tree;

import de.applejuicenet.client.facade.entity.ShareEntry;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ShareWarningDetectorTest {
    private static final String LINUX_HOME = "/home/user";
    private static final String WINDOWS_HOME = "C:\\Users\\User";

    @Test
    public void filesystemRootIsAWarning() {
        assertEquals(ShareWarningDetector.Reason.FILESYSTEM_ROOT, only("/", LINUX_HOME));
    }

    @Test
    public void driveRootsAreWarningsInEverySpelling() {
        for (String dir : List.of("C:", "C:\\", "c:/", "D:\\")) {
            assertEquals("dir " + dir, ShareWarningDetector.Reason.DRIVE_ROOT, only(dir, WINDOWS_HOME));
        }
    }

    @Test
    public void homeDirectoryIsAWarning() {
        assertEquals(ShareWarningDetector.Reason.HOME_DIRECTORY, only("/home/user", LINUX_HOME));
        assertEquals(ShareWarningDetector.Reason.HOME_DIRECTORY, only("/home/user/", LINUX_HOME));
        assertEquals(ShareWarningDetector.Reason.HOME_DIRECTORY, only("c:\\users\\user\\", WINDOWS_HOME));
    }

    @Test
    public void subFoldersAndOtherSharesAreFine() {
        for (String dir : List.of("/home/user/files", "/home", "/srv/share", "C:\\Users\\User\\Downloads", "C:\\Users")) {
            assertTrue("dir " + dir + " must not warn", warnings(dir, dir.startsWith("/") ? LINUX_HOME : WINDOWS_HOME).isEmpty());
        }
    }

    @Test
    public void entriesThatAreNotSharedAreIgnored() {
        assertTrue(ShareWarningDetector.findWarnings(
                List.of(entry("/", ShareEntry.SHAREMODE.NOT_SHARED)), LINUX_HOME).isEmpty());
    }

    @Test
    public void missingValuesDoNotWarn() {
        assertTrue(ShareWarningDetector.findWarnings(null, LINUX_HOME).isEmpty());
        assertTrue(ShareWarningDetector.findWarnings(Set.of(entry(null, ShareEntry.SHAREMODE.SUBDIRECTORY)), LINUX_HOME).isEmpty());
        assertTrue(ShareWarningDetector.findWarnings(
                List.of(entry("/", ShareEntry.SHAREMODE.SUBDIRECTORY)), null).stream()
                .noneMatch(warning -> warning.reason() == ShareWarningDetector.Reason.HOME_DIRECTORY));
    }

    @Test
    public void warningKeepsTheOriginalDirectory() {
        List<ShareWarningDetector.Warning> warnings = ShareWarningDetector.findWarnings(
                List.of(entry("C:\\", ShareEntry.SHAREMODE.SUBDIRECTORY)), WINDOWS_HOME);

        assertEquals("C:\\", warnings.get(0).directory());
    }

    private static ShareWarningDetector.Reason only(String dir, String home) {
        List<ShareWarningDetector.Warning> warnings = warnings(dir, home);

        assertEquals(1, warnings.size());
        return warnings.get(0).reason();
    }

    private static List<ShareWarningDetector.Warning> warnings(String dir, String home) {
        return ShareWarningDetector.findWarnings(List.of(entry(dir, ShareEntry.SHAREMODE.SUBDIRECTORY)), home);
    }

    private static ShareEntry entry(String dir, ShareEntry.SHAREMODE mode) {
        return new ShareEntry() {
            public String getDir() {
                return dir;
            }

            public SHAREMODE getShareMode() {
                return mode;
            }
        };
    }
}
