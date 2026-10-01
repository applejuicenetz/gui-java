package de.applejuicenet.client.fassade.controller.xml;

import de.applejuicenet.client.fassade.controller.CoreConnectionSettingsHolder;
import de.applejuicenet.client.fassade.event.DownloadDataPropertyChangeEvent;
import org.junit.Test;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import static org.junit.Assert.*;

public class LargeFileTest {
    private static final long GIB = 1L << 30;

    private CoreConnectionSettingsHolder settings() throws Exception {
        return new CoreConnectionSettingsHolder("localhost", 9851, "", true);
    }

    private Object parseObject(String method, String tag, String attributes) throws Exception {
        NodeList nodes = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new InputSource(new StringReader("<" + tag + " " + attributes + "/>")))
                .getElementsByTagName(tag);
        Method parser = GetObjectXMLHolder.class.getDeclaredMethod(method, NodeList.class);
        parser.setAccessible(true);
        return parser.invoke(new GetObjectXMLHolder(settings()), nodes);
    }

    private Map<String, String> attributes(String text) {
        Map<String, String> values = new HashMap<>();
        for (String pair : text.split(" ")) {
            String[] parts = pair.split("=", 2);
            values.put(parts[0], parts[1]);
        }
        return values;
    }

    @Test
    public void downloadObjectPreservesSizesAcrossIntBoundary() throws Exception {
        for (long size : new long[]{Integer.MAX_VALUE, (long) Integer.MAX_VALUE + 1, 5 * GIB}) {
            DownloadDO download = (DownloadDO) parseObject("getDownloadObject", "download",
                    "id='1' shareid='2' hash='hash' size='" + size + "' ready='" + (size - 1)
                    + "' status='0' filename='large.bin' targetdirectory='/tmp' powerdownload='0' temporaryfilenumber='1'");
            assertEquals(size, download.getGroesse());
            assertEquals(size - 1, download.getReady());
            assertTrue(download.getProzentGeladen() > 99);
        }
    }

    @Test
    public void sourceObjectPreservesOffsetsAndFractionalProgress() throws Exception {
        DownloadSourceDO source = (DownloadSourceDO) parseObject("getDownloadSourceObject", "user",
                "id='3' status='7' directstate='1' downloadfrom='3221225472' downloadto='7516192768' "
                + "actualdownloadposition='4294967296' speed='1024' version='0.0.0.0' queueposition='0' "
                + "powerdownload='0' filename='large.bin' nickname='peer' downloadid='1' source='2'");
        assertEquals(3 * GIB, source.getDownloadFrom());
        assertEquals(4 * GIB, source.getSize());
        assertEquals(GIB, source.getBereitsGeladen());
        assertEquals(3 * GIB, source.getNochZuLaden());
        assertEquals(25.0, source.getReadyPercent(), 0.001);
        source.setActualDownloadPosition(3 * GIB + 1);
        assertTrue(source.getReadyPercent() > 0);
    }

    @Test
    public void uploadObjectPreservesLargeRange() throws Exception {
        UploadDO upload = (UploadDO) parseObject("getUploadObject", "upload",
                "id='4' shareid='2' version='0.0.0.0' priority='0' nick='peer' status='1' "
                + "uploadfrom='3221225472' uploadto='7516192768' actualuploadposition='4294967296' "
                + "speed='1024' directstate='1' lastconnection='0' loaded='0.5'");
        assertEquals(3 * GIB, upload.getUploadFrom());
        assertEquals(4 * GIB, upload.getSize());
        assertEquals(25.0, upload.getDownloadPercent(), 0.001);
    }

    @Test
    public void modifiedDownloadPreservesReadyAndEventValues() throws Exception {
        ModifiedXMLHolder holder = new ModifiedXMLHolder(settings(), null);
        Method parser = ModifiedXMLHolder.class.getDeclaredMethod("checkDownloadMap", DownloadDO.class, Map.class, boolean.class);
        parser.setAccessible(true);
        DownloadDO download = new DownloadDO(1);
        Map<String, String> values = attributes("shareid=2 size=5368709120 hash=hash temporaryfilenumber=1 "
                + "status=0 filename=large.bin targetdirectory=/tmp powerdownload=0 ready=3221225472");
        parser.invoke(holder, download, values, true);
        assertEquals(5 * GIB, download.getGroesse());
        assertEquals(3 * GIB, download.getReady());
        values.put("ready", Long.toString(4 * GIB));
        parser.invoke(holder, download, values, false);
        assertEquals(4 * GIB, download.getReady());
        Field eventsField = ModifiedXMLHolder.class.getDeclaredField("downloadEvents");
        eventsField.setAccessible(true);
        Vector<?> events = (Vector<?>) eventsField.get(holder);
        DownloadDataPropertyChangeEvent event = (DownloadDataPropertyChangeEvent) events.lastElement();
        assertEquals(3 * GIB, event.getOldValue());
        assertEquals(4 * GIB, event.getNewValue());
    }

    @Test
    public void modifiedTransferOffsetsPreserved() throws Exception {
        ModifiedXMLHolder holder = new ModifiedXMLHolder(settings(), null);
        Method uploadParser = ModifiedXMLHolder.class.getDeclaredMethod("checkUploadMap", UploadDO.class, Map.class);
        uploadParser.setAccessible(true);
        UploadDO upload = new UploadDO(4);
        uploadParser.invoke(holder, upload, attributes("shareid=2 status=1 directstate=1 priority=0 "
                + "uploadfrom=3221225472 uploadto=7516192768 actualuploadposition=4294967296 "
                + "speed=1024 nick=peer lastconnection=0 loaded=0.5 version=0.0.0.0 operatingsystem=-1"));
        assertEquals(4 * GIB, upload.getActualUploadPosition());
        assertEquals(4 * GIB, upload.getSize());
        Method sourceParser = ModifiedXMLHolder.class.getDeclaredMethod("checkUserMap", DownloadSourceDO.class, Map.class);
        sourceParser.setAccessible(true);
        DownloadSourceDO source = new DownloadSourceDO(3);
        sourceParser.invoke(holder, source, attributes("status=7 directstate=1 downloadfrom=3221225472 "
                + "downloadto=7516192768 actualdownloadposition=4294967296 speed=1024 queueposition=0 "
                + "powerdownload=0 filename=large.bin nickname=peer version=0.0.0.0 source=2 operatingsystem=-1"));
        assertEquals(4 * GIB, source.getActualDownloadPosition());
        assertEquals(4 * GIB, source.getSize());
    }

    @Test
    public void totalLoadedAndRemainingTimeDoNotOverflow() {
        DownloadDO download = new DownloadDO(1);
        download.setGroesse(5 * GIB);
        download.setReady(3 * GIB);
        DownloadSourceDO source = new DownloadSourceDO(3);
        source.setDownloadFrom(3 * GIB);
        source.setDownloadTo(5 * GIB);
        source.setActualDownloadPosition(4 * GIB);
        source.setSpeed(1);
        download.addSource(source);
        assertEquals(4 * GIB, download.getBereitsGeladen());
        assertEquals(2 * GIB, download.getRestZeit());
        assertEquals("24855:03:14:08", download.getRestZeitAsString());
        download.setReady(4 * GIB);
        assertEquals("12427:13:37:04", download.getRestZeitAsString());
    }

    @Test
    public void unknownOrEmptyRangesHaveZeroProgress() {
        DownloadSourceDO source = new DownloadSourceDO(3);
        assertEquals(0.0, source.getReadyPercent(), 0.0);
        source.setDownloadFrom(-1);
        source.setDownloadTo(-1);
        source.setActualDownloadPosition(-1);
        assertEquals(0, source.getSize());
        assertEquals(0, source.getBereitsGeladen());
        assertEquals(0, source.getNochZuLaden());
        assertEquals(0.0, source.getReadyPercent(), 0.0);
        assertEquals(0.0, new UploadDO(4).getDownloadPercent(), 0.0);
        assertEquals(0.0, new DownloadDO(1).getProzentGeladen(), 0.0);
    }
}
