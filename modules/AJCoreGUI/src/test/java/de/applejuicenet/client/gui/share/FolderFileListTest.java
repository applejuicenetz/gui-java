package de.applejuicenet.client.gui.share;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.entity.Share;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.share.table.DateiListeTableModel;
import de.applejuicenet.client.gui.share.table.ShareNode;
import de.applejuicenet.client.gui.share.table.ShareTable;
import de.applejuicenet.client.gui.share.table.ShareTableModel;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class FolderFileListTest {
    @BeforeClass
    public static void loadLanguage() {
        LanguageSelector.getInstance(Paths.get("../../resources/language/deutsch.properties")
                .toAbsolutePath().normalize().toString());
    }

    @Test
    public void folderDialogContainsNestedFilesAndOpensInsideRightEdge() throws Exception {
        String previousSeparator = ApplejuiceFassade.separator;
        ApplejuiceFassade.separator = "/";
        try {
            SwingUtilities.invokeAndWait(() -> {
                ShareNode root = root();
                ShareNode folder = (ShareNode) root.getChildrenMap().get("folder");
                JFrame owner = new JFrame();
                DateiListeDialog dialog = new DateiListeDialog(owner, false, folder);
                DateiListeDialog emptyDialog = new DateiListeDialog(owner, false);
                try {
                    DateiListeTableModel model = (DateiListeTableModel) findTable(dialog).getModel();
                    assertEquals(2, model.getRowCount());
                    assertEquals(Set.of("alpha.bin", "beta.bin"), Arrays.stream(model.getShares())
                            .map(Share::getShortfilename).collect(Collectors.toSet()));
                    assertEquals(0, findTable(emptyDialog).getRowCount());
                    assertNotNull(findTable(dialog).getDropTarget());
                    owner.setBounds(40, 60, 900, 600);
                    owner.setVisible(true);
                    dialog.setSize(300, 200);
                    dialog.setVisible(true);
                    assertEquals(new Point(640, 260), dialog.getLocation());
                } finally {
                    emptyDialog.dispose();
                    dialog.dispose();
                    owner.dispose();
                }
            });
        } finally {
            ApplejuiceFassade.separator = previousSeparator;
        }
    }

    @Test
    public void folderRightClickUsesFolderMenuAndFileUsesExistingMenu() throws Exception {
        String previousSeparator = ApplejuiceFassade.separator;
        ApplejuiceFassade.separator = "/";
        try {
            SwingUtilities.invokeAndWait(() -> {
                ShareTable table = new ShareTable(new ShareTableModel(root()));
                table.getTree().setRootVisible(false);
                table.getTree().expandRow(0);
                table.setSize(800, 300);
                RecordingPopup filePopup = new RecordingPopup();
                RecordingPopup folderPopup = new RecordingPopup();
                ShareTableMouseAdapter adapter = new ShareTableMouseAdapter(table, filePopup, folderPopup);
                click(adapter, table, 0);
                assertEquals(1, folderPopup.shown);
                assertEquals(0, filePopup.shown);
                int fileRow = -1;
                for (int row = 0; row < table.getRowCount(); row++) {
                    ShareNode node = (ShareNode) table.getTree().getPathForRow(row).getLastPathComponent();
                    if (node.isLeaf()) {
                        fileRow = row;
                        break;
                    }
                }
                assertTrue(fileRow >= 0);
                click(adapter, table, fileRow);
                assertEquals(1, folderPopup.shown);
                assertEquals(1, filePopup.shown);
                MouseEvent emptyArea = new MouseEvent(table, MouseEvent.MOUSE_PRESSED,
                        0, 0, 10, 290, 1, true, MouseEvent.BUTTON3);
                adapter.mousePressed(emptyArea);
                assertEquals(1, folderPopup.shown);
                assertEquals(1, filePopup.shown);
            });
        } finally {
            ApplejuiceFassade.separator = previousSeparator;
        }
    }

    private static void click(ShareTableMouseAdapter adapter, JTable table, int row) {
        Rectangle cell = table.getCellRect(row, 0, true);
        adapter.mousePressed(new MouseEvent(table, MouseEvent.MOUSE_PRESSED,
                0, 0, cell.x + 4, cell.y + 4, 1, true, MouseEvent.BUTTON3));
    }

    @Test
    public void fileListFormatsLargeSizesAndFitsColumnsWithoutChangingDragHandlers() throws Exception {
        String previousSeparator = ApplejuiceFassade.separator;
        ApplejuiceFassade.separator = "/";
        try {
            final DateiListeDialog[] dialog = new DateiListeDialog[1];
            final JTable[] table = new JTable[1];
            SwingUtilities.invokeAndWait(() -> {
                dialog[0] = new DateiListeDialog(null, false);
                table[0] = findTable(dialog[0]);
                table[0].setSize(800, 300);
                table[0].getParent().setSize(800, 300);
                ShareNode initial = new ShareNode(null, null);
                initial.addChild(share(10, "large.bin", 5L << 30));
                ((DateiListeTableModel) table[0].getModel()).addNodes(initial);
            });
            try {
                SwingUtilities.invokeAndWait(() -> {
                    JTable files = table[0];
                    assertEquals(Long.class, files.getColumnClass(1));
                    assertEquals(5L << 30, files.getValueAt(0, 1));
                    JLabel rendered = (JLabel) files.prepareRenderer(files.getCellRenderer(0, 1), 0, 1);
                    assertEquals("5,0 GB", rendered.getText());
                    int sizeWidth = files.getColumnModel().getColumn(1).getWidth();
                    assertTrue(sizeWidth >= rendered.getPreferredSize().width);
                    assertTrue(sizeWidth < 200);
                    assertEquals(files.getParent().getWidth() - sizeWidth,
                            files.getColumnModel().getColumn(0).getWidth());
                    assertNotNull(files.getDropTarget());
                    assertFalse(files.getDragEnabled());
                    ShareNode larger = new ShareNode(null, null);
                    larger.addChild(share(11, "larger.bin", 999L << 40));
                    ((DateiListeTableModel) files.getModel()).addNodes(larger);
                });
                SwingUtilities.invokeAndWait(() -> {
                    JTable files = table[0];
                    JLabel rendered = (JLabel) files.prepareRenderer(files.getCellRenderer(1, 1), 1, 1);
                    assertEquals("999,0 TB", rendered.getText());
                    assertTrue(files.getColumnModel().getColumn(1).getWidth() >= rendered.getPreferredSize().width);
                    assertEquals(files.getParent().getWidth(), files.getColumnModel().getColumn(0).getWidth()
                            + files.getColumnModel().getColumn(1).getWidth());
                });
            } finally {
                SwingUtilities.invokeAndWait(() -> dialog[0].dispose());
            }
        } finally {
            ApplejuiceFassade.separator = previousSeparator;
        }
    }

    private static JTable findTable(Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof JTable table) {
                return table;
            }
            if (component instanceof Container child) {
                JTable table = findTable(child);
                if (table != null) {
                    return table;
                }
            }
        }
        return null;
    }

    private static ShareNode root() {
        ShareNode root = new ShareNode(null, null);
        root.addChild(share(1, "folder/alpha.bin"));
        root.addChild(share(2, "folder/nested/beta.bin"));
        root.addChild(share(3, "other/ignored.bin"));
        return root;
    }

    private static Share share(int id, String name) {
        return share(id, name, 1024);
    }

    private static Share share(int id, String name, long size) {
        return new Share() {
            public int getId() { return id; }
            public String getFilename() { return name; }
            public String getShortfilename() { return name.substring(name.lastIndexOf('/') + 1); }
            public long getSize() { return size; }
            public String getCheckSum() { return "hash"; }
            public int getPrioritaet() { return 1; }
            public long getAskCount() { return 0; }
            public long getLastAsked() { return 0; }
            public long getSearchCount() { return 0; }
            public String getAjfspLink() { return "ajfsp://file|" + getShortfilename() + "|hash|1024/"; }
        };
    }

    private static class RecordingPopup extends JPopupMenu {
        private int shown;

        @Override
        public void show(Component invoker, int locationX, int locationY) {
            shown++;
        }
    }
}
