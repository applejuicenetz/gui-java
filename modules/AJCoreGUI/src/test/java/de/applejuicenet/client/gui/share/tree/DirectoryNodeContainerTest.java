package de.applejuicenet.client.gui.share.tree;

import de.applejuicenet.client.facade.entity.Directory;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DirectoryNodeContainerTest {
    @Test
    public void desktopAndThisPcAreContainers() {
        assertTrue(node(Directory.TYPE_DESKTOP, "C:\\Users\\me\\Desktop").isContainer());
        assertTrue(node(Directory.TYPE_FOLDER, "::{20D04FE0-3AEA-1069-A2D8-08002B30309D}").isContainer());
        assertTrue(node(Directory.TYPE_FOLDER, "::{20d04fe0-3aea-1069-a2d8-08002b30309d}").isContainer());
    }

    @Test
    public void otherNodesAreNot() {
        // the core reports the first entry below the desktop (e.g. "Documents") as a computer node
        assertFalse(node(Directory.TYPE_COMPUTER, "C:\\Users\\me\\Documents").isContainer());
        assertFalse(node(Directory.TYPE_DRIVE, "C:\\").isContainer());
        assertFalse(node(Directory.TYPE_FOLDER, "::{F02C1A0D-BE21-4350-88B0-7367FC96EF3C}").isContainer());
        assertFalse(node(Directory.TYPE_FOLDER, "/home/me").isContainer());
        assertFalse(new DirectoryNode(null, null).isContainer());
    }

    private static DirectoryNode node(int type, String path) {
        return new DirectoryNode(null, new Directory() {
            public String getName() { return path; }
            public int getType() { return type; }
            public boolean isFileSystem() { return !path.startsWith("::"); }
            public String getPath() { return path; }
            public Directory[] getChildren() { return new Directory[0]; }
        });
    }
}
