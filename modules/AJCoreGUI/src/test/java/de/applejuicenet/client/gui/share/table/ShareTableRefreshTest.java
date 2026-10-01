package de.applejuicenet.client.gui.share.table;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.entity.Share;
import org.junit.Test;

import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class ShareTableRefreshTest {
    private Share share(int id, String name) {
        return new Share() {
            public int getId() { return id; }
            public String getFilename() { return name; }
            public String getShortfilename() { return name.substring(name.lastIndexOf('/') + 1); }
            public long getSize() { return 1024; }
            public String getCheckSum() { return "hash"; }
            public int getPrioritaet() { return 1; }
            public long getAskCount() { return 0; }
            public long getLastAsked() { return 0; }
            public long getSearchCount() { return 0; }
            public String getAjfspLink() { return "ajfsp://file|" + getShortfilename() + "|hash|1024/"; }
        };
    }

    @Test
    public void refreshRebuildsTreeAndNotifiesSwingOnEventThread() throws Exception {
        String oldSeparator = ApplejuiceFassade.separator;
        ApplejuiceFassade.separator = "/";
        try {
            SwingUtilities.invokeAndWait(() -> {
                ShareTableModel model = new ShareTableModel(new ShareNode(null, null));
                AtomicInteger changes = new AtomicInteger();
                model.addTreeModelListener(new TreeModelListener() {
                    public void treeNodesChanged(TreeModelEvent e) { }
                    public void treeNodesInserted(TreeModelEvent e) { }
                    public void treeNodesRemoved(TreeModelEvent e) { }
                    public void treeStructureChanged(TreeModelEvent e) {
                        assertTrue(SwingUtilities.isEventDispatchThread());
                        assertSame(model.getRoot(), e.getTreePath().getLastPathComponent());
                        changes.incrementAndGet();
                    }
                });
                model.setShares(Collections.singletonList(share(1, "/old/file.bin")));
                ShareNode oldRoot = model.getRootNode();
                assertEquals("old", model.getChild(oldRoot, 0).toString());
                model.setShares(Collections.singletonList(share(1, "/new/file.bin")));
                assertNotSame(oldRoot, model.getRootNode());
                assertEquals(1, model.getChildCount(model.getRoot()));
                assertEquals("new", model.getChild(model.getRoot(), 0).toString());
                model.setShares(Collections.emptyList());
                assertEquals(0, model.getChildCount(model.getRoot()));
                assertEquals(3, changes.get());
            });
        } finally {
            ApplejuiceFassade.separator = oldSeparator;
        }
    }

    @Test
    public void addingDirectoryInvalidatesSortedChildren() {
        String oldSeparator = ApplejuiceFassade.separator;
        ApplejuiceFassade.separator = "/";
        try {
            ShareNode root = new ShareNode(null, null);
            root.addChild(share(1, "/first/file.bin"));
            assertEquals(1, root.getChildren().length);
            root.addChild(share(2, "/second/file.bin"));
            assertEquals(2, root.getChildren().length);
        } finally {
            ApplejuiceFassade.separator = oldSeparator;
        }
    }
}
