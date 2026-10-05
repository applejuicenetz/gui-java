package de.applejuicenet.client.gui.share.table;

import de.applejuicenet.client.fassade.entity.Share;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DateiListeSortTest {
    @Test
    public void sortsAscendingByDefaultAndDescendingOnDemand() {
        DateiListeTableModel model = model("b.bin", "C.bin", "a.bin");
        assertFalse(model.isDescending());
        assertEquals("a.bin", model.getValueAt(0, 0));
        assertEquals("C.bin", model.getValueAt(2, 0));

        model.setDescending(true);
        assertTrue(model.isDescending());
        assertEquals("C.bin", model.getValueAt(0, 0));
        assertEquals("a.bin", model.getValueAt(2, 0));

        model.setDescending(false);
        assertEquals("a.bin", model.getValueAt(0, 0));
    }

    @Test
    public void descendingOrderSurvivesAddAndRemove() {
        DateiListeTableModel model = model("a.bin", "b.bin");
        model.setDescending(true);
        ShareNode extra = new ShareNode(null, null);
        extra.addChild(share(9, "z.bin"));
        model.addNodes(extra);
        assertEquals("z.bin", model.getValueAt(0, 0));
        model.removeRow(0);
        assertEquals("b.bin", model.getValueAt(0, 0));
    }

    private static DateiListeTableModel model(String... names) {
        de.applejuicenet.client.fassade.ApplejuiceFassade.separator = "/";
        DateiListeTableModel model = new DateiListeTableModel();
        ShareNode root = new ShareNode(null, null);
        for (int i = 0; i < names.length; i++) {
            root.addChild(share(i, names[i]));
        }
        model.addNodes(root);
        return model;
    }

    private static Share share(int id, String name) {
        return new Share() {
            public int getId() { return id; }
            public String getFilename() { return name; }
            public String getShortfilename() { return name; }
            public long getSize() { return 1; }
            public String getCheckSum() { return "h"; }
            public int getPrioritaet() { return 1; }
            public long getAskCount() { return 0; }
            public long getLastAsked() { return 0; }
            public long getSearchCount() { return 0; }
            public String getAjfspLink() { return ""; }
        };
    }
}
