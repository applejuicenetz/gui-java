package de.applejuicenet.client.gui.share.tree;

import org.junit.Test;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.List;
import java.util.function.Function;

import static org.junit.Assert.*;

public class ShareTreeLocatorTest {
    @Test
    public void buildsPrefixesForPosixAndWindowsPaths() {
        assertEquals(List.of("/", "/home", "/home/me", "/home/me/incoming"),
                ShareTreeLocator.prefixes("/home/me/incoming", "/"));
        assertEquals(List.of("C:\\", "C:\\Users", "C:\\Users\\me", "C:\\Users\\me\\incoming"),
                ShareTreeLocator.prefixes("C:\\Users\\me\\incoming", "\\"));
        assertTrue(ShareTreeLocator.prefixes("", "/").isEmpty());
        assertTrue(ShareTreeLocator.prefixes(null, "/").isEmpty());
    }

    @Test
    public void locatesDeepestExistingAncestor() {
        TreeModel model = model();
        TreePath path = ShareTreeLocator.locate(model, "/home/me/incoming", "/", ShareTreeLocatorTest::path);
        assertNotNull(path);
        assertEquals(5, path.getPathCount());
        assertEquals("/home/me/incoming", path.getLastPathComponent().toString());
    }

    @Test
    public void stopsAtMissingSegmentAndIgnoresCase() {
        TreeModel model = model();
        TreePath path = ShareTreeLocator.locate(model, "/HOME/ME/unknown/deeper", "/", ShareTreeLocatorTest::path);
        assertNotNull(path);
        assertEquals(4, path.getPathCount());
        assertEquals("/home/me", path.getLastPathComponent().toString());
        TreePath root = ShareTreeLocator.locate(model, "/elsewhere", "/", ShareTreeLocatorTest::path);
        assertEquals("/", root.getLastPathComponent().toString());
        assertNull(ShareTreeLocator.locate(model, "Z:\\elsewhere", "\\", ShareTreeLocatorTest::path));
    }

    private static String path(Object node) {
        return node.toString();
    }

    private static TreeModel model() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
        DefaultMutableTreeNode slash = new DefaultMutableTreeNode("/");
        root.add(slash);
        DefaultMutableTreeNode home = new DefaultMutableTreeNode("/home");
        slash.add(home);
        DefaultMutableTreeNode me = new DefaultMutableTreeNode("/home/me");
        home.add(me);
        DefaultMutableTreeNode incoming = new DefaultMutableTreeNode("/home/me/incoming");
        me.add(incoming);
        me.add(new DefaultMutableTreeNode("/home/me/temp"));
        return new DefaultTreeModel(root);
    }
}
