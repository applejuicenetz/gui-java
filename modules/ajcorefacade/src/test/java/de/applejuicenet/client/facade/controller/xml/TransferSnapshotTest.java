package de.applejuicenet.client.facade.controller.xml;

import de.applejuicenet.client.facade.controller.CoreConnectionSettingsHolder;
import de.applejuicenet.client.facade.entity.Download;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.Assert.*;

public class TransferSnapshotTest {
    @Test
    public void equalDownloadsHaveEqualHashCodesAndWorkAsSetMembers() {
        DownloadDO first = new DownloadDO(7);
        DownloadDO sameId = new DownloadDO(7);
        DownloadDO other = new DownloadDO(8);
        assertEquals(first, sameId);
        assertEquals("equal objects need equal hash codes", first.hashCode(), sameId.hashCode());
        java.util.Set<Download> set = new java.util.HashSet<>();
        set.add(first);
        assertTrue("a download with the same id must be found in a hash set", set.contains(sameId));
        assertFalse(set.contains(other));
        assertEquals("a snapshot is still the same download", first.hashCode(), first.snapshot().hashCode());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void otherSnapshotsDetachMutableValues() throws Exception {
        ModifiedXMLHolder holder = new ModifiedXMLHolder(
                new CoreConnectionSettingsHolder("localhost", 9851, "", true), null);
        UploadDO upload = new UploadDO(1);
        upload.setSpeed(100);
        ServerDO server = new ServerDO(2);
        server.setName("before");
        SearchDO search = new SearchDO(3);
        search.setSearchText("before");
        SearchDO.SearchEntryDO entry = search.new SearchEntryDO(4, 3, "hash", 123);
        SearchDO.SearchEntryDO.FileNameDO name = entry.new FileNameDO("file.txt", 1);
        entry.addFileName(name);
        search.addSearchEntry(entry);
        for (Object[] pair : new Object[][]{{"uploadMap", upload}, {"serverMap", server}, {"searchMap", search}}) {
            Field field = ModifiedXMLHolder.class.getDeclaredField((String) pair[0]);
            field.setAccessible(true);
            ((Map<Integer, Object>) field.get(holder)).put(1, pair[1]);
        }
        var uploads = holder.getUploads();
        var servers = holder.getServer();
        var searches = holder.getSearchs();
        upload.setSpeed(200);
        server.setName("after");
        search.setSearchText("after");
        name.setFrequency(2);
        assertEquals(100, uploads.get(1).getSpeed());
        assertEquals("before", servers.get(1).getName());
        assertEquals("before", searches.get(1).getSearchText());
        assertEquals(1, searches.get(1).getAllSearchEntries().get(0).getFileNames()[0].getFrequency());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void downloadSnapshotDetachesMapAndNestedSources() throws Exception {
        ModifiedXMLHolder holder = new ModifiedXMLHolder(
                new CoreConnectionSettingsHolder("localhost", 9851, "", true), null);
        Field field = ModifiedXMLHolder.class.getDeclaredField("downloadMap");
        field.setAccessible(true);
        Map<Integer, Download> live = (Map<Integer, Download>) field.get(holder);
        DownloadDO download = new DownloadDO(1);
        download.setReady(10);
        DownloadSourceDO source = new DownloadSourceDO(2);
        source.setSpeed(100);
        download.addSource(source);
        live.put(1, download);

        Map<Integer, Download> snapshot = holder.getDownloads();
        download.setReady(20);
        source.setSpeed(200);
        live.clear();

        assertEquals(1, snapshot.size());
        assertEquals(10, snapshot.get(1).getReady());
        assertEquals(100, snapshot.get(1).getSources()[0].getSpeed());
    }
}
