package de.applejuicenet.client.gui.download;

import de.applejuicenet.client.fassade.entity.*;
import de.applejuicenet.client.gui.components.table.TableSorter;
import de.applejuicenet.client.gui.download.table.DownloadsTableModel;
import de.applejuicenet.client.gui.download.table.DownloadSourcesTableModel;
import de.applejuicenet.client.shared.tablecellrenderer.SizeTableCellRenderer;
import org.junit.Test;
import org.junit.BeforeClass;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import java.nio.file.Paths;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;

import static org.junit.Assert.*;

public class LargeFileGuiTest {
    private static final long GIB = 1L << 30;

    @BeforeClass
    public static void loadLanguage() {
        LanguageSelector.getInstance(Paths.get("../../resources/language/deutsch.properties").toAbsolutePath().normalize().toString());
    }

    private Object entity(String type, int id) throws Exception {
        Class<?> clazz = Class.forName("de.applejuicenet.client.fassade.controller.xml." + type);
        Constructor<?> constructor = clazz.getDeclaredConstructor(int.class);
        constructor.setAccessible(true);
        return constructor.newInstance(id);
    }

    private void set(Object object, String method, Class<?> type, Object value) throws Exception {
        Method setter = object.getClass().getDeclaredMethod(method, type);
        setter.setAccessible(true);
        setter.invoke(object, value);
    }

    private DownloadSource source(long from, long to, long ready) throws Exception {
        DownloadSource source = (DownloadSource) entity("DownloadSourceDO", 3);
        set(source, "setStatus", int.class, DownloadSource.UEBERTRAGUNG);
        set(source, "setDownloadFrom", long.class, from);
        set(source, "setDownloadTo", long.class, to);
        set(source, "setActualDownloadPosition", long.class, ready);
        return source;
    }

    @Test
    public void sizeRendererAcceptsLongAndLegacyIntegerValues() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            SizeTableCellRenderer renderer = new SizeTableCellRenderer();
            JLabel label = (JLabel) renderer.getTableCellRendererComponent(new JTable(), 5 * GIB, false, false, 0, 0);
            assertEquals("5,0 GB", label.getText());
            label = (JLabel) renderer.getTableCellRendererComponent(new JTable(), 1024, false, false, 0, 0);
            assertEquals("1,0 KB", label.getText());
            assertEquals("1,0 TB", renderer.parseGroesse(1L << 40));
            assertEquals("8,0 EB", renderer.parseGroesse(Long.MAX_VALUE));
        });
    }

    @Test
    public void downloadTablesExposeLongByteColumns() throws Exception {
        Download download = (Download) entity("DownloadDO", 1);
        set(download, "setGroesse", long.class, 5 * GIB);
        set(download, "setReady", long.class, 3 * GIB);
        DownloadsTableModel downloads = new DownloadsTableModel();
        downloads.setDownloads(Collections.singletonMap(1, download));
        assertEquals(Long.class, downloads.getColumnClass(2));
        assertEquals(Long.class, downloads.getColumnClass(3));
        assertEquals(Long.class, downloads.getColumnClass(7));
        assertEquals(5 * GIB, downloads.getValueAt(0, 2));
        assertEquals(3 * GIB, downloads.getValueAt(0, 3));
        assertEquals(2 * GIB, downloads.getValueAt(0, 7));
        set(download, "setStatus", int.class, Download.FERTIG);
        assertEquals(0L, downloads.getValueAt(0, 7));

        DownloadSource source = source(3 * GIB, 7 * GIB, 4 * GIB);
        Method add = download.getClass().getDeclaredMethod("addSource", DownloadSource.class);
        add.setAccessible(true);
        add.invoke(download, source);
        DownloadSourcesTableModel sources = new DownloadSourcesTableModel();
        sources.setDownload(download);
        assertEquals(Long.class, sources.getColumnClass(3));
        assertEquals(Long.class, sources.getColumnClass(4));
        assertEquals(Long.class, sources.getColumnClass(8));
        assertEquals(4 * GIB, sources.getValueAt(0, 3));
        assertEquals(GIB, sources.getValueAt(0, 4));
        assertEquals(3 * GIB, sources.getValueAt(0, 8));
    }

    private Part part(long from, int type) {
        return new Part() {
            public long getFromPosition() { return from; }
            public int getType() { return type; }
        };
    }

    @Test
    public void partGraphicRendersLargeCompletedRangesAndSourceOffsets() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            try {
                for (long size : new long[]{5 * GIB, 1L << 50}) {
                    DownloadSource source = source(size / 2, size, size * 3 / 4);
                    PartList list = new PartList() {
                        public int getPartListType() { return SOURCE_PARTLIST; }
                        public long getGroesse() { return size; }
                        public Part[] getParts() { return new Part[]{part(0, -1), part(size / 2, 0)}; }
                        public Object getValueObject() { return source; }
                    };
                    DownloadPartListPanel panel = DownloadPartListPanel.getInstance();
                    panel.setSize(100, 30);
                    panel.setPartList(list, size == 5 * GIB ? 1 : 2);
                    Field imageField = DownloadPartListPanel.class.getDeclaredField("image");
                    imageField.setAccessible(true);
                    BufferedImage image = (BufferedImage) imageField.get(panel);
                    assertNotNull(image);
                    assertEquals(PartList.COLOR_TYPE_OK.getRGB(), image.getRGB(20, 5));
                    assertEquals(PartList.COLOR_READY_70.getRGB(), image.getRGB(50, 20));
                }
            } catch (Exception e) {
                throw new AssertionError(e);
            }
        });
    }

    @Test
    public void verifiedPartsBeyondIntBoundaryRenderAtCorrectPosition() throws Exception {
        Download download = (Download) entity("DownloadDO", 1);
        set(download, "setStatus", int.class, Download.PAUSIERT);
        PartList list = new PartList() {
            public int getPartListType() { return MAIN_PARTLIST; }
            public long getGroesse() { return 5 * GIB; }
            public Part[] getParts() { return new Part[]{part(0, -1), part(3 * GIB, 0), part(4 * GIB, 1)}; }
            public Object getValueObject() { return download; }
        };
        SwingUtilities.invokeAndWait(() -> {
            try {
                DownloadPartListPanel panel = DownloadPartListPanel.getInstance();
                panel.setSize(100, 30);
                panel.setPartList(list, 5);
                Field imageField = DownloadPartListPanel.class.getDeclaredField("image");
                imageField.setAccessible(true);
                BufferedImage image = (BufferedImage) imageField.get(panel);
                assertNotNull(image);
                assertEquals(PartList.COLOR_TYPE_UEBERPRUEFT.getRGB(), image.getRGB(80, 5));
                assertEquals(PartList.COLOR_TYPE_0.getRGB(), image.getRGB(40, 20));
                assertEquals(PartList.COLOR_TYPE_1.getRGB(), image.getRGB(80, 20));
            } catch (Exception e) {
                throw new AssertionError(e);
            }
        });
    }

    @Test
    public void longSizesSortWithoutLosingPrecision() {
        TableSorter<Download> sorter = new TableSorter<>(new DownloadsTableModel());
        assertTrue(sorter.compare(Long.MAX_VALUE - 1, Long.MAX_VALUE) < 0);
        assertTrue(sorter.compare(5 * GIB, 3 * GIB) > 0);
    }
}
