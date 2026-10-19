package de.applejuicenet.client.gui.share.tree;

import de.applejuicenet.client.facade.entity.Directory;
import de.applejuicenet.client.facade.entity.ShareEntry;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DirectoryNodeWarningTest {
    @After
    public void clearShareDirs() {
        DirectoryNode.setShareDirs(Set.of());
    }

    @Test
    public void riskyShareEntriesMarkMatchingNodes() {
        DirectoryNode.setShareDirs(Set.of(entry("/", ShareEntry.SHAREMODE.SUBDIRECTORY)));

        assertTrue(DirectoryNode.hasWarningShares());
        assertTrue(new DirectoryNode(null, directory("/")).isWarningShare());
        assertFalse(new DirectoryNode(null, directory("/home")).isWarningShare());
        assertFalse(new DirectoryNode(null, null).isWarningShare());
    }

    @Test
    public void normalSharesDoNotTriggerWarning() {
        DirectoryNode.setShareDirs(Set.of(entry("/home/user/files", ShareEntry.SHAREMODE.SUBDIRECTORY)));

        assertFalse(DirectoryNode.hasWarningShares());
        assertFalse(new DirectoryNode(null, directory("/home/user/files")).isWarningShare());
    }

    @Test
    public void homeDirectoryShareMarksTheHomeNode() {
        String home = System.getProperty("user.home");

        DirectoryNode.setShareDirs(Set.of(entry(home, ShareEntry.SHAREMODE.SINGLEDIRECTORY)));

        assertTrue(DirectoryNode.hasWarningShares());
        assertTrue(new DirectoryNode(null, directory(home + File.separator)).isWarningShare());
    }

    @Test
    public void notSharedEntriesDoNotMarkNodes() {
        DirectoryNode.setShareDirs(Set.of(entry("/", ShareEntry.SHAREMODE.NOT_SHARED)));

        assertFalse(DirectoryNode.hasWarningShares());
        assertFalse(new DirectoryNode(null, directory("/")).isWarningShare());
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

    private static Directory directory(String path) {
        return new Directory() {
            public String getName() {
                return path;
            }

            public int getType() {
                return Directory.TYPE_FOLDER;
            }

            public boolean isFileSystem() {
                return true;
            }

            public String getPath() {
                return path;
            }

            public Directory[] getChildren() {
                return new Directory[0];
            }
        };
    }
}
