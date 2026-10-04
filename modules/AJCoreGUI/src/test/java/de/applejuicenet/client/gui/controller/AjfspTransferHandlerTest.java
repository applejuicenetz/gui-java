package de.applejuicenet.client.gui.controller;

import org.junit.Test;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class AjfspTransferHandlerTest {
    private static final String FILE_LINK = "ajfsp://file|example.bin|checksum|1234/";
    private static final String SERVER_LINK = "ajfsp://server|example.org|2002";

    @Test
    public void readsFileAndServerLinks() {
        assertEquals(List.of(FILE_LINK, SERVER_LINK), AjfspTransferHandler.readLinks(
                new StringSelection(FILE_LINK + "\r\n" + SERVER_LINK)));
    }

    @Test
    public void normalizesBrowserLinks() {
        assertEquals(List.of(FILE_LINK), AjfspTransferHandler.readLinks(new StringSelection(
                " web+" + FILE_LINK.replace("|", "%7c") + " ")));
    }

    @Test
    public void ignoresUnrelatedAndIncompleteLinks() {
        assertTrue(AjfspTransferHandler.readLinks(new StringSelection(
                "https://example.org\najfsp://file|incomplete\najfsp://other|value|123")).isEmpty());
    }

    @Test
    public void readsUriListAndDeduplicatesFlavors() throws Exception {
        DataFlavor uriList = new DataFlavor("text/uri-list;class=java.io.Reader");
        Transferable transferable = new Transferable() {
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[]{uriList, DataFlavor.stringFlavor};
            }

            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return flavor.equals(uriList) || flavor.equals(DataFlavor.stringFlavor);
            }

            public Object getTransferData(DataFlavor flavor) {
                return flavor.equals(uriList) ? new StringReader("# Browser link\n" + FILE_LINK) : FILE_LINK;
            }
        };
        assertEquals(List.of(FILE_LINK), AjfspTransferHandler.readLinks(transferable));
    }

    @Test
    public void installsOnExistingAndNewChildrenAndImportsLinks() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JPanel panel = new JPanel();
            JTextField existing = new JTextField();
            panel.add(existing);
            List<String> imported = new ArrayList<>();
            AjfspTransferHandler.install(panel, imported::add);
            JPanel added = new JPanel();
            JTextField addedField = new JTextField();
            added.add(addedField);
            panel.add(added);
            assertTrue(existing.getTransferHandler() instanceof AjfspTransferHandler);
            assertTrue(addedField.getTransferHandler() instanceof AjfspTransferHandler);
            AjfspTransferHandler handler = (AjfspTransferHandler) addedField.getTransferHandler();
            assertTrue(handler.importLinks(new StringSelection(FILE_LINK + "\n" + SERVER_LINK)));
            assertEquals(List.of(FILE_LINK, SERVER_LINK), imported);
            assertFalse(handler.importLinks(new StringSelection("plain text")));
        });
    }

    @Test
    public void preservesTextPasteAndCopy() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JTextField field = new JTextField();
            List<String> imported = new ArrayList<>();
            AjfspTransferHandler.install(field, imported::add);
            TransferHandler handler = field.getTransferHandler();
            assertTrue(handler.importData(new TransferHandler.TransferSupport(
                    field, new StringSelection(FILE_LINK))));
            assertEquals(FILE_LINK, field.getText());
            assertTrue(imported.isEmpty());
            field.selectAll();
            Clipboard clipboard = new Clipboard("test");
            handler.exportToClipboard(field, clipboard, TransferHandler.COPY);
            try {
                assertEquals(FILE_LINK, clipboard.getData(DataFlavor.stringFlavor));
            } catch (Exception exception) {
                throw new AssertionError(exception);
            }
            assertTrue(handler.canImport(field, new DataFlavor[]{DataFlavor.stringFlavor}));
            field.selectAll();
            assertTrue(handler.importData(field, new StringSelection("replacement")));
            assertEquals("replacement", field.getText());
        });
    }
}
