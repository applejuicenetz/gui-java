package de.applejuicenet.client.gui.share;

import de.applejuicenet.client.fassade.entity.Share;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ShareListFormatTest {
    @Test
    public void ajfspListHasOneSortedLinkPerLine() {
        Share[] shares = {share("b.bin", "h2", 2), share("A.bin", "h1", 1)};
        assertEquals("ajfsp://file|A.bin|h1|1/\r\najfsp://file|b.bin|h2|2/", ShareListFormat.ajfsp(shares));
    }

    @Test
    public void ajlListHasHeaderAndSortedTriples() {
        Share[] shares = {share("b.bin", "h2", 2), share("A.bin", "h1", 1)};
        String ajl = ShareListFormat.ajl(shares);
        assertTrue(ajl.contains("Diese Datei darf nicht modifiziert werden!\r\n-----\r\n100\r\n"));
        assertTrue(ajl.endsWith("100\r\nA.bin\r\nh1\r\n1\r\nb.bin\r\nh2\r\n2\r\n"));
    }

    @Test
    public void descendingClipboardFormatsFollowTitleOrder() {
        Share[] shares = {share("A.bin", "h1", 1), share("b.bin", "h2", 2)};
        assertEquals("ajfsp://file|b.bin|h2|2/\r\najfsp://file|A.bin|h1|1/",
                ShareListFormat.ajfsp(shares, true));
        assertTrue(ShareListFormat.ajl(shares, true)
                .endsWith("100\r\nb.bin\r\nh2\r\n2\r\nA.bin\r\nh1\r\n1\r\n"));
    }

    @Test
    public void emptyListGivesEmptyAjfsp() {
        assertEquals("", ShareListFormat.ajfsp(new Share[0]));
    }

    @Test
    public void fileExportSortingFollowsDescendingSetting() throws Exception {
        de.applejuicenet.client.gui.controller.LanguageSelector.getInstance(
                java.nio.file.Paths.get("../../resources/language/deutsch.properties")
                        .toAbsolutePath().normalize().toString());
        javax.swing.SwingUtilities.invokeAndWait(() -> {
            DateiListeDialog dialog = new DateiListeDialog(null, false);
            try {
                java.lang.reflect.Field field = DateiListeDialog.class.getDeclaredField("table");
                field.setAccessible(true);
                javax.swing.JTable table = (javax.swing.JTable) field.get(dialog);
                ((de.applejuicenet.client.gui.share.table.DateiListeTableModel) table.getModel()).setDescending(true);
                DateiListeDialog.SpeichernMouseAdapter adapter = dialog.new SpeichernMouseAdapter();
                java.lang.reflect.Method method = adapter.getClass().getDeclaredMethod("sortShares", Share[].class);
                method.setAccessible(true);
                Share[] sorted = (Share[]) method.invoke(adapter,
                        (Object) new Share[]{share("A.bin", "h1", 1), share("b.bin", "h2", 2)});
                assertEquals("b.bin", sorted[0].getShortfilename());
                assertEquals("A.bin", sorted[1].getShortfilename());
            } catch (ReflectiveOperationException ex) {
                throw new AssertionError(ex);
            } finally {
                dialog.dispose();
            }
        });
    }

    private static Share share(String name, String hash, long size) {
        return new Share() {
            public int getId() { return 0; }
            public String getFilename() { return name; }
            public String getShortfilename() { return name; }
            public long getSize() { return size; }
            public String getCheckSum() { return hash; }
            public int getPrioritaet() { return 1; }
            public long getAskCount() { return 0; }
            public long getLastAsked() { return 0; }
            public long getSearchCount() { return 0; }
            public String getAjfspLink() { return "ajfsp://file|" + name + "|" + hash + "|" + size + "/"; }
        };
    }
}
