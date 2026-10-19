package de.applejuicenet.client.gui.share;

import de.applejuicenet.client.facade.entity.Share;
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
        assertTrue(ajl.contains("Do not modify this file!\r\n-----\r\n100\r\n"));
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
            FileListDialog dialog = new FileListDialog(null, false);
            try {
                java.lang.reflect.Field field = FileListDialog.class.getDeclaredField("table");
                field.setAccessible(true);
                javax.swing.JTable table = (javax.swing.JTable) field.get(dialog);
                ((de.applejuicenet.client.gui.share.table.FileListTableModel) table.getModel()).setDescending(true);
                FileListDialog.SaveMouseAdapter adapter = dialog.new SaveMouseAdapter();
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

    @Test
    public void ajlRoundTripsThroughTheReader() throws Exception {
        Share[] shares = {share("b.bin", "h2", 2), share("A.bin", "h1", 1)};
        java.io.File file = java.io.File.createTempFile("share", ".ajl");
        try {
            java.nio.file.Files.writeString(file.toPath(), ShareListFormat.ajl(shares),
                    java.nio.charset.StandardCharsets.UTF_8);
            assertEquals(java.util.List.of("ajfsp://file|A.bin|h1|1/", "ajfsp://file|b.bin|h2|2/"),
                    de.applejuicenet.client.shared.AjlFile.readLinks(file));
        } finally {
            file.delete();
        }
    }

    @Test
    public void lineBreaksInNamesDoNotShiftTheEntryLayout() throws Exception {
        Share[] shares = {share("a\r\nb\nc.bin", "h1", 1), share("z.bin", "h2", 2)};
        java.io.File file = java.io.File.createTempFile("share", ".ajl");
        try {
            java.nio.file.Files.writeString(file.toPath(), ShareListFormat.ajl(shares),
                    java.nio.charset.StandardCharsets.UTF_8);
            assertEquals(java.util.List.of("ajfsp://file|a b c.bin|h1|1/", "ajfsp://file|z.bin|h2|2/"),
                    de.applejuicenet.client.shared.AjlFile.readLinks(file));
        } finally {
            file.delete();
        }
    }

    @Test
    public void entriesWithoutNameOrChecksumAreSkipped() {
        Share[] shares = {share(null, "h0", 1), share("   ", "h0", 1), share("x.bin", null, 1),
                share("y.bin", " ", 1), null, share("ok.bin", "h9", 9)};
        String ajl = ShareListFormat.ajl(shares);
        assertTrue(ajl.endsWith("100\r\nok.bin\r\nh9\r\n9\r\n"));
        assertEquals("ajfsp://file|ok.bin|h9|9/", ShareListFormat.ajfsp(new Share[]{share("ok.bin", "h9", 9), null})
                .replace("\r\n", ""));
    }

    @Test
    public void emptyListGivesHeaderOnly() {
        String ajl = ShareListFormat.ajl(new Share[0]);
        assertTrue(ajl.endsWith("-----\r\n100\r\n"));
    }

    @Test
    public void singleLineReplacesAllLineBreakKinds() {
        assertEquals("a b c d", ShareListFormat.singleLine("a\r\nb\rc\nd"));
        assertEquals("plain", ShareListFormat.singleLine("  plain  "));
    }

    private static Share share(String name, String hash, long size) {
        return new Share() {
            public int getId() { return 0; }
            public String getFilename() { return name; }
            public String getShortfilename() { return name; }
            public long getSize() { return size; }
            public String getCheckSum() { return hash; }
            public int getPriority() { return 1; }
            public long getAskCount() { return 0; }
            public long getLastAsked() { return 0; }
            public long getSearchCount() { return 0; }
            public String getAjfspLink() { return "ajfsp://file|" + name + "|" + hash + "|" + size + "/"; }
        };
    }
}
