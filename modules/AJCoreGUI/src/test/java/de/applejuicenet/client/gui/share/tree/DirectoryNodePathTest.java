package de.applejuicenet.client.gui.share.tree;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DirectoryNodePathTest {
    @Test
    public void sameDirectoryIgnoresCaseAndTrailingSeparators() {
        assertTrue(DirectoryNode.sameDirectory("/home/user/incoming", "/home/USER/incoming/"));
        assertTrue(DirectoryNode.sameDirectory("C:\\Downloads", "C:\\Downloads\\"));
        assertFalse(DirectoryNode.sameDirectory("/home/user/incoming/sub", "/home/user/incoming"));
        assertFalse(DirectoryNode.sameDirectory(null, "/home/user/incoming"));
        assertFalse(DirectoryNode.sameDirectory("/home/user/incoming", null));
        assertFalse(DirectoryNode.sameDirectory("/home/user/incoming", ""));
    }

    @Test
    public void rootPathsAreRecognised() {
        assertTrue(DirectoryNode.isRootPath("/"));
        assertTrue(DirectoryNode.isRootPath("C:"));
        assertTrue(DirectoryNode.isRootPath("C:\\"));
        assertTrue(DirectoryNode.isRootPath("d:/"));
        assertFalse(DirectoryNode.isRootPath("/home"));
        assertFalse(DirectoryNode.isRootPath("C:\\Users"));
        assertFalse(DirectoryNode.isRootPath(""));
        assertFalse(DirectoryNode.isRootPath(null));
    }
}
