package de.applejuicenet.client.gui.share.tree;

import de.applejuicenet.client.facade.entity.Directory;
import de.applejuicenet.client.facade.entity.ShareEntry;
import org.junit.After;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;

public class DirectoryNodeShareModeTest {
    @After
    public void clearShareDirs() {
        DirectoryNode.setShareDirs(Set.of());
    }

    @Test
    public void driveRootShareMatchesTheDriveNode() {
        DirectoryNode.setShareDirs(Set.of(entry("C:", ShareEntry.SHAREMODE.SUBDIRECTORY)));

        assertEquals(DirectoryNode.SHARED_WITH_SUB, new DirectoryNode(null, directory("C:\\")).getShareMode());
    }

    @Test
    public void filesystemRootShareMatchesTheRootNode() {
        DirectoryNode.setShareDirs(Set.of(entry("/", ShareEntry.SHAREMODE.SUBDIRECTORY)));

        assertEquals(DirectoryNode.SHARED_WITH_SUB, new DirectoryNode(null, directory("/")).getShareMode());
    }

    @Test
    public void trailingSeparatorInTheShareEntryStillMatches() {
        DirectoryNode.setShareDirs(Set.of(entry("/home/user/", ShareEntry.SHAREMODE.SINGLEDIRECTORY)));

        assertEquals(DirectoryNode.SHARED_WITHOUT_SUB, new DirectoryNode(null, directory("/home/user")).getShareMode());
    }

    @Test
    public void subFoldersInheritTheSharedState() {
        DirectoryNode.setShareDirs(Set.of(entry("/home/user", ShareEntry.SHAREMODE.SUBDIRECTORY)));
        DirectoryNode parent = new DirectoryNode(null, directory("/home/user"));

        assertEquals(DirectoryNode.SHARED_WITH_SUB, parent.getShareMode());
        assertEquals(DirectoryNode.SHARED_SUB, new DirectoryNode(parent, directory("/home/user/sub")).getShareMode());
    }

    @Test
    public void folderAboveAShareIsMarkedAsPartlyShared() {
        DirectoryNode.setShareDirs(Set.of(entry("/home/user/files", ShareEntry.SHAREMODE.SUBDIRECTORY)));

        assertEquals(DirectoryNode.SHARED_SOMETHING, new DirectoryNode(null, directory("/home/user")).getShareMode());
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
